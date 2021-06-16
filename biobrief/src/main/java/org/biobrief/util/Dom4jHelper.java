package org.biobrief.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

//https://dom4j.github.io/
public final class Dom4jHelper
{	
	private static final Logger logger=LoggerFactory.getLogger(Dom4jHelper.class);
	private static final String ELEMENT_IS_NULL="element is null";
	
	private Dom4jHelper(){}
	
	public static Document parse(String xml)
	{
		if (!StringHelper.hasContent(xml))
			throw new CException("XML document is empty: ["+xml+"]");
		try
		{
			SAXReader reader = new SAXReader();
			return reader.read(new StringReader(xml));
		}
		catch(Exception  e)
		{
			throw new CException(e);
		}
	}
	
	public static String getAttribute(Element element, String name)
	{
		if (element==null)
			throw new CException(ELEMENT_IS_NULL);
		Attribute attribute=element.attribute(name);
		if (attribute==null)
			throw new CException("attribute "+name+" is null");
		String value=attribute.getValue();
		//logger.debug("attribute="+value);
		return fixEntities(value);
	}
	
	// optional default value
	public static String getAttribute(Element element, String name, Object dflt)
	{
		if (element==null)
			throw new CException(ELEMENT_IS_NULL);
		Attribute attribute=element.attribute(name);
		if (attribute==null)
		{
			if (dflt==null)
				return null;
			else return dflt.toString();
		}
		String value=attribute.getValue();
		//logger.debug("attribute="+value);
		return fixEntities(value);
	}
	
	public static Integer getIntAttribute(Element element, String name)
	{
		String value=getAttribute(element,name);
		return Integer.valueOf(value);
	}
	
	public static Integer getIntAttribute(Element element, String name, Integer dflt)
	{
		String value=getAttribute(element,name,dflt);
		if (value==null)
			return null;
		return Integer.valueOf(value);
	}
	
	public static Float getFloatAttribute(Element element, String name)
	{
		String value=getAttribute(element,name);
		return Float.valueOf(value);
	}
	
	public static Long getLongAttribute(Element element, String name)
	{
		String value=getAttribute(element,name);
		return Long.valueOf(value);
	}
	
	public static Boolean getBoolAttribute(Element element, String name)
	{
		String value=getAttribute(element,name);
		return Boolean.valueOf(value);
	}
	
	public static Boolean getBoolAttribute(Element element, String name, Boolean dflt)
	{
		String value=getAttribute(element,name,dflt);
		if (value==null)
			return null;
		return Boolean.valueOf(value);
	}

	public static Date getDateAttribute(Element element, String name, String pattern)
	{
		String value=getAttribute(element, name);
		return DateHelper.parse(value, pattern);
	}
	
	public static Map<String,String> getAttributes(Element node)
	{
		Map<String,String> attributes=Maps.newLinkedHashMap();
		for (Iterator<?> iter=node.attributeIterator();iter.hasNext();)
		{
			Attribute attribute=(Attribute)iter.next();
			attributes.put(attribute.getName(),attribute.getValue());
		}
		return attributes;
	}
	
	public static String getValue(Element element, String path)
	{
		return getValue(element,path,null);
	}
	
	public static String getValue(Element element, String path, String dflt)
	{
		if (element==null)
			throw new CException(ELEMENT_IS_NULL);
		String value=element.valueOf(path);
		if (value==null)
		{
			if (dflt==null)
				throw new CException("value is null for path"+path);
			else return dflt;
		}
		//logger.debug("path value="+value);
		return fixEntities(value);
	}
	
	public static Integer getIntValue(Element element, String path)
	{
		String value=getValue(element,path,null);
		return MathHelper.parseInteger(value);
		//return Integer.valueOf(value);
	}
	
	public static Float getFloatValue(Element element, String path)
	{
		String value=getValue(element,path,null);
		return MathHelper.parseFloat(value);
		//return Float.valueOf(value);
	}
	
	public static Date getDateValue(Element element, String path, String pattern)
	{
		String value=getValue(element,path,null);
		return DateHelper.parse(value, pattern);
	}
	
	public static String getText(Node node)
	{
		//logger.debug("node.getText()="+node.getText());
		if (node==null)
			throw new CException("node is null");
		String text=node.getText();
		return fixEntities(text);
	}
	
	public static String getTrimmedText(Node node)
	{
		return getText(node).trim();
	}
	
	public static String getChildrenAsXml(Element element)
	{
		if (element==null)
			throw new CException(ELEMENT_IS_NULL);
		StringBuilder buffer=new StringBuilder();
		Iterator<?> iter=element.nodeIterator();
		while(iter.hasNext())
		{
			Node child = (Node)iter.next();
			//logger.debug("child name="+child.getName());
			//logger.debug("string value=["+child.getStringValue().trim()+"]");
			//logger.debug("xml=["+child.asXML()+"]");
			//buffer.append(child.asXML().trim());
			buffer.append(child.asXML());
		}
		String value=buffer.toString().trim();
		//String value=buffer.toString();
		return fixEntities(value);
	}
	
	public static String getChildrenAsXml(Element parent, String path)
	{
		Element element=(Element)parent.selectSingleNode(path);
		return getChildrenAsXml(element);
	}
	
	public static String getChildrenAsXml(Element parent, String path, String dflt)
	{
		Element element=(Element)parent.selectSingleNode(path);
		if (element==null)
			return dflt;
		return getChildrenAsXml(element);
	}
		
	public static String fixEntities(String str)
	{
		return str;
	}
	
	public static List<Element> selectNodes(Element root, String... args)
	{
		Set<String> names=Sets.newHashSet(args);
		List<Element> nodes=new ArrayList<Element>();
		for (Iterator<?> iter=root.nodeIterator();iter.hasNext();)
		{
			Object node=iter.next();
			if (!(node instanceof Element))
				continue;
			Element element=(Element)node;
			if (!names.contains(element.getName()))
				continue;
			nodes.add(element);
		}
		logger.debug("found "+nodes.size()+" nodes of type"+names);
		return nodes;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	
	// https://gist.github.com/sachin-handiekar/1291393
	public static String formatXml(String sourceXml)
	{
		try
		{
			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			// serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			// serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount", "2");
			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(sourceXml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());
			serializer.transform(xmlSource, res);
			return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
}
package org.biobrief.util;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class JsonHelper
{
	private static final Logger logger=LoggerFactory.getLogger(JsonHelper.class);
	
	private JsonHelper(){}
	
	public static String toJson(Object... args)
	{
		try
		{
			Object obj=(args.length>1) ? StringHelper.createMap(args) : args[0];
			ObjectMapper mapper=createObjectMapper();
			return mapper.writeValueAsString(obj);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static byte[] toJsonBytes(Object... args)
	{
		try
		{
			Object obj=(args.length>1) ? StringHelper.createMap(args) : args[0];
			ObjectMapper mapper=createObjectMapper();
			return mapper.writeValueAsBytes(obj);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static ObjectMapper createObjectMapper()
	{
		ObjectMapper mapper=new ObjectMapper();
		configureMapper(mapper);
		return mapper;
	}
	
	public static void configureMapper(ObjectMapper mapper)
	{
		//mapper.registerModule(new JavaTimeModule());
		mapper.setDateFormat(new SimpleDateFormat(LocalDateHelper.YYYYMMDD_PATTERN));
		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);
		//mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	///////////////////////////////////////////////////////
	
	public static JsonNode parse(String json)
	{
		try
		{
			ObjectMapper mapper=new ObjectMapper();
			return mapper.readTree(json);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static JsonNode parse(String json, ObjectMapper mapper)
	{
		try
		{
			return mapper.readTree(json);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static Map<String,Object> parseParams(String str)
	{
		//logger.debug("trying to parse params: "+str);
		List<String> items=StringHelper.split(str,",",true);
		Map<String,Object> params=Maps.newLinkedHashMap();
		for (String item : items)
		{
			String[] pair=item.split(":");
			if (pair.length!=2)
				throw new CException("cannot parse params: "+str);
			String name=pair[0].trim();
			String value=pair[1].trim();
			params.put(name,value);
		}
		return params;
	}
	
	/////////////////////////////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	public static JsonNode parseFile(String filename)
	{
		String json=FileHelper.readFile(filename);
		return parse(json);
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T parseFile(String filename, Class cls)
	{
		String json=FileHelper.readFile(filename);
		return parse(json, cls);
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T parse(String json, Class cls)
	{
		ObjectMapper mapper=createObjectMapper();
		return parse(json, cls, mapper);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T parse(String json, Class cls, ObjectMapper mapper)
	{
		try
		{			
			T obj=(T)mapper.readValue(json, cls);
			return obj;
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	//////////////////////////////////////////////////////////
	
	private static final String JS_EXPRESSION_START="<$$";
	private static final String JS_EXPRESSION_END="$$>";
	
	public static String expr(String expression)
	{
		return JS_EXPRESSION_START+expression+JS_EXPRESSION_END;
	}
	
	////////////////////////////////////////////////////////////////
	
	// same as JSON expect without quoting the fields - must quote fields manually
	public static String toJavascript(Map<String,?> params)
	{
		return toJavascript(params,0);
	}

	@SuppressWarnings("unchecked")
	private static String toJavascript(Object obj, int level)
	{
		if (obj instanceof List)
			return listToJavascript((List<Map<String,?>>)obj, level);
		else if (obj instanceof Map)
			return mapToJavascript((Map<String,?>)obj, level);
		else return obj.toString();
	}
	
	private static String mapToJavascript(Map<String,?> params, int level)
	{
		List<String> list=Lists.newArrayList();
		for (String name : params.keySet())
		{
			Object obj=params.get(name);
			list.add(name+": "+toJavascript(obj,level));
		}
		if (list.isEmpty())
			return "{}";
		StringBuilder buffer=new StringBuilder();//"\n"
		buffer.append(StringHelper.indent("{", level)).append("\n");
		buffer.append(StringHelper.indent(StringHelper.join(list,",\n"), level+1)).append("\n");
		buffer.append(StringHelper.indent("}", level));
		return buffer.toString();
	}
	
	private static String listToJavascript(List<?> array, int level)
	{
		List<String> list=Lists.newArrayList();
		for (Object params : array)
		{
			list.add(toJavascript(params,level+1));
		}
		if (list.isEmpty())
			return "[]";
		StringBuilder buffer=new StringBuilder();//"\n"
		buffer.append(StringHelper.indent("[", level)).append("\n");
		buffer.append(StringHelper.indent(StringHelper.join(list,",\n"), level+1)).append("\n");
		buffer.append(StringHelper.indent("]", level));
		return buffer.toString();
	}
	
	//////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public static List<Object> findOrCreateArray(Map<String,Object> params, String name)
	{
		if (!params.containsKey(name))
			params.put(name,Lists.newArrayList());
		return (List<Object>)params.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> findOrCreateMap(Map<String,Object> params, String name)
	{
		if (!params.containsKey(name))
			params.put(name,Maps.newLinkedHashMap());
		return (Map<String,Object>)params.get(name);
	}
	
	///////////////////////////////////////////
	
	public static List<String> getArrayValues(JsonNode node)
	{
		if (node.getNodeType()!=JsonNodeType.ARRAY)
			throw new CException("expected ARRAY node type: "+node.getNodeType());
		List<String> list=Lists.newArrayList();
		logger.debug("array node string: "+node.toString());
		logger.debug("array node type: "+node.getNodeType());
		for (Iterator<JsonNode> iter=node.iterator(); iter.hasNext();)
		{
			JsonNode item=iter.next();
			logger.debug("item node string: "+item.toString());
			logger.debug("item node type: "+item.getNodeType());
			list.add(item.toString());
		}
		return list;
	}
	
	//////////////////////////////////////////////////////////////
	
	public static <Obj> Obj deepClone(Obj obj)
	{
		return deepClone(obj, obj.getClass());
	}
	
	//https://www.baeldung.com/java-deep-copy
	@SuppressWarnings("unchecked")
	public static <Obj> Obj deepClone(Obj obj, Class<?> cls)
	{
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			String json=objectMapper.writeValueAsString(obj);			
			Obj deepCopy = (Obj)objectMapper.readValue(json, cls);
			return deepCopy;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////
	
	//http://skyscreamer.org/JSONassert/javadoc/org/skyscreamer/jsonassert/JSONCompareMode.html
//	public static String diff(String doc1, String doc2)
//	{
//		try
//		{
//			JSONCompareResult result=JSONCompare.compareJSON(doc1, doc2, JSONCompareMode.LENIENT);
//			return result.getMessage();
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
}

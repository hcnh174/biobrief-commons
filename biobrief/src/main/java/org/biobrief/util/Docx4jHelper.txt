package org.biobrief.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

//https://www.baeldung.com/docx4j
//https://docx4java.org/docx4j/plutext-docx4j_on_a_page-v800.pdf
@SuppressWarnings("deprecation")
public class Docx4jHelper
{	
	private static final Logger logger=LoggerFactory.getLogger(Docx4jHelper.class);
	
	public static String extractText(String filename, String password)
	{
		try
		{
			File doc = new File(filename);
			//WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doc);
			WordprocessingMLPackage wordMLPackage = (WordprocessingMLPackage)OpcPackage.load(doc, password);
			MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
			String textNodesXPath = "//w:t";
			List<Object> textNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);
			List<String> buffer = Lists.newArrayList();
			for (Object obj : textNodes)
			{
				Text text = (Text)((JAXBElement)obj).getValue();
				String textValue = text.getValue();
				//System.out.println(textValue);
				buffer.add(textValue);
			}
			return StringHelper.join(buffer, "");
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	
	public List<Object> xpath(WordprocessingMLPackage wordMLPackage, String xpath)
	{
		try
		{
			MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
			List<Object> list = documentPart.getJAXBNodesViaXPath(xpath, false);
			return list;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	

	public void addShape(WordprocessingMLPackage wordMLPackage, String xml)
	{
		try
		{
			org.docx4j.wml.P para = (P)XmlUtils.unmarshalString(xml);
			MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
			Document wmlDocumentEl=(Document)documentPart.getJaxbElement();
			Body body = wmlDocumentEl.getBody();
			body.getContent().add(para);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public void applyTemplate(String infile, String outfile, HashMap<String,String> mappings)
	{
		WordprocessingMLPackage wordMLPackage=load(infile);
		wordMLPackage=applyTemplate(wordMLPackage,mappings);
		save(wordMLPackage,outfile);
	}
	
	public void applyTemplate(String infile, OutputStream os, HashMap<String,String> mappings)
	{
		WordprocessingMLPackage wordMLPackage=load(infile);
		wordMLPackage=applyTemplate(wordMLPackage,mappings);
		save(wordMLPackage,os);
	}
	
	public WordprocessingMLPackage load(String infile)
	{
		try
		{
			logger.debug("opening file: "+infile);
			WordprocessingMLPackage wordMLPackage=WordprocessingMLPackage.load(new File(infile));
			return wordMLPackage;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public WordprocessingMLPackage applyTemplate(WordprocessingMLPackage wordMLPackage, HashMap<String,String> mappings)
	{
		try
		{
			replaceText(wordMLPackage,mappings);
			return wordMLPackage;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public void save(WordprocessingMLPackage wordMLPackage, String outfile)
	{
		try
		{
			wordMLPackage.save(new File(outfile));
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public void save(WordprocessingMLPackage wordMLPackage, OutputStream os)
	{
		try
		{
			SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
			saver.save(os);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
//	public void pdf(WordprocessingMLPackage wordMLPackage, String outfile)
//	{
//		try
//		{
//			wordMLPackage.setFontMapper(new IdentityPlusMapper());
//			PdfConversion converter=new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wordMLPackage);
//			OutputStream os = new FileOutputStream(outfile);
//			PdfSettings settings=new PdfSettings();
//			converter.output(os,settings);
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}

	public byte[] readImage(String filename) throws Exception
	{
		InputStream is=null;
		try
		{
			File file = new File(filename);
			is = new FileInputStream(filename);
			long length = file.length();   
			// You cannot create an array using a long type. It needs to be an int type.
			if (length > Integer.MAX_VALUE) {
				throw new CException("File too large!!");
			}
			byte[] bytes = new byte[(int)length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)
			{
				offset += numRead;
			}
			// Ensure all the bytes have been read in
			if (offset < bytes.length){
				logger.debug("Could not completely read file "+file.getName());
			}
			is.close();
			return bytes;
		}
		finally
		{
			if (is!=null)
				is.close();
		}
	}
	
	public void replaceText(WordprocessingMLPackage wordMLPackage, HashMap<String,String> mappings) throws JAXBException
	{
		String xml=marshaltoString(wordMLPackage);
		unmarshallFromTemplate(wordMLPackage,xml,mappings);
	}
	
	public String marshaltoString(WordprocessingMLPackage wordMLPackage) throws JAXBException
	{
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		Document doc=(Document)documentPart.getJaxbElement();
		String xml = XmlUtils.marshaltoString(doc, true);
		return xml;
	}
	
	public void unmarshallFromTemplate(WordprocessingMLPackage wordMLPackage, String xml, HashMap<String,String> mappings) throws JAXBException
	{
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		Document doc = (Document)XmlUtils.unmarshallFromTemplate(xml, mappings);
		documentPart.setJaxbElement(doc);
	}

	public static void newImage(WordprocessingMLPackage wordMLPackage, R run, byte[] bytes, long cx)
			throws Exception
	{
		String filenameHint = null; String altText = null; int id1 = 0;	int id2 = 1; boolean link=false;
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);			   
		Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2, cx, link);
		// Now add the inline in w:p/w:r/w:drawing
		ObjectFactory factory = new ObjectFactory();   
		Drawing drawing = factory.createDrawing();			   
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);	   
	}
	
	public static P newImage(WordprocessingMLPackage wordMLPackage, byte[] bytes,
			String filenameHint, String altText, int id1, int id2, long cx) throws Exception
	{
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);			   
		Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2, cx);
		// Now add the inline in w:p/w:r/w:drawing
		ObjectFactory factory = new ObjectFactory();
		P  p = factory.createP();
		R  run = factory.createR();			 
		p.getContent().add(run);	   
		Drawing drawing = factory.createDrawing();			   
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);	   
		return p;
	}
	
	public void addParagraph(Body body, String simpleText)
	{
		 ObjectFactory factory = Context.getWmlObjectFactory();
		// Create the paragraph
		P para = factory.createP();
		// Create the text element
		Text t = factory.createText();
		t.setValue(simpleText); 
		// Create the run
		R run = factory.createR();
		run.getContent().add(t);
		para.getContent().add(run);
		// Now add our paragraph to the document body
		//Body body = this.jaxbElement.getBody();
		body.getContent().add(para);
	}
	
	public void addDrawing(Body body)
	{
		ObjectFactory factory = Context.getWmlObjectFactory();
		// Create the paragraph
		Drawing drawing = factory.createDrawing();
		body.getContent().add(drawing);
	}
}
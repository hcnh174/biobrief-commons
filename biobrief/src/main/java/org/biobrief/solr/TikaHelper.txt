package org.biobrief.solr;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.xml.sax.ContentHandler;

//https://www.baeldung.com/apache-tika
public class TikaHelper
{	

	
	public static String extractContent(String filename)
	{
		InputStream stream=null;
		try
		{
			stream=getStream(filename);
			return extractContent2(stream);
		}
		finally
		{
			try
			{
				if (stream!=null)
					stream.close();
			}
			catch(Exception e)
			{
				System.err.println("could not close stream for file: "+filename);
			}
		}
	}
	
	public static InputStream getStream(String filename)
	{
		FileHelper.checkExists(filename);
		return FileHelper.openFileInputStream(filename);
		//InputStream stream = TikaHelper.class.getClassLoader().getResourceAsStream(filename);
		//return stream;
	}
	
	public static String detectDocType(InputStream stream) throws IOException
	{
		try
		{
			Tika tika = new Tika();
			return tika.detect(stream);
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String extractContent(InputStream stream)
	{
		try
		{
			Tika tika = new Tika();
			return tika.parseToString(stream);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String extractContent2(InputStream stream)
	{
		try
		{
			Parser parser = new AutoDetectParser();
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext context = new ParseContext();			 
			parser.parse(stream, handler, metadata, context);
			return handler.toString();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
}

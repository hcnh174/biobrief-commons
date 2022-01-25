package org.biobrief.solr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;

//https://www.baeldung.com/apache-tika
public class TikaHelper
{
//	public static String extractContent(String filename)
//	{
//		InputStream stream=null;
//		try
//		{
//			stream=getStream(filename);
//			return extractContent(stream);
//		}
//		finally
//		{
//			try
//			{
//				if (stream!=null)
//					stream.close();
//			}
//			catch(Exception e)
//			{
//				System.err.println("could not close stream for file: "+filename);
//			}
//		}
//	}
//	
//	public static String extractContent(String filename, String password)
//	{
//		System.out.println("extracting content from file: "+filename+" with password "+password);
//		InputStream stream=null;
//		try
//		{
//			stream=getStream(filename);
//			return extractContent(stream, password);
//		}
//		finally
//		{
//			try
//			{
//				if (stream!=null)
//					stream.close();
//			}
//			catch(Exception e)
//			{
//				System.err.println("could not close stream for file: "+filename);
//			}
//		}
//	}
//	
//	public static InputStream getStream(String filename)
//	{
//		FileHelper.checkExists(filename);
//		return FileHelper.openFileInputStream(filename);
//		//InputStream stream = TikaHelper.class.getClassLoader().getResourceAsStream(filename);
//		//return stream;
//	}
	
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
	
	public static String parseToString(InputStream stream)
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
	
	public static String extractPdfContent(String filename)
	{
		return extractPdfContent(filename, Optional.empty());
	}
	
	public static String extractPdfContent(String filename, Optional<String> password)
	{
		InputStream stream=null;
		try
		{
			System.out.println("extractPdfContent: "+filename);
			PDFParser parser=createPdfParser();
			BodyContentHandler handler=new BodyContentHandler(-1);
			Metadata metadata=new Metadata();
			ParseContext context=createParseContext(password);
			FileHelper.checkExists(filename);
			stream=FileHelper.openFileInputStream(filename);
			parser.parse(stream, handler, metadata, context);
			return handler.toString();
		}
		catch (Exception e)
		{
			throw new CException(e);
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
	
	private static PDFParser createPdfParser()
	{
		PDFParser parser = new PDFParser();
		parser.getPDFParserConfig().setEnableAutoSpace(true);
		parser.getPDFParserConfig().setSuppressDuplicateOverlappingText(true);
		return parser;
	}
	
	private static ParseContext createParseContext(Optional<String> password)
	{
		ParseContext context = new ParseContext();
		if (password.isPresent())
		{
			context.set(PasswordProvider.class, new PasswordProvider() {
				@Override public String getPassword(Metadata metadata) {
					return password.get();
				}
			});
		}
		return context;
	}
}
//AutoDetectParser parser = new AutoDetectParser();

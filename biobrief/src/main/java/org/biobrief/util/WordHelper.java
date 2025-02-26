package org.biobrief.util;

import java.io.File;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

//import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

//https://www.tutorialspoint.com/apache_poi_word/apache_poi_word_quick_guide.htm
//http://poi.apache.org/text-extraction.html
//http://poi.apache.org/apidocs/4.1/org/apache/poi/hwpf/extractor/WordExtractor.html#WordExtractor-org.apache.poi.poifs.filesystem.POIFSFileSystem-
//https://poi.apache.org/text-extraction.html
//https://poi.apache.org/encryption.html
//http://useof.org/java-open-source/org.apache.poi.xwpf.usermodel.XWPFDocument/9
public class WordHelper
{	
	public static final String DOCX="docx";

	public static String extractText(String filename, String password)
	{
		try
		{
			FileHelper.checkExists(filename);
//			File file=new File(filename);
//			NPOIFSFileSystem filesystem = new NPOIFSFileSystem(file, true);
//			EncryptionInfo info = new EncryptionInfo(filesystem);
//			Decryptor d = Decryptor.getInstance(info);
//			d.verifyPassword(password);
//			InputStream dataStream = d.getDataStream(filesystem);
//			OPCPackage opc = OPCPackage.open(dataStream);
//			XWPFDocument doc = new XWPFDocument(opc);
//			XWPFWordExtractor ex = new XWPFWordExtractor(doc);
//			String text = ex.getText();
//			return text;
			
			File file=new File(filename);
			POIFSFileSystem filesystem = new POIFSFileSystem(file, true);
			EncryptionInfo info = new EncryptionInfo(filesystem);
			Decryptor d = Decryptor.getInstance(info);
			d.verifyPassword(password);
			InputStream dataStream = d.getDataStream(filesystem);
			OPCPackage opc = OPCPackage.open(dataStream);
			XWPFDocument doc = new XWPFDocument(opc);
			XWPFWordExtractor ex = new XWPFWordExtractor(doc);
			String text = ex.getText();
			ex.close();
			return text;
			
//			Biff8EncryptionKey.setCurrentUserPassword(password);
//			POIFSFileSystem fs = new POIFSFileSystem(new File(filename), true);
////			//WordExtractor extractor = new WordExtractor(fs);
//			XWPFWordExtractor extractor=new XWPFWordExtractor(fs);
//			return extractor.getText();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}		
	}
	
//	public static String extractText(String filename, String password)
//	{
//		try
//		{
//			FileHelper.checkExists(filename);
//			Biff8EncryptionKey.setCurrentUserPassword(password);
//			POIFSFileSystem fs = new POIFSFileSystem(new File(filename), true);
//			//WordExtractor extractor = new WordExtractor(fs);
//			XWPFWordExtractor extractor=new XWPFWordExtractor(fs);
//			return extractor.getText();
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}		
//	}
}

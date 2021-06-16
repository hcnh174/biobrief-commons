package org.biobrief.util;

import java.io.File;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hwpf.extractor.WordExtractor;
//import org.apache.poi.xwpf.extractor.XPFFWordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

//https://www.tutorialspoint.com/apache_poi_word/apache_poi_word_quick_guide.htm
//http://poi.apache.org/text-extraction.html
//http://poi.apache.org/apidocs/4.1/org/apache/poi/hwpf/extractor/WordExtractor.html#WordExtractor-org.apache.poi.poifs.filesystem.POIFSFileSystem-
//https://poi.apache.org/encryption.html
public class WordHelper
{	
	public static final String DOCX="docx";

	public static String extractText(String filename, String password)
	{
		try
		{
			FileHelper.checkExists(filename);
			Biff8EncryptionKey.setCurrentUserPassword(password);
			POIFSFileSystem fs = new POIFSFileSystem(new File(filename), true);
			WordExtractor extractor = new WordExtractor(fs);
			return extractor.getText();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}		
	}
}

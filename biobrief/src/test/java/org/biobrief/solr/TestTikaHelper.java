package org.biobrief.solr;

import java.io.IOException;
import java.util.Optional;

import org.biobrief.util.Constants;
import org.biobrief.util.FileHelper;
import org.junit.jupiter.api.Test;

//https://lucene.apache.org/solr/guide/8_1/using-solrj.html
//gradle --info test --tests *TestTikaHelper
public class TestTikaHelper
{
	//@Test
	public void parseToString() throws IOException
	{
		String filename="c:/projects/papers/Yoshida et al_2006_Spreds, inhibitors of the Ras-ERK signal transduction, are dysregulated in human hepatocellular carcinoma and linked to the malignant phenotype of tumors.pdf";
		String str=TikaHelper.extractPdfContent(filename);
		System.out.println("tika output: "+str);
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp/tika/parse_to_string.txt", str);
	}
	
	//@Test
	public void extractContent() throws IOException
	{
		String filename="c:/projects/papers/Yoshida et al_2006_Spreds, inhibitors of the Ras-ERK signal transduction, are dysregulated in human hepatocellular carcinoma and linked to the malignant phenotype of tumors.pdf";
		String str=TikaHelper.extractPdfContent(filename);
		System.out.println("tika output: "+str);
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp/tika/extract_content.txt", str);
	}
	
	@Test
	public void extractContentPassword() throws IOException
	{
		String filename="X:\\D103504297488\\Report_04-2021-14200043_001.pdf";
		String str=TikaHelper.extractPdfContent(filename, Optional.of("D1035"));
		System.out.println("tika output: ["+str+"]");
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp/tika/extract_content_password.txt", str);
	}
}

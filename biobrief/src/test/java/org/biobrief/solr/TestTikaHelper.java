package org.biobrief.solr;

import java.io.IOException;

import org.biobrief.util.Constants;
import org.biobrief.util.FileHelper;

//https://lucene.apache.org/solr/guide/8_1/using-solrj.html
//gradle --stacktrace --info test --tests *TestTikaHelper
public class TestTikaHelper
{
	//@Test
	public void extractContent() throws IOException
	{
		String filename="d:/projects/papers/Yoshida et al_2006_Spreds, inhibitors of the Ras-ERK signal transduction, are dysregulated in human hepatocellular carcinoma and linked to the malignant phenotype of tumors.pdf";
		String str=TikaHelper.extractContent(filename);
		System.out.println("tika output: "+str);
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp/tika/extracted.txt", str);
	}
}

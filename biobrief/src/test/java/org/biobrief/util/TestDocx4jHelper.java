package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle test --stacktrace --info --tests *TestDocx4jHelper
public class TestDocx4jHelper
{
	//@Test
	public void loadFile()
	{
		String filename="C:\\temp\\word.docx";
		String password="secret";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.BIOBRIEF_DIR+"/.temp/word";
		FileHelper.createDirectory(outdir);
	
		String xml=Docx4jHelper.extractText(filename, password);
		xml=Dom4jHelper.formatXml(xml);
		
		FileHelper.writeFile(outdir+"/"+prefix+".xml", xml);
	}
}

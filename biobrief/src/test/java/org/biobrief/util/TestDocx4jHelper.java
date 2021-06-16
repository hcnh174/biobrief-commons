package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle :biobrief-util:test --stacktrace --info --tests *TestDocx4jHelper
public class TestDocx4jHelper
{
	//@Test
	public void loadFile()
	{
		String filename="C:\\temp\\word.docx";
		String password="secret";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.TEMP_DIR+"/word";
		FileHelper.createDirectory(outdir);
	
		String xml=Docx4jHelper.extractText(filename, password);
		xml=Dom4jHelper.formatXml(xml);
		
		FileHelper.writeFile(outdir+"/"+prefix+".xml", xml);
	}
}

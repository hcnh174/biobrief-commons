package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle :biobrief-util:test --stacktrace --info --tests *TestWordHelper
public class TestWordHelper
{
	//@Test
	public void loadFile()
	{
		String filename="c:\\temp\\word.docx";
		String password="secret";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.TEMP_DIR+"/word";
		FileHelper.createDirectory(outdir);
		
		StringBuilder buffer=new StringBuilder();
		String text=WordHelper.extractText(filename, password);

		FileHelper.writeFile(outdir+"/"+prefix+".xml", buffer.toString());
	}
}
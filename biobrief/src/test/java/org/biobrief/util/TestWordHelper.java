package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle test --stacktrace --info --tests *TestWordHelper
public class TestWordHelper
{
	@Test
	public void loadFile()
	{
		//String filename="c:\\temp\\word.docx";
		//String password="secret";
		String filename="x:\\B105602374891\\Seq_B105602374891.docx";
		String password="B1056";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.BIOBRIEF_DIR+"/.temp/word";
		FileHelper.createDirectory(outdir);
		
		String text=WordHelper.extractText(filename, password);

		FileHelper.writeFile(outdir+"/"+prefix+".xml", text);
	}
}
package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle :biobrief-util:test --stacktrace --info --tests *TestWordHelper
public class TestWordHelper
{
	@Test
	public void loadFile()
	{
		String filename="C:\\projects\\expertpanel\\エキスパートパネル関係\\HU20190051\\SeqHU20190051.docx";
		String password="20190051";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.TEMP_DIR+"/word";
		FileHelper.createDirectory(outdir);
		
		StringBuilder buffer=new StringBuilder();
		String text=WordHelper.extractText(filename, password);

		FileHelper.writeFile(outdir+"/"+prefix+".xml", buffer.toString());
	}
}
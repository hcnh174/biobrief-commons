package org.biobrief.util;

import org.dom4j.Document;
import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestDom4jReader
public class TestDom4jReader
{	
	private static final String DIR="x:";
	
	@Test
	public void read()
	{
		String xmlfile=DIR+"/C301903205336_F1/Seq_04-2023-14000022_001.xml";
		System.out.println("reading xmlfile: "+xmlfile);
		Document document=Dom4jHelper.parse(FileHelper.readFile(xmlfile));
		System.out.println("xml="+document.asXML());
	}

}
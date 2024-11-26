package org.biobrief.dictionary;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *DictionaryTest
public class DictionaryTest
{
	@Test
	public void dictionary()
	{
		//Dictionary dictionary=new Dictionary("c:/workspace/hlsg/data/dictionary");
		Dictionary dictionary=new Dictionary("c:/workspace/hucgc/data/dictionary");
		System.out.println(dictionary.toString());
	}
}
package org.biobrief.util;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestStringHelper
public class TestStringHelper
{
	//@Test
	public void testStripXmlComments()
	{
		assertThat(StringHelper.stripXmlComments("abcdefg<!--XYZ-->hijkl")).isEqualTo("abcdefghijkl");
		assertThat(StringHelper.stripXmlComments("abcdefg<!--\nX\nYZ\n-->hijkl")).isEqualTo("abcdefghijkl");
		//List<String> filenames=JpaHelper.getSqlFilesFromPom("pom.xml", "vw_");
		//System.out.println(StringHelper.join(filenames,"\n"));
	}
	
//	@Test
//	public void testGetSqlFilesFromPom()
//	{
//		List<String> filenames=JpaHelper.getSqlFilesFromPom("pom.xml", "vw_");
//		System.out.println(StringHelper.join(filenames,"\n"));
//		assertTrue(!filenames.isEmpty());
//	}
	
//	@Test
//	public void testKuromoji()
//	{
//		String filename=".temp/tmp/kuromoji.txt";
//		FileHelper.writeFile(filename);
//		Tokenizer tokenizer = new Tokenizer();
//		List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");
//		for (Token token : tokens)
//		{
//			FileHelper.appendFile(filename, "new token: "+token.toString());
//			FileHelper.appendFile(filename, "surface="+token.getSurface());
//			FileHelper.appendFile(filename, "features="+token.getAllFeatures());
//			FileHelper.appendFile(filename, "reading="+token.getReading());
//			FileHelper.appendFile(filename, "pronunciation="+token.getPronunciation());
//			FileHelper.appendFile(filename, "baseform="+token.getBaseForm());
//			FileHelper.appendFile(filename, "conjugation form="+token.getConjugationForm());
//			FileHelper.appendFile(filename, "conjugation type="+token.getConjugationType());
//			FileHelper.appendFile(filename, "transliterate="+StringHelper.transliterate("Latin", token.getReading()));
//			FileHelper.appendFile(filename, "---");
//		}
//	}
	
	//@Test
	public void romaji()
	{
		assertThat(StringHelper.romaji("お寿司が食べたい。")).isEqualTo("osushigatabetai。");
		assertThat(StringHelper.romaji("ﾊｰﾎﾞﾆｰ")).isEqualTo("hābonī");
		assertThat(StringHelper.romaji("GRANDTOWER MEDICALCOURT Life care clinic")).isEqualTo("GRANDTOWER MEDICALCOURT Life care clinic");
	}
	
	//@Test
	public void furigana()
	{
		assertThat(StringHelper.furigana("お寿司が食べたい。")).isEqualTo("ｵｽｼｶﾞﾀﾍﾞﾀｲ｡");
		assertThat(StringHelper.furigana("ﾊｰﾎﾞﾆｰ")).isEqualTo("ﾊｰﾎﾞﾆｰ");
		assertThat(StringHelper.furigana("GRANDTOWER MEDICALCOURT Life care clinic")).isEqualTo("GRANDTOWER MEDICALCOURT Life care clinic");
	}
	
	//@Test
	public void transliterate()
	{
		String text="お寿司が食べたい。";
		String rule="Katakana-Latin";
		assertThat(StringHelper.transliterate(rule, text)).isEqualTo("お寿司が食べたい.");
		//http://userguide.icu-project.org/transforms/general
		//String text="お寿司が食べたい。";
		//String rule="Latin";
		//String rule="[:Katakana:]; NFD; Katakana-Latin; (Lower); NFKC; ([:Latin:]);";
		//String rule="Katakana-Latin";
		//FileHelper.writeFile(".temp/tmp/icu4j.txt", StringHelper.transliterate(rule, text));
	}
	
	//@Test
	public void padInteger()
	{
		assertThat(StringHelper.padLeft(42, 8)).isEqualTo("00000042");
	}
	
	@Test
	public void doesNotContainJapaneseText()
	{
		assertThat(StringHelper.containsJapaneseText("B002800788603")).isFalse();
	}
	
	@Test
	public void containsJapaneseText()
	{
		assertThat(StringHelper.containsJapaneseText("臨床診断名: 肺扁平上皮癌")).isTrue();
	}
	
	@Test
	public void containsSomeJapaneseText()
	{
		assertThat(StringHelper.containsJapaneseText("年齢: 69")).isTrue();
	}
	
	@Test
	public void containsWord()
	{
		String value="TP53,TMB-H,FGF3";
		assertThat(StringHelper.containsWord(value, "TMB-H")).isTrue();
		
		value="TP53,TMB-High,FGF3";
		assertThat(StringHelper.containsWord(value, "TMB-H")).isFalse();
	}
	
	@Test
	public void replaceWord()
	{
		String value="TP53,TMB-High,FGF3";
		assertThat(StringHelper.removeWord(value, "TMB-High")).isEqualTo( "TP53,,FGF3");
	}
}
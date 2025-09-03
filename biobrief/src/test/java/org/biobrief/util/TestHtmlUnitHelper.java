package org.biobrief.util;

//gradle  --stacktrace --info test --tests *TestHtmlUnitHelper
public class TestHtmlUnitHelper
{
	//@Test
	public void parseUrl()
	{
		String url="https://oncokb.org/gene/BRAF";
		HtmlUnitHelper.parseUrl(url);
	}
}

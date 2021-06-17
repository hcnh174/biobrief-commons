package org.biobrief.util;

import org.junit.jupiter.api.Test;

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
package org.biobrief.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info :biobrief-util:test --tests *TestJsoupHelper
public class TestJsoupHelper
{
	@Test
	public void parse()
	{
		String html = "<html><head><title>First parse</title></head>"
				+ "<body><p>Parsed HTML into a doc.</p></body></html>";
		Document document=JsoupHelper.parse(html);
		String text = document.body().text();
		System.out.println("text="+text);
	}
	
	//
	@Test
	public void parseUrl()
	{
		String url="http://en.wikipedia.org/";
		Document document=JsoupHelper.parseUrl(url);
		System.out.println("title="+document.title());
		Elements newsHeadlines = document.select("#mp-itn b a");
		for (Element headline : newsHeadlines)
		{
			System.out.println("headline title="+headline.attr("title")+" href="+headline.absUrl("href"));
		}
	}
	
	
}
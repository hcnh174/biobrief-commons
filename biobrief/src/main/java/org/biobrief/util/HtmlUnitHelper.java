package org.biobrief.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//http://htmlunit.sourceforge.net/faq.html#AJAXDoesNotWork
public class HtmlUnitHelper
{
	public static void parseUrl(String url)
	{
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME))
		{
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			HtmlPage page = webClient.getPage(url);//"http://htmlunit.sourceforge.net");
			//Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());
			System.out.println("title="+page.getTitleText());
			String xml = page.asXml();
			//FileHelper.writeFile(Constants.TMP_DIR+"/htmlunit.xml", xml);///)
			System.out.println("xml="+xml);
			//String text = page.asText();
			////System.out.println("text="+text);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
}

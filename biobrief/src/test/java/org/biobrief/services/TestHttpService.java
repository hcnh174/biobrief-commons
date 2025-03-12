package org.biobrief.services;

import org.biobrief.util.MessageWriter;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestHttpService
public class TestHttpService
{
	@Test 
	public void get()
	{
		HttpService httpService=new HttpService("c:/temp/test", 10000L, 30);
		MessageWriter out=new MessageWriter();
		String name1="jRCT2051210086";
		String name2="jRCTs041210103";
		String name3="jRCTs031200064";

		lookup(httpService, name1, out);
		lookup(httpService, name2, out);
		lookup(httpService, name3, out);
		lookup(httpService, name1, out);
		lookup(httpService, name2, out);
		lookup(httpService, name3, out);
	}
	
	private void lookup(HttpService httpService, String name, MessageWriter out)
	{
		String url="https://jrct.niph.go.jp/latest-detail/"+name;
		String key=name+"_ja";
		System.out.println("url="+url);
		String html=httpService.get(key, url, out);
		out.println("looked up value for "+name+": "+html);
	}
}

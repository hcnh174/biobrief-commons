package org.biobrief.services;

import java.util.List;

import org.biobrief.util.HashMapHttpCache;
import org.biobrief.util.HttpCache;
import org.biobrief.util.JsoupHelper;
import org.biobrief.util.JsoupHelper.NameValuePair;
import org.biobrief.util.StringHelper;
import org.biobrief.util.ThreadHelper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class HttpService
{
	private final HttpCache cache;
	private Integer millis=3000;

	public HttpService(HttpCache cache)
	{
		this.cache=cache;
	}
	
	public HttpService()
	{
		this(new HashMapHttpCache());
	}
	
	public String post(String url, List<NameValuePair> params)
	{
		String key=JsoupHelper.createKey(url, params);
		if (cache.containsKey(key))
			return cache.getValue(key);
		Document document=JsoupHelper.post(url, params);
		String html=document.html();
		setValue(key, html);
		sleep();
		return html;
	}
	
	public String dump()
	{
		return StringHelper.toString(cache.getKeys());
	}
	
	private void setValue(String key, String html)
	{
		cache.setValue(key, html);
	}
	
	private void sleep()
	{
		System.out.println("sleeping for "+millis+" milliseconds");
		ThreadHelper.sleep(millis);
	}
}

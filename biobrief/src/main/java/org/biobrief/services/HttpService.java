package org.biobrief.services;

import java.util.List;

import org.biobrief.util.JsoupHelper;
import org.biobrief.util.JsoupHelper.NameValuePair;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestHelper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Service @Data @EqualsAndHashCode(callSuper=false)
public class HttpService extends AbstractFileCacheService
{	
	public HttpService(String cacheDir)
	{
		this(cacheDir, RestHelper.DEFAULT_SLEEP);
	}
	
	public HttpService(String cacheDir, Long sleeptime)
	{
		super(cacheDir, sleeptime, 30, ".html");
	}

	public String get(String key, String url, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		Document document=JsoupHelper.parseUrl(url);
		String html=document.html();
		setValue(key, html, out);
		sleep(out);
		return html;
	}
	
	public String post(String key, String url, List<NameValuePair> params, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		Document document=JsoupHelper.post(url, params);
		String html=document.html();
		setValue(key, html, out);
		sleep(out);
		return html;
	}
	
	
//	private final HttpCache cache;
//	private Integer millis=3000;
//
//	public HttpService(HttpCache cache)
//	{
//		this.cache=cache;
//	}
//	
//	public HttpService()
//	{
//		this(new HashMapHttpCache());
//	}
//	
//	public String post(String url, List<NameValuePair> params)
//	{
//		String key=JsoupHelper.createKey(url, params);
//		if (cache.containsKey(key))
//			return cache.getValue(key);
//		Document document=JsoupHelper.post(url, params);
//		String html=document.html();
//		setValue(key, html);
//		sleep();
//		return html;
//	}
//	
//	public String dump()
//	{
//		return StringHelper.toString(cache.getKeys());
//	}
//	
//	private void setValue(String key, String html)
//	{
//		cache.setValue(key, html);
//	}
//	
//	private void sleep()
//	{
//		System.out.println("sleeping for "+millis+" milliseconds");
//		ThreadHelper.sleep(millis);
//	}
}

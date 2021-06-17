package org.biobrief.util;

import java.util.List;

import org.biobrief.util.JsoupHelper.NameValuePair;
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
	
//	public String get(String url)
//	{
//		String key=JsoupHelper.createKey(url);
//		if (cache.containsKey(key))
//			return cache.getValue(key);
//		String json=JsoupHelper.get(url);
//		setValue(key, json);
//		sleep();
//		return json;
//	}
//	
//	public String get(String url, Headers headers)
//	{
//		String key=createKey(url);
//		if (cache.containsKey(key))
//			return cache.getValue(key);
//		String json=RestHelper.get(restTemplate, url, headers);
//		setValue(key, json);
//		sleep();
//		return json;
//	}
//	
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
		//String filename=Constants.TMP_DIR+"/httpcache/"+DateHelper.getTimestamp()+".html";
		//FileHelper.writeFile(filename, html);
		cache.setValue(key, html);
	}
	
	private void sleep()
	{
		System.out.println("sleeping for "+millis+" milliseconds");
		ThreadHelper.sleep(millis);
	}
	
	////////////////////////////////////////////
	
	
}

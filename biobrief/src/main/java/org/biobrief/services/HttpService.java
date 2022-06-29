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
		sleep(out);
		Document document=JsoupHelper.parseUrl(url);
		String html=document.html();
		setValue(key, html, out);
		//return html;
		return getValue(key, out);
	}
	
	public String post(String key, String url, List<NameValuePair> params, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		sleep(out);
		Document document=JsoupHelper.post(url, params);
		String html=document.html();
		setValue(key, html, out);
		//return html;
		return getValue(key, out);
	}
}

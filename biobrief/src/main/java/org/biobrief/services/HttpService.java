package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.JsoupHelper;
import org.biobrief.util.JsoupHelper.NameValuePair;
import org.biobrief.util.MessageWriter;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Service @Data @EqualsAndHashCode(callSuper=false)
public class HttpService extends AbstractFileCacheService
{	
	protected Boolean offline=false;
	
	public HttpService(String cacheDir, Long sleeptime, Integer maxAge)
	{
		super(cacheDir, sleeptime, maxAge, ".html");
	}
	
	public String get(String key, String url, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		return forceGet(key, url, out);
	}

	public String forceGet(String key, String url, MessageWriter out)
	{
		if (offline)
			throw new CException("HttpService is set to offline so failed to download url: "+url);
		sleep(out);
		Document document=JsoupHelper.parseUrl(url);
		String html=document.html();
		setValue(key, html, out);
		return getValue(key, out);
	}
	
	//////////////////////////////////////////////////
	
//	public String post(String key, String url, List<NameValuePair> params, MessageWriter out)
//	{
//		Date expirationDate=getExpirationDate();
//		return post(key, url, params, expirationDate, out);
//	}
	
	public String post(String key, String url, List<NameValuePair> params, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		return forcePost(key, url, params, out);
	}
	
	public String forcePost(String key, String url, List<NameValuePair> params, MessageWriter out)
	{
		if (offline)
			throw new CException("HttpService is set to offline so failed to download url: "+url);
		sleep(out);
		Document document=JsoupHelper.post(url, params);
		String html=document.html();
		setValue(key, html, out);
		return getValue(key, out);
	}
}

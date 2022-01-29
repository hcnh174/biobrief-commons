package org.biobrief.services;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.biobrief.util.CException;
import org.biobrief.util.HashMapRestCache;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestCache;
import org.biobrief.util.RestHelper;
import org.biobrief.util.RestHelper.Headers;
import org.biobrief.util.StringHelper;
import org.biobrief.util.ThreadHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService
{
	private final RestTemplate restTemplate;
	private final RestCache cache;
	private Integer millis=10000;

	public RestService(RestTemplate restTemplate, RestCache cache)
	{
		this.restTemplate=restTemplate;
		this.cache=cache;
	}
	
	public RestService()
	{
		this(new RestTemplate(), new HashMapRestCache());
	}
	
	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}
	
	public String get(String url, MessageWriter out)
	{
		String key=RestHelper.createKey(url);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.get(restTemplate, url, out);
		setValue(key, json);
		sleep(out);
		return json;
	}
	
	public String get(String url, Headers headers, MessageWriter out)
	{
		String key=RestHelper.createKey(url);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.get(restTemplate, url, headers, out);
		setValue(key, json);
		sleep(out);
		return json;
	}
	
	public <P> String post(String url, P params, MessageWriter out)
	{
		String key=RestHelper.createKey(url, params);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.post(restTemplate, url, params, out);
		setValue(key, json);
		sleep(out);
		return json;
	}
	
//	//https://javadeveloperzone.com/spring-boot/spring-boot-resttemplate-download-file-example/
//	public boolean downloadFile(String url, String filename, MessageWriter out)
//	{
//		try
//		{
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
//			HttpEntity<String> entity = new HttpEntity<>(headers);
//			ResponseEntity<byte[]> response = restTemplate
//					.exchange(url, HttpMethod.GET, entity, byte[].class);
//			byte[] body=response.getBody();
//			System.out.println("respone body: "+body);
//			if (body==null)
//				return false;
//			Files.write(Paths.get(filename), response.getBody());
//			return true;
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
	public String dump()
	{
		return StringHelper.toString(cache.getKeys());
	}
	
	private void setValue(String key, String json)
	{
		cache.setValue(key, json);
	}
	
	private void sleep(MessageWriter out)
	{
//		System.out.println("sleeping for "+millis+" milliseconds");
		ThreadHelper.sleep(millis, out);
	}
}

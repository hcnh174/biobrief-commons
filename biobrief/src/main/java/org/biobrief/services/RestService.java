package org.biobrief.services;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.biobrief.util.CException;
import org.biobrief.util.HashMapRestCache;
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
	
	public String get(String url)
	{
		String key=RestHelper.createKey(url);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.get(restTemplate, url);
		setValue(key, json);
		sleep();
		return json;
	}
	
	public String get(String url, Headers headers)
	{
		String key=RestHelper.createKey(url);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.get(restTemplate, url, headers);
		setValue(key, json);
		sleep();
		return json;
	}
	
	public <P> String post(String url, P params)
	{
		String key=RestHelper.createKey(url, params);
		if (cache.containsKey(key))
			return cache.getValue(key);
		String json=RestHelper.post(restTemplate, url, params);
		setValue(key, json);
		sleep();
		return json;
	}
	
	//https://javadeveloperzone.com/spring-boot/spring-boot-resttemplate-download-file-example/
	public boolean downloadFile(String url, String filename)
	{
		try
		{
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
			HttpEntity<String> entity = new HttpEntity<>(headers);
			//RestTemplateBuilder builder=new RestTemplateBuilder();
			ResponseEntity<byte[]> response = restTemplate //builder.build()
					.exchange(url, HttpMethod.GET, entity, byte[].class);
			//System.out.println("respone body: "+response.getBody());
			byte[] body=response.getBody();
			System.out.println("respone body: "+body);
			if (body==null)
				return false;
			Files.write(Paths.get(filename), response.getBody());
			return true;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public String dump()
	{
		return StringHelper.toString(cache.getKeys());
	}
	
	private void setValue(String key, String json)
	{
		//String filename=Constants.TMP_DIR+"/restcache/"+DateHelper.getTimestamp()+".json";
		//FileHelper.writeFile(filename, json);
		cache.setValue(key, json);
	}
	
	private void sleep()
	{
		System.out.println("sleeping for "+millis+" milliseconds");
		ThreadHelper.sleep(millis);
	}
}

package org.biobrief.services;

import org.biobrief.util.FileHelper;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestHelper;
import org.biobrief.util.RestHelper.Headers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Service @Data
public class RestService
{
	private final RestTemplate restTemplate;
	private final String cacheDir;
	private final Integer sleeptime;//millis
	//private final String expires="1 day";

	public RestService(RestTemplate restTemplate, String cacheDir)
	{
		this(restTemplate, cacheDir, RestHelper.DEFAULT_SLEEP);
	}
	
	public RestService(RestTemplate restTemplate, String cacheDir, int sleeptime)
	{
		this.restTemplate=restTemplate;
		this.cacheDir=cacheDir;
		this.sleeptime=sleeptime;
	}
	
	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}
	
	///////////////////////////////////////
	
	public String get(String key, String url, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		String json=RestHelper.get(restTemplate, url, out);
		setValue(key, json, out);
		sleep(out);
		return json;
	}
	
	public String get(String key, String url, Headers headers, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		String json=RestHelper.get(restTemplate, url, headers, out);
		setValue(key, json, out);
		sleep(out);
		return json;
	}
	
	public <P> String post(String key, String url, P params, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		String json=RestHelper.post(restTemplate, url, params, out);
		setValue(key, json, out);
		sleep(out);
		return json;
	}

	///////////////////////////////////////
	
	private boolean containsKey(String key, MessageWriter out)
	{
		String jsonfile=getFilename(key);
		boolean found=FileHelper.exists(jsonfile);
		if (found)
			out.println("jsonfile found: "+jsonfile);
		return found;
	}
	
	private String getValue(String key, MessageWriter out)
	{
		String jsonfile=getFilename(key);
		out.println("jsonfile found: "+jsonfile);
		return FileHelper.readFile(jsonfile);
	}
	
	private void setValue(String key, String json, MessageWriter out)
	{
		JsonNode node=JsonHelper.parse(json);
		String jsonfile=getFilename(key);
		FileHelper.writeFile(jsonfile, node.toPrettyString());
		out.println("writing jsonfile: "+jsonfile);
		FileHelper.writeFile(jsonfile, json);
	}
	
	private String getFilename(String key)
	{
		return cacheDir+"/"+key+".json";
	}
	
	private void sleep(MessageWriter out)
	{
		RestHelper.sleep(sleeptime, out);
	}
}

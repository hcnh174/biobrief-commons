package org.biobrief.services;

import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestHelper;
import org.biobrief.util.RestHelper.Headers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Service @Data @EqualsAndHashCode(callSuper=false)
public class RestService extends AbstractFileCacheService
{
	private final RestTemplate restTemplate;
	
	public RestService(RestTemplate restTemplate, String cacheDir)
	{
		this(restTemplate, cacheDir, RestHelper.DEFAULT_SLEEP);
	}
	
	public RestService(RestTemplate restTemplate, String cacheDir, Long sleeptime)
	{
		super(cacheDir, sleeptime, 30, ".json");
		this.restTemplate=restTemplate;
	}
	
	//////////////////////////////////
	
	public String get(String key, String url, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		sleep(out);
		String json=RestHelper.get(restTemplate, url, out);
		setValue(key, json, out);
		return json;
	}
	
	public String get(String key, String url, Headers headers, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		sleep(out);
		String json=RestHelper.get(restTemplate, url, headers, out);
		setValue(key, json, out);
		return json;
	}
	
	public <P> String post(String key, String url, P params, MessageWriter out)
	{
		if (containsKey(key, out))
			return getValue(key, out);
		sleep(out);
		String json=RestHelper.post(restTemplate, url, params, out);
		setValue(key, json, out);
		return json;
	}
	
	////////////////////////////////////
	
	@Override
	protected String format(String json)
	{
		JsonNode node=JsonHelper.parse(json);
		return node.toPrettyString();
	}
}

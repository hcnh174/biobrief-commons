package org.biobrief.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;

public class HashMapRestCache implements RestCache
{
	private Map<String, String> cache=Maps.newLinkedHashMap();
		
	////////////////////////////////////////
	
	@Override
	public List<String> getKeys()
	{
		return Lists.newArrayList(cache.keySet().iterator());
	}
	
	@Override
	public boolean containsKey(String key)
	{
		return cache.containsKey(key);
	}
	
	@Override
	public String getValue(String key)
	{
		if (!containsKey(key))
			throw new CException("key not found in cache: "+key);
		System.out.println("found value in cache for key ["+key+"]");
		return cache.get(key);
	}
	
	@Override
	public void setValue(String key, String json)
	{
		System.out.println("caching value: key=["+key+"]\njson="+json);
		cache.put(key, json);
	}
}
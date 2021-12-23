package org.biobrief.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SimpleMap<T> extends LinkedHashMap<String, T>
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(SimpleMap.class);
	
	public SimpleMap() {}
	
	public SimpleMap(Map<String, T> map)
	{
		super(map);
	}
	
//	public SimpleMap(T... args)
//	{
//		super(StringHelper.createMap(args));
//	}
	
	@Override
	public T put(String key, T value)
	{
		checkKey(key);
		checkValue(key, value);
		return super.put(key, value);
	}
	
	public String toJson()
	{
		return JsonHelper.toJson(this);
	}
	
	public String toJavascript()
	{
		return JsonHelper.toJavascript(this);
	}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
	
	////////////////
	
	private void checkKey(String key)
	{
		if (!StringHelper.hasContent(key))
			throw new CException("SimpleMap: key is null or empty: ["+key+"]");
	}
	
	private void checkValue(String key, Object value)
	{
		if (value==null)
			throw new CException("SimpleMap: key is null or empty: ["+key+"]");
	}
}

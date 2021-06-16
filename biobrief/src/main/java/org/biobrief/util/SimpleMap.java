package org.biobrief.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SimpleMap extends LinkedHashMap<String, Object>
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(SimpleMap.class);
	
	public SimpleMap() {}
	
	public SimpleMap(Map<String, Object> map)
	{
		super(map);
	}
	
	public SimpleMap(Object... args)
	{
		super(StringHelper.createMap(args));
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
}

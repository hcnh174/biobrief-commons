package org.biobrief.dictionary;

import java.util.List;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Maps;

public final class DictUtil
{	
	public static String get(Map<String, String> map, String name)
	{
		if (!map.containsKey(name))
			throw new CException("expected key with name: "+name);
		return map.get(name);
	}
	
	public static Boolean asBoolean(String value)
	{
		return asBoolean(value, null);
	}
	
	public static Boolean asBoolean(Object value, Boolean dflt)
	{
		if (!StringHelper.hasContent(value))
			return dflt;
		return value.toString().equalsIgnoreCase("TRUE");
	}
	
	////////////////////////////////////////
	
	public static Integer asInteger(String value)
	{
		return asInteger(value, null);
	}
	
	public static Integer asInteger(String value, Integer dflt)
	{
		if (!StringHelper.hasContent(value))
			return dflt;
		return Integer.parseInt(value);
	}
	
	////////////////////////////////////////////////////
	
	public static String nullIfEmpty(String value)
	{
		return dfltIfEmpty(value, null);
	}
	
	public static String dfltIfEmpty(String value, String dflt)
	{
		if (StringHelper.hasContent(value))
			return value;
		else return dflt;
	}
	
	///////////////////////////////////////////

	public static Map<String,Object> parseParams(String str)
	{
		List<String> items=StringHelper.split(str,",",true);
		Map<String,Object> params=Maps.newLinkedHashMap();
		for (String item : items)
		{
			String[] pair=item.split(":");
			String name=pair[0].trim();
			String value=pair[1].trim();
			params.put(name,value);
		}
		return params;
	}
	
	////////////////////////////////////////////////////////////////////////////////
}

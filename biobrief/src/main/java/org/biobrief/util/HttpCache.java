package org.biobrief.util;

import java.util.List;

public interface HttpCache
{
	List<String> getKeys();
	boolean containsKey(String key);
	String getValue(String key);
	void setValue(String key, String json);
}
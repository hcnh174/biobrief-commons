package org.biobrief.dictionary;

import org.springframework.stereotype.Component;

@Component
public class DictionaryService
{
	private final DictionaryProperties properties;
	
	public DictionaryService(DictionaryProperties properties)
	{
		this.properties=properties;
	}
	
	public Dictionary getDictionary()
	{
		return new Dictionary(properties.getDir());
	}
}

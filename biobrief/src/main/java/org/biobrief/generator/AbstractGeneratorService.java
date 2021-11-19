package org.biobrief.generator;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGeneratorService
{
	protected @Autowired DictionaryService dictionaryService;

	////////////////////////////////////////////////////
	
	protected Dictionary getDictionary()
	{
		return dictionaryService.getDictionary();
	}
}

package org.biobrief.dictionary;

import java.util.Map;

import org.biobrief.util.StringHelper;

public class TextFieldDefinition extends FieldDefinition
{		
	public TextFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.STRING, entityType, map);
	}
	
	public String getDefault()
	{
		return StringHelper.doubleQuote("");
	}
	
	@Override
	protected String getDefaultConverter()
	{
		if (name.toLowerCase().endsWith("note"))
			return "NOTE";
		else return super.getDefaultConverter();
	}
}

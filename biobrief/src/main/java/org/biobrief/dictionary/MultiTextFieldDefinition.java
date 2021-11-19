package org.biobrief.dictionary;

import java.util.Map;

public class MultiTextFieldDefinition extends TextFieldDefinition
{
	public MultiTextFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{
		super(entityType, map);
	}

	@Override
	public boolean isMulti()
	{
		return true;
	}
	
	@Override
	protected String getInitializer()
	{
		return "=Lists.newArrayList()";
	}
	
	@Override
	public String getDefault()
	{
		return null;
	}
	
	@Override
	public String getDeclaredType()
	{
		String type=super.getDeclaredType();
		return "List<"+type+">";
	}
}

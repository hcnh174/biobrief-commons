package org.biobrief.dictionary;

import java.util.Map;

public class MultiRefFieldDefinition extends RefFieldDefinition
{
	public MultiRefFieldDefinition(EntityDefinition entityType, Map<String, String> map)
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
	public String getTsType()
	{
		return "string[]";
	}
	
	@Override
	public String getDeclaredType()
	{
		return "List<String>";
	}
}

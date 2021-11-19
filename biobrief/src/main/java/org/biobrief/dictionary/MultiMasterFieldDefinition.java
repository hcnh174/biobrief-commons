package org.biobrief.dictionary;

import java.util.Map;

public class MultiMasterFieldDefinition extends FieldDefinition
{		
	public MultiMasterFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.MASTER, entityType, map);
	}
	
	@Override
	public boolean isMulti()
	{
		return true;
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "MASTER_LIST:"+type;
	}
	
	@Override
	public String getDefault()
	{
		return null;
	}
	
	@Override
	public String getDeclaredType()
	{
		return "List<String>";
	}
}

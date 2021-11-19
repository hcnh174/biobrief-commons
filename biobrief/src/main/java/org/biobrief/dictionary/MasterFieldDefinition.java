package org.biobrief.dictionary;

import java.util.Map;

public class MasterFieldDefinition extends FieldDefinition
{		
	public MasterFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.MASTER, entityType, map);
	}
	
	@Override
	public String getDeclaredType()
	{
		return "String";
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "MASTER:"+type;
	}
	
	@Override
	protected String getComment()
	{
		return super.getComment()+" "+type;
	}
}

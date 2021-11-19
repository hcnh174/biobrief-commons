package org.biobrief.dictionary;

import java.util.Map;

public class BooleanFieldDefinition extends FieldDefinition
{		
	public BooleanFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.BOOLEAN, entityType, map);
	}
	
	@Override
	public String getDefault()
	{
		return "false";
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "BOOLEAN";
	}
	
	@Override
	protected String getSqlType()
	{
		return "BOOLEAN"+getSqlConstraints();
	}
}

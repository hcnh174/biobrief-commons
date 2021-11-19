package org.biobrief.dictionary;

import java.util.Map;

public class FloatFieldDefinition extends FieldDefinition
{		
	public FloatFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.FLOAT, entityType, map);
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "FLOAT";
	}
	
	@Override
	protected String getSqlType()
	{
		return "REAL"+getSqlConstraints();
	}
}

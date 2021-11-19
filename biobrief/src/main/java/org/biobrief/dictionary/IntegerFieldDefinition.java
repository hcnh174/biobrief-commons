package org.biobrief.dictionary;

import java.util.Map;

public class IntegerFieldDefinition extends FieldDefinition
{		
	public IntegerFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.INTEGER, entityType, map);
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "INTEGER";
	}
	
	@Override
	protected String getSqlType()
	{
		return "INTEGER"+getSqlConstraints();
	}
}

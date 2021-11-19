package org.biobrief.dictionary;

import java.util.Map;

public class EnumFieldDefinition extends FieldDefinition
{		
	public EnumFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{
		super(FieldType.ENUM, entityType, map);
	}
	
	@Override
	public String getDefault()
	{
		return this.type+".NULL";
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "ENUM:"+type;
	}
}

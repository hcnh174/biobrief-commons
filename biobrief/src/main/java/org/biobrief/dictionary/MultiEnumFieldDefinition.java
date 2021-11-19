package org.biobrief.dictionary;

import java.util.Map;

public class MultiEnumFieldDefinition extends FieldDefinition
{
	public MultiEnumFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{
		super(FieldType.ENUM, entityType, map);
	}

	@Override
	public boolean isMulti()
	{
		return true;
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "ENUM_LIST:"+type;
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

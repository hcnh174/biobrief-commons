package org.biobrief.dictionary;

import java.util.Map;

public class RefFieldDefinition extends FieldDefinition
{		
	public RefFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.REF, entityType, map);
	}
	
	@Override
	public String getDeclaredType()
	{
		return "String";
	}
	
	@Override
	protected String getDefaultConverter()
	{
		return "REF:"+type;
	}
	
	@Override
	protected String getComment()
	{
		return super.getComment()+" "+type;
	}
}

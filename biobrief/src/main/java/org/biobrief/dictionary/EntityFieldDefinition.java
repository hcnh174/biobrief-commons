package org.biobrief.dictionary;

import java.util.Map;

public class EntityFieldDefinition extends FieldDefinition
{
	public EntityFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.ENTITY, entityType, map);
	}
	
	@Override
	public boolean isEntity()
	{
		return true;
	}
	
	@Override
	protected String getInitializer()
	{
		return "=new "+getDeclaredType()+"()";
	}
}

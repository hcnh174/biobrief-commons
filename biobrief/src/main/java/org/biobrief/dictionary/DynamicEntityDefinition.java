package org.biobrief.dictionary;

import java.util.Map;

import com.google.common.collect.Maps;

public class DynamicEntityDefinition extends EntityDefinition
{	
	public DynamicEntityDefinition(GroupDefinition group, String name)
	{
		super(group, createMap(name));
	}
	
	private static Map<String, String> createMap(String name)
	{
		Map<String, String> map=Maps.newLinkedHashMap();
		map.put("name", name);
		return map;
	}
	
	@Override
	public FieldDefinition getFieldDefinition(String name, boolean strict)
	{
		if (hasFieldDefinition(name))
			return fields.get(name.toLowerCase());
		FieldDefinition field=FieldDefinition.createDynamicField(this, name);
		add(field);
		return field;
	}
}
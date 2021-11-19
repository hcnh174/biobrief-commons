package org.biobrief.generator.angular;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.biobrief.dictionary.GroupDefinition;
import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.util.StringHelper;

public final class FieldGenerator
{
	public static Map<String, List<Map<String, Object>>> generate(Dictionary dictionary)
	{
		return generateFieldJson(dictionary.getGroup("patients"));
	}
	
	private static Map<String, List<Map<String, Object>>> generateFieldJson(GroupDefinition group)
	{
		Map<String, List<Map<String, Object>>> data=Maps.newLinkedHashMap();
		for (EntityDefinition entityType : group.getEntities())
		{
			List<Map<String, Object>> list=Lists.newArrayList();
			for (FieldDefinition field : entityType.getFieldDefinitions())
			{
				Map<String, Object> map=StringHelper.createMap(
					"type", field.getType(),
					"name", field.getName());
				if (!field.getLabel().equals(field.getName()))
					map.put("label", field.getLabel());
				if (field.getIndexed())
					map.put("indexed", true);
				list.add(map);
			}
			data.put(entityType.getName().toLowerCase(), list);
		}
		return data;
	}
}

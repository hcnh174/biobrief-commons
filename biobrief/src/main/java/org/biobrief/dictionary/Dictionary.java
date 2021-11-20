package org.biobrief.dictionary;

import java.util.Collection;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.DataFrame;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.FileHelper;

import com.google.common.collect.Maps;

public class Dictionary
{
	private final Map<String, GroupDefinition> groups=Maps.newLinkedHashMap();
	private final String baseDir;
	private final MergeSources sources;
	
	public Dictionary()
	{
		this.baseDir=FileHelper.getBaseDirectory();
		sources=new MergeSources();
	}
	
	public Dictionary(String dir)
	{
		this.baseDir=dir;
		this.sources=new MergeSources(baseDir+"/sources.txt");
		createEntityGroups();
		for (GroupDefinition group : groups.values())
		{
			createEntityTypes(group);
			createFieldDefinitions(group);
			addInheritedFieldDefinitions(group);
		}
	}
	
	public GroupDefinition findOrCreateDynamicGroup(String name)
	{
		if (!groups.containsKey(name))
			createDynamicGroup(name);
		return groups.get(name);
	}
	
	public GroupDefinition createDynamicGroup(String name)
	{
		System.out.println("creating dynamic group: "+name);
		Map<String, String> map=Maps.newLinkedHashMap();
		map.put("name", name);
		GroupDefinition group=new GroupDefinition(this, map);
		groups.put(group.getName(), group);
		return group;
	}
	
	private void createEntityGroups()
	{
		//System.out.println("creating entity groups");
		StringDataFrame dataframe=DataFrame.parseTabFile(baseDir+"/.config");
		for (String rowname : dataframe.getRowNames())
		{
			Map<String, String> map=dataframe.getRowAsStrings(rowname);
			GroupDefinition group=new GroupDefinition(this, map);
			groups.put(group.getName(), group);
		}
	}
	
	private void createEntityTypes(GroupDefinition group)
	{
		//System.out.println("creating entity group: "+group.getName());
		String configfile=baseDir+"/"+group.getName()+"/.config";
		StringDataFrame dataframe=DataFrame.parseTabFile(configfile);
		for (String rowname : dataframe.getRowNames())
		{
			Map<String, String> map=dataframe.getRowAsStrings(rowname);
			EntityDefinition type=new EntityDefinition(group, map);
			group.add(type);
		}		
	}
	
	private void createFieldDefinitions(GroupDefinition group)
	{
		for (EntityDefinition entity : group.getEntities())
		{
			createFieldDefinitions(entity);
		}
	}

	private void createFieldDefinitions(EntityDefinition entity)
	{
		//System.out.println("creating field definitions for type: "+entityType.getGroup().getName()+":"+entityType.getName());
		StringDataFrame dataframe=DataFrame.parseTabFile(entity.getFilename());
		for (String rowname : dataframe.getRowNames())
		{
			Map<String, String> map=dataframe.getRowAsStrings(rowname);
			FieldDefinition field=FieldDefinition.create(entity, map);
			//System.out.println("creating field definitions for "+field.getName());
			entity.add(field);
		}
		addAuditingFields(entity);
	}
	
	private void addInheritedFieldDefinitions(GroupDefinition group)
	{
		for (EntityDefinition entity : group.getEntities())
		{
			addInheritedFieldDefinitions(group, entity);
		}
	}
	
	private void addInheritedFieldDefinitions(GroupDefinition group, EntityDefinition entity)
	{
		if (!entity.inherits())
			return;
		//System.out.println("addInheritedFieldDefinitions for "+group.getName()+" "+entity.getName());
		EntityDefinition ancestor=group.getEntity(entity.getInherits());
		for (FieldDefinition field : ancestor.getFieldDefinitions())
		{
			entity.addInherited(field);
		}
	}
	
	private void addAuditingFields(EntityDefinition entity)
	{
		if (!entity.getGroup().getPersistenceType().isOrm() || entity.isChild())
			return;
		//System.out.println("adding auditing field definitions for entity: "+entity.getName());
		entity.add(FieldDefinition.createIdField(entity), false);
		entity.add(FieldDefinition.createNameField(entity), false);
		entity.add(FieldDefinition.createCreatedDateField(entity), false);
		entity.add(FieldDefinition.createCreatedByField(entity), false);
		entity.add(FieldDefinition.createLastModifiedDateField(entity), false);
		entity.add(FieldDefinition.createLastModifiedByField(entity), false);
	}
	
	public Collection<GroupDefinition> getEntityGroups()
	{
		return groups.values();
	}
	
	public GroupDefinition getGroup(String name)
	{
		if (!groups.containsKey(name))
			throw new CException("cannot find entity group with name: "+name);
		return groups.get(name);
	}
	
//	public GroupDefinition getGroup(EntityGroup group)
//	{
//		return getGroup(group.name());
//	}
	
	public MergeSources getSources() {return sources;}
	
	public String getBaseDir() {return baseDir;}
}

package org.biobrief.dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EntityDefinition
{
	protected GroupDefinition group;
	protected String name;
	protected String label;
	protected String table;
	protected EntityDefinition parent;
	protected List<EntityDefinition> children=Lists.newArrayList();
	protected String collection;
	protected List<String> files;
	protected boolean escape=false;
	protected boolean indexed=false;
	protected String prefix;
	protected String plural;
	protected String inherits;
	protected String subdir;
	
	protected final Map<String, FieldDefinition> fields=Maps.newLinkedHashMap();
	
	public String getName(){return name;}
	public String getLabel(){return label;}
	public GroupDefinition getGroup(){return group;}
	public EntityDefinition getParent(){return parent;}
	public List<EntityDefinition> getChildren(){return children;}
	public String getCollection(){return collection;}
	public String getTable(){return table;}
	public boolean getEscape() {return escape;}
	public Collection<FieldDefinition> getFieldDefinitions(){return fields.values();}
	public String getPlural(){return plural;}
	public boolean isIndexed(){return indexed;}
	public String getInherits(){return inherits;}
	public boolean inherits(){return StringHelper.hasContent(inherits);}
	public String getSubdir(){return subdir;}
	
	public EntityDefinition(GroupDefinition group, Map<String, String> map)
	{
		this.group=group;
		this.name=map.get("name");
		this.label=DictUtil.dfltIfEmpty(map.get("label"), this.name);
		this.plural=getPlural(map);
		this.table=DictUtil.dfltIfEmpty(map.get("table"), plural);
		this.collection=DictUtil.dfltIfEmpty(map.get("collection"), plural);//map.get("collection");
		this.escape=DictUtil.asBoolean(map.get("escape"), group.getEscape());
		this.indexed=DictUtil.asBoolean(map.get("indexed"), false);
		this.inherits=DictUtil.nullIfEmpty(map.get("inherits"));
		this.subdir=DictUtil.nullIfEmpty(map.get("subdir"));
		String parent=DictUtil.nullIfEmpty(map.get("parent"));
		if (StringHelper.hasContent(parent))
			group.getEntity(parent).addChild(this);
		//System.out.println("EntityDefinition: "+name+": inherits="+inherits);
	}
	
	public String getFilename()
	{
		String baseDir=group.getDictionary().getBaseDir();
		return baseDir+"/"+group.getName()+"/"+getPlural().toLowerCase()+".txt";
	}
	
	
	public String getClassFile()
	{
		String path="/entities";
		if (StringHelper.hasContent(subdir))
			path+="/"+subdir;
		path+="/"+getName()+".java";
		return path;
	}
	
	// LabResult labResults, therapy therapies
	private static String getPlural(Map<String, String> map)
	{
		String plural=StringHelper.uncapitalizeFirstLetter(map.get("name"))+"s";
		return DictUtil.dfltIfEmpty(map.get("plural"), plural);
	}
	
	public void setParent(EntityDefinition parent)
	{
		this.parent=parent;
	}
	
	public void addChild(EntityDefinition child)
	{
		child.setParent(this);
		this.children.add(child);
	}

	public void addInherited(FieldDefinition field)
	{
		if (hasFieldDefinition(field.getName()))
			return;
		//System.out.println("adding inherited field definition: "+group.getName()+" "+name+" "+field.getName());
		FieldDefinition inherited=(FieldDefinition)field.clone();
		//FieldDefinition inherited=JsonHelper.deepClone(field, field.getClass());
		inherited.setInherited(true);
		add(inherited);
		//System.out.println("check clone: old.inherited="+field.getInherited()+" new.inherited="+inherited.getInherited());
	}
	
	public void add(FieldDefinition field)
	{
		add(field, true);
	}
	
	public void add(FieldDefinition field, boolean strict)
	{
		String key=field.getName().toLowerCase();
		if (hasFieldDefinition(field.getName()))
		{
			if (!strict)
				return;
			throw new CException("found duplicate key for entity "+name+": "+key);
		}
		fields.put(key, field);
	}
	
	public boolean isChild()
	{
		return this.parent!=null;
	}

	public boolean isClassEntity()
	{
		return group.getPersistenceType().isOrm();
	}
	
	public boolean isSql()
	{
		return group.getPersistenceType().isSql();
	}

	public boolean hasFieldDefinition(String name)
	{
		return fields.containsKey(name.toLowerCase());
	}
	
	public FieldDefinition getFieldDefinition(String name)
	{
		return getFieldDefinition(name, false);
	}
	
	public FieldDefinition getFieldDefinition(String name, boolean strict)
	{
		if (isPath(name))
			return getNestedFieldDefinition(name);
		if (hasFieldDefinition(name))
			return fields.get(name.toLowerCase());
		String message="cannot find entityType field named: "+name+" in entityType "+getName();
		if (strict)
			throw new CException(message);
		else if (!name.toLowerCase().endsWith("mask"))
			LogUtil.log(message);
		return null;
	}
	
	// mcus[0].diabetesTreatment
	private FieldDefinition getNestedFieldDefinition(String name)
	{
		name=name.replaceAll("\\[[0-9]+\\]", "");
		//System.out.println("nested name="+name);
		int index=name.indexOf(".");
		//assert(index!=-1);
		String left=name.substring(0,index);
		String right=name.substring(index+1);
		//System.out.println("nested path: name="+name+"; left="+left+"; right="+right);
		FieldDefinition field=getFieldDefinition(left);
		//assert(field.getFieldType()==FieldType.ENTITY);
		EntityDefinition entityType=((EntityFieldDefinition)field).getEntity();
		//System.out.println(" entityType="+entityType);
		//return group.getEntityDefinition(entityType), right, false);
		return entityType.getFieldDefinition(right, false);
	}
	
	private static boolean isPath(String name)
	{
		return name.contains(".") || name.contains("[");
	}
	
	public List<FieldDefinition> getIndexedFields()
	{
		List<FieldDefinition> list=Lists.newArrayList();
		for (FieldDefinition field : fields.values())
		{
			if (field.getIndexed())
				list.add(field);
		}
		return list;
	}

//	public String getClassFile()
//	{
//		if (!isClassEntity())
//			return null;
//		return "./src/main/java/org/hlsg/"+group.getName()+"/entities/"+cls.getSimpleName()+".java";
//	}
	
//	// if there is no class file, then assume the fields cannot be mapped easily to Java fields and surround with quotes
//	public boolean escapeColnames()
//	{
//		return entityType.getEscape();
//	}

//	public void getI18n(Map<String, String> i18n)
//	{
//		if (!isClassEntity())
//			return;
//		for (FieldDefinition field : fields)
//		{
//			field.getI18n(i18n);
//		}
//	}
	
//	public Predicate getPredicate(Filters filters)
//	{
//		BooleanBuilder builder=new BooleanBuilder();
//		for (Filter filter : filters.getFilters())
//		{
//			FieldDefinition field=getFieldDefinition(filter.getField(), true);
//			Predicate predicate=field.getPredicate(filter);
//			if (predicate!=null)
//				builder=builder.and(predicate);
//		}
//		return builder;
//	}
	
//	public Optional<Predicate> getPredicate(Filters filters)
//	{
//		List<BooleanExpression> predicates=Lists.newArrayList();
//		for (Filter filter : filters.getFilters())
//		{
//			FieldDefinition field=getFieldDefinition(filter.getField());
//			predicates.add(field.getPredicate(filter));
//		}
//		if (predicates.isEmpty())
//			return Optional.empty();
//		BooleanExpression result = predicates.get(0);
//		for (int i = 1; i < predicates.size(); i++)
//		{
//			result = result.and(predicates.get(i));
//		}
//		return Optional.of(result);
//	}
	
//	//////////////////////////////////////////////////////////////

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public Optional<PathBuilder> getPathBuilder()
//	{
//		if (!this.group.getPersistenceType().isOrm())
//			return Optional.empty();
//		String name=this.name;
//		name=name.substring(0,1).toLowerCase()+name.substring(1);
//		String classname="org.biobrief."+group.getName()+"."+this.name;
//		Class<?> cls=BeanHelper.lookupClass(classname);
//		return Optional.of(new PathBuilder(cls, name));
//	}
	
	/////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString()
	{
		List<String> list=Lists.newArrayList();
		for (FieldDefinition field : fields.values())
		{
			list.add(field.toString());
		}
		return StringHelper.join(list, "\n")+"\n";
	}
	
//	@Override
//	public String toString()
//	{
//		StringBuilder buffer=new StringBuilder();
//		for (int i=0;i<fields.size();i++)
//		{
//			FieldDefinition field=fields.get(i);
//			buffer.append(field.toString()).append("\n");
//		}
//		return buffer.toString();
//	}
}

package org.biobrief.dictionary;

import java.util.List;
import java.util.Map;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.Constants;
import org.biobrief.util.Constants.ElasticType;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class FieldDefinition implements Cloneable
{
	protected final FieldType fieldType;
	protected final EntityDefinition entityType;
	protected final String name;
	protected final String type;
	protected final String label;
	protected final Boolean notnull;
	protected final Boolean readonly;
	protected final Boolean disabled;
	protected final Boolean calculated;
	protected final Boolean indexed;
	protected final Boolean download;
	protected final Boolean analyzed;
	protected final String converter;
	//protected final MergeSourceMap mergemap;
	//protected final Map<String,Object> elasticConfig;
	protected final Map<String,Object> formConfig;
	protected final Map<String,Object> columnConfig;
	//protected final Map<String,Object> mongoConfig;
	protected final BeanHelper beanhelper=new BeanHelper();
	protected boolean inherited=false;
	
	public FieldDefinition(FieldType fieldType, EntityDefinition entityType, Map<String, String> map)
	{
		this.fieldType=fieldType;
		this.entityType=entityType;
		this.name=parseName(map.get("name"));
		this.type=map.get("type");
		this.label=map.get("label");
		this.notnull=DictUtil.asBoolean(map.get("notnull"), entityType.isClassEntity() ? false : fieldType.isNotNull());
		this.readonly=DictUtil.asBoolean(map.get("readonly"), false);
		this.disabled=DictUtil.asBoolean(map.get("disabled"), false);
		this.calculated=DictUtil.asBoolean(map.get("calculated"), false);
		this.inherited=DictUtil.asBoolean(map.get("inherited"), false);
		this.indexed=DictUtil.asBoolean(map.get("indexed"), false);
		this.download=DictUtil.asBoolean(map.get("download"), false);
		this.analyzed=DictUtil.asBoolean(map.get("analyzed"), false);
		//this.elasticConfig=DictUtil.parseParams(map.get("elasticconfig"));
		this.formConfig=DictUtil.parseParams(map.get("formconfig"));
		this.columnConfig=DictUtil.parseParams(map.get("columnconfig"));
		//this.mongoConfig=DictUtil.parseParams(map.get("mongoconfig"));
		this.converter=DictUtil.dfltIfEmpty(map.get("converter"), "AUTO");
		//this.mergemap=new MergeSourceMap(entityType.getGroup().getDictionary().getSources(), map);
	}
	
	private String parseName(String name)
	{
		name=StringHelper.replace(name, "Enum:", "");
		name=StringHelper.replace(name, "Master:", "");
		return name;
	}
	
	public final FieldType getFieldType(){return this.fieldType;}
	public final EntityDefinition getEntity(){return this.entityType;}
	public final String getName(){return this.name;}
	public final String getType(){return this.type;}
	public final String getLabel(){return this.label;}
	public final Boolean getReadonly(){return this.readonly;}
	public final Boolean getDisabled(){return this.disabled;}
	public final Boolean getCalculated(){return this.calculated;}
	public final Boolean getInherited(){return this.inherited;}
	public final Boolean getIndexed(){return this.indexed;}
	public final Boolean getDownload(){return this.download;}
	public final Boolean getAnalyzed(){return this.analyzed;}
	//public final Map<String,Object> getElasticConfig(){return elasticConfig;}
	public final Map<String,Object> getFormConfig(){return formConfig;}
	public final Map<String,Object> getColumnConfig(){return columnConfig;}
	//public final Map<String,Object> getMongoConfig(){return mongoConfig;}
	//public final MergeSourceMap getMergeMap(){return this.mergemap;}
	
	public void setInherited(boolean inherited)
	{
		this.inherited=inherited;
	}

	public String getTsType()
	{
		String type=fieldType.getTsType();
		if (isEntity())
			type=getDeclaredType();
		if (type.startsWith("List<"))// remove List< and >
			return removeList(type)+"[]";
		return type;
	}
	
	public static String removeList(String type)
	{
		if (!(type.startsWith("List<") && type.endsWith(">")))
			throw new CException("expected type name to start with List< and end with >: "+type);
		return type.substring(5, type.length()-1);
	}
	
	public ElasticType getElasticType()
	{
		return this.fieldType.getElasticType(isMulti());
	}
	
	public final boolean isNotNull()
	{
		return notnull;
	}

	public final boolean isReadonly()
	{
		return readonly;
	}
	
	public final boolean isDisabled()
	{
		return disabled;
	}
	
	public final boolean isCalculated()
	{
		return calculated;
	}
	
	public final boolean isInherited()
	{
		return inherited;
	}
	
	public final boolean isGenerated()
	{
		return !isReadonly() && !isCalculated() && !isInherited();
	}
	
	public final boolean isIndexed()
	{
		return indexed;
	}
	
	public final boolean isDownloaded()
	{
		return download;
	}
	
	public final boolean isAnalyzed()
	{
		return analyzed;
	}
	
	public final boolean isDate()
	{
		return fieldType==FieldType.DATE;
	}
	
	public final boolean isMaster()
	{
		return fieldType==FieldType.MASTER;
	}
	
	public final boolean isEnum()
	{
		return fieldType==FieldType.ENUM;
	}
	
	public boolean isMulti()
	{
		return false;
	}
	
	public boolean isEntity()
	{
		return false;
	}
	
	public String getDefault()
	{
		return null;
	}
	
	public final String getConverter()
	{
		if (!converter.equals("AUTO"))
			return converter;
		else return getDefaultConverter();
	}
	
	protected String getDefaultConverter()
	{
		return "DEFAULT";
	}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}

	////////////////////////////////////////////////////////////
	
	private final String getJavaBeanName(String name)
	{
		return StringHelper.getJavaBeanName(name);
	}
	
	private final String getPropertyAnnotations()
	{
		List<String> annotations=Lists.newArrayList();
		//if (isMongoIndex())
		//	annotations.add("@Indexed");
		if (annotations.isEmpty())
			return "";
		else return StringHelper.join(annotations," ")+" ";//+"\n";
	}

//	protected final boolean isMongoIndex()
//	{
//		return DictUtil.asBoolean(mongoConfig.get("indexed"), false);
//	}
	
	protected String getComment()
	{
		return (this.label==null) ? "" : " //"+this.label;
	}

	///////////////////////////////////
	
	public final String createDeclaration()
	{
		String annotations=getPropertyAnnotations();
		return annotations+"protected "+getDeclaredType()+" "+this.name+this.getInitializer()+";\n";//+getComment()+"\n";
	}
	
	protected String getInitializer()
	{
		return isMulti() ? "=Lists.newArrayList()" : "";
	}
	
	public final String createSetter()
	{
		String str="public void set"+getJavaBeanName(name)+"(final "+getDeclaredType()+" "+name+")";
		str+="{this."+name+"="+name+";}\n";
		return str;
	}
	
	public final String createGetter()
	{
		String str="public "+getDeclaredType()+" get"+this.getJavaBeanName(name)+"()";//indent()
		str+="{return "+name+";}\n";
		return str;
	}

	public final String getGetter()
	{
		return "get"+this.getJavaBeanName(name)+"()";
	}
	
	public final String createAccessors()
	{
		return createGetter()+createSetter();
	}

	public String getDeclaredType()
	{
		return this.type;
	}
	
	/////////////////////////////////////
	
	public final String createInit()
	{
		if (!isGenerated())
			return null;
		String dflt=getDefault();
		if (StringHelper.hasContent(dflt))
			return "this."+this.name+"="+dflt+";\n";
		return null; 
	}
	
	protected String getSqlType()
	{
		return "TEXT"+getSqlConstraints();
	}
	
	protected final String getSqlConstraints()
	{
		//return isNotNull() ? " NOT NULL" : " NULL";
		return " NULL";
	}
	
	public final String createSql()
	{
		String sqltype=getSqlType();
		return "\t"+this.getColname()+" "+sqltype+",\n";
	}
	
	public final String getColname()
	{
		if (entityType.getEscape())
			return StringHelper.doubleQuote(name);
		else return StringHelper.toUnderscore(name);
	}
	
	////////////////////////////////////////////////////////////////////

	public final Object getProperty(Object obj)
	{
		return getProperty(obj, getName());
	}
	
	public Object getProperty(Object obj, String path)
	{
		Object value=BeanHelper.getProperty(obj, path, null);
		if (value==null)
			return null;
		else if (value instanceof Integer)
			return value;
		else return value.toString();
	}

	//////////////////////////////////////////////////////////////
	
	public void setPropertyFromString(Object obj, String value)
	{
		if (isReadonly())
			return;
		beanhelper.setValue(obj, getName(), value);
	}
	
	public final void getI18n(Map<String,String> i18n)
	{
		i18n.put(entityType.getName().toLowerCase()+"_"+name, label);
	}
	
	@Override
	public Object clone()
	{	
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new CException(e);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	
	public static FieldDefinition create(EntityDefinition entityType, Map<String, String> map)
	{
		String type=map.get("type");
		if (!StringHelper.hasContent(type))
			throw new CException("cannot find type column in field definitions for type "+entityType.getName());
		boolean multi=false;
		if (type.startsWith("List<"))
		{
			//type=type.substring(5, type.length()-1);
			type=removeList(type);
			map.put("type", type);
			multi=true;
		}
		if (!StringHelper.hasContent(type))
			throw new CException("field type is blank for field "+entityType.getName()+": "+StringHelper.toString(map));
		FieldType fieldType=FieldType.find(type);
		if (type.contains(":"))
		{
			type=type.substring(type.indexOf(":")+1);
			map.put("type", type);
		}
		switch(fieldType)
		{
		case STRING:
			if (multi)
				return new MultiTextFieldDefinition(entityType, map);
			else return new TextFieldDefinition(entityType, map);
		case BOOLEAN:
			return new BooleanFieldDefinition(entityType, map);
		case INTEGER:
			return new IntegerFieldDefinition(entityType, map);
		case FLOAT:
			return new FloatFieldDefinition(entityType, map);
		case DATE:
			return new DateFieldDefinition(entityType, map);
		case ENTITY:
			if (multi)
				return new MultiEntityFieldDefinition(entityType, map);
			else return new EntityFieldDefinition(entityType, map);
		case ENUM:
			if (multi)
				return new MultiEnumFieldDefinition(entityType, map);
			else return new EnumFieldDefinition(entityType, map);
		case MASTER:
			if (multi)
				return new MultiMasterFieldDefinition(entityType, map);
			else return new MasterFieldDefinition(entityType, map);
		case REF:
			if (multi)
				return new MultiRefFieldDefinition(entityType, map);
			return new RefFieldDefinition(entityType, map);
		default:
			throw new CException("field type not recognized: "+fieldType);
		}
	}
	
	public static FieldDefinition createIdField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.ID);
		map.put("type", "String");
		map.put("inherited", "TRUE");
		map.put("label", "id");
		return create(entityType, map);
	}
	
	public static FieldDefinition createNameField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.NAME);
		map.put("type", "String");
		map.put("inherited", "TRUE");
		map.put("label", Constants.NAME_LABEL);//I18n.name.getText());
		return create(entityType, map);
	}
	
	public static FieldDefinition createCreatedDateField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.CREATED_DATE);
		map.put("type", "Date");
		map.put("inherited", "TRUE");
		map.put("label", Constants.CREATED_DATE_LABEL);//I18n.createdDate.getText());
		return create(entityType, map);
	}
	
	public static FieldDefinition createCreatedByField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.CREATED_BY);
		map.put("type", "Ref:User");
		map.put("inherited", "TRUE");
		map.put("label", Constants.CREATED_BY_LABEL);
		return create(entityType, map);
	}
	
	public static FieldDefinition createLastModifiedDateField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.LAST_MODIFIED_DATE);
		map.put("type", "Date");
		map.put("inherited", "TRUE");
		map.put("label", Constants.LAST_MODIFIED_DATE_LABEL);
		return create(entityType, map); 
	}
	
	public static FieldDefinition createLastModifiedByField(EntityDefinition entityType)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", Constants.LAST_MODIFIED_BY);
		map.put("type", "Ref:User");
		map.put("inherited", "TRUE");
		map.put("label", Constants.LAST_MODIFIED_BY_LABEL);
		return create(entityType, map);
	}
	
	public static FieldDefinition createDynamicField(EntityDefinition entityType, String name)
	{
		System.out.println("creating dynamic fields: "+entityType.getName()+":"+name);
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", name);
		map.put("type", "String");
		map.put("label", name);
		return create(entityType, map);
	}
}

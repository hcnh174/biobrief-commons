package org.biobrief.dictionary;

import org.biobrief.util.Constants.ElasticType;
import org.biobrief.util.StringHelper;

public enum FieldType
{
	STRING,
	BOOLEAN,
	INTEGER,
	FLOAT,
	DATE,
	ENUM,
	MASTER,
	ENTITY,
	REF;
	
	public boolean isBasic()
	{
		return !isSpecial();
	}
	
	public boolean isSpecial()
	{
		return this==FieldType.ENUM || this==FieldType.MASTER;
	}
	
	public boolean isNumeric()
	{
		return this==FieldType.INTEGER || this==FieldType.FLOAT;
	}
	
	public boolean isNotNull()
	{
		if (this==FieldType.STRING)
			return true;
		if (this==FieldType.ENUM)
			return true;
		return false;
	}
	
	public String getClassName()
	{
		return StringHelper.capitalizeFirstLetter(name())+"Field";
	}
	
	public String getJsType()
	{
		switch(this)
		{
		case DATE:
			return "date";
		case FLOAT:
			return "number";
		case INTEGER:
			return "int";
		case BOOLEAN:
			return "boolean";
		case STRING:
		case ENUM:
		case REF:
			return "string";		
		default:
			return "auto";
		}
	}
	
	public String getTsType()
	{
		switch(this)
		{
		case DATE:
			return "Date";//return "string";//return "Date";//"string";
		case FLOAT:
			return "number";
		case INTEGER:
			return "number";
		case BOOLEAN:
			return "boolean";
		case STRING:
		case ENUM:
		case REF:
			return "string";
		default:
			return "any";
		}
	}
	
	public ElasticType getElasticType(boolean multi)
	{
		switch(this)
		{
		case DATE:
			return ElasticType.DATE;
		case FLOAT:
			return ElasticType.FLOAT;
		case INTEGER:
			return ElasticType.INTEGER;
		case BOOLEAN:
			return ElasticType.BOOLEAN;
		case STRING:
			return multi ? ElasticType.KEYWORD : ElasticType.STRING;
		case ENUM:
			return ElasticType.KEYWORD;
		default:
			return ElasticType.STRING;
		}
	}
	
	public String getGraphQLType(String dflt)
	{
		switch(this)
		{
		case DATE:
			return "Date";
		case FLOAT:
			return "Float";
		case INTEGER:
			return "Int";
		case BOOLEAN:
			return "Boolean";
		case STRING:
		case ENUM:
			return "String";
		default:
			if (dflt.startsWith("List<"))
				return dflt.substring(5,dflt.length()-1);
			return dflt;
		}
	}
	
	////////////////////////////////////////////////////
	
	public static FieldType find(String value)
	{
		//assert(StringHelper.hasContent(value));
		for (FieldType type : values())
		{
			if (type.name().equalsIgnoreCase(value))
				return type;
		}
		if (value.startsWith("Enum:"))
			return FieldType.ENUM;
		if (value.startsWith("Master:"))
			return FieldType.MASTER;
		if (value.startsWith("Ref:"))
			return FieldType.REF;
		return FieldType.ENTITY;
	}
	
}

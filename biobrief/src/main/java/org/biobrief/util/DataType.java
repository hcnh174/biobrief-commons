package org.biobrief.util;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public enum DataType
{
	STRING(false, "string", "TEXT"),
	ENUM(false, "string", "TEXT"),
	INTEGER(true, "int", "INTEGER"),
	FLOAT(true, "float", "REAL"),
	DOUBLE(true, "float", "REAL"),
	BOOLEAN(false, "boolean", "BOOLEAN"), 
	DATE(false, "date", "TIMESTAMP"), 
	DATETIME(false, "date", "TIMESTAMP"),
	REF(false, "object", "TEXT"),
	MAP(false, "null", "MAP"),
	LIST(false, "null", "LIST"),
	NULL(false, "null", "NULL");
	
	private boolean numeric;
	private String json;
	private String sql;
	
	DataType(boolean numeric, String json, String sql)
	{
		this.numeric=numeric;
		this.json=json;
		this.sql=sql;
	}
	
	public boolean isNumeric(){return this.numeric;}
	public String getJson(){return this.json;}
	public String getSql(){return this.sql;}
	
	public boolean isQuoted()
	{
		return (this==STRING || this==ENUM);
	}
		
	public Object convert(String value)
	{
		if (!StringHelper.hasContent(value))
			return null;
		try
		{
			switch(this)
			{
			case STRING:
			case ENUM:
				return value;
			case INTEGER:
				return Integer.valueOf(value);
			case DOUBLE:
				return Double.valueOf(value);
			case FLOAT:
				return Float.valueOf(value);
			case BOOLEAN:
				return Boolean.valueOf(value);
			case DATE:
				return DateHelper.parseDate(value, DateHelper.POSTGRES_YYYYMMDD_PATTERN);
			case DATETIME:
				return DateHelper.parseDate(value, DateHelper.DATETIME_PATTERN);
				//return LocalDateHelper.parseDate(value, LocalDateHelper.POSTGRES_YYYYMMDD_PATTERN);
			default:
				throw new CException("no handler for DataType "+name());
			}
		}
		catch (Exception e)
		{
			System.err.println("DataType.convert problem: ["+value+"]: "+e);
			return value;
		}
	}
	
	public String formatSql(Optional<Object> value)
	{
		//System.out.println("value: "+value);
		if (value==null)
			throw new CException("Optional<> must not be null: "+value);
		if (!value.isPresent())
			return "NULL";
		return formatSql(value.get());//hack
	}
	
	public String formatSql(Object value)
	{
		if (value==null)
			return "NULL";
		switch(this)
		{
		case STRING:
		case ENUM:
			return StringHelper.singleQuote(StringHelper.escapeSql(value.toString()));
		case DATE:
			return StringHelper.singleQuote(DateHelper.format((Date)value, DateHelper.POSTGRES_YYYYMMDD_PATTERN));
		case DATETIME:
			return StringHelper.singleQuote(DateHelper.format((Date)value, DateHelper.DATETIME_PATTERN));
		default:
			return value.toString(); 
		}
	}
	
	public static DataType guessDataType(Collection<Object> values)
	{
		boolean is_integer=true;
		boolean is_float=true;
		boolean is_boolean=true;
		for (Object obj : values)
		{
			String value=obj.toString().toLowerCase().trim();
			if (StringHelper.isEmpty(value))
				continue;
			if (!MathHelper.isFloat(value))
				is_float=false;
			if (!MathHelper.isInteger(value))
				is_integer=false;
			if (!"true".equals(value) && !"false".equals(value))
				is_boolean=false;
		}
		if (is_boolean)
			return DataType.BOOLEAN;
		if (is_integer)
			return DataType.INTEGER;
		if (is_float)
			return DataType.FLOAT;
		return DataType.STRING;
	}
	
	public static DataType guessDataType(Object obj)
	{
		DataType type=guessDataTypeByClass(obj);
		if (obj==null || type!=DataType.STRING)
			return type;
		boolean is_integer=true;
		boolean is_float=true;
		boolean is_boolean=true;
		String value=obj.toString().toLowerCase().trim();
		if (StringHelper.isEmpty(value))
			return DataType.STRING;
		if (!MathHelper.isFloat(value))
			is_float=false;
		if (!MathHelper.isInteger(value))
			is_integer=false;
		if (!"true".equals(value) && !"false".equals(value))
			is_boolean=false;
		// determine data type
		if (is_boolean)
			return DataType.BOOLEAN;
		if (is_integer)
			return DataType.INTEGER;
		if (is_float)
			return DataType.FLOAT;			
		return DataType.STRING;
	}
	
	public static DataType guessDataTypeByClass(Object obj)
	{
		if (obj instanceof String)
			return DataType.STRING;
		else if (obj instanceof Integer)
			return DataType.INTEGER;
		else if (obj instanceof Float || obj instanceof Double)
			return DataType.FLOAT;
		else if (obj instanceof Boolean)
			return DataType.BOOLEAN;
		else if (obj instanceof LocalDate)
			return DataType.DATE;
		else if (obj instanceof Byte)
			return DataType.STRING;
		//else if (obj.getClass().getSuperclass().toString().equals("java.lang.Enum"))
		//	return DataType.STRING;
		//if (obj!=null)
		//	throw new CException("no guessDataType handler for class "+obj.getClass().getName()+": ["+obj.toString()+"]");
			//System.err.println("no guessDataType handler for class "+obj.getClass().getName());
		return DataType.STRING;
	}
	
	public static DataType getDataTypeByPropertyType(Class<?> cls)
	{
		if (cls==null)
			return DataType.STRING;
		String classname=cls.getName();
		if ("java.lang.String".equals(classname))
			return DataType.STRING;
		else if ("java.lang.Integer".equals(classname) || "int".equals(classname))
			return DataType.DOUBLE;
		else if ("java.lang.Double".equals(classname) || "double".equals(classname))
			return DataType.FLOAT;
		else if ("java.lang.Float".equals(classname) || "float".equals(classname))
			return DataType.FLOAT;
		else if ("java.lang.Boolean".equals(classname) || "boolean".equals(classname))
			return DataType.BOOLEAN;
		else if ("java.time.LocalDate".equals(classname))
			return DataType.DATE;
		else if ("java.util.Map".equals(classname))
			return DataType.MAP;
		else if ("java.util.List".equals(classname))
			return DataType.LIST;
		else if (cls.isEnum())
			return DataType.ENUM;
		System.out.println("no handler for class "+classname);
		return DataType.STRING;
	}
}

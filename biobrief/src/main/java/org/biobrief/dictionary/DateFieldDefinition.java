package org.biobrief.dictionary;

import java.time.LocalDate;
import java.util.Map;

import org.biobrief.util.BeanHelper;

public class DateFieldDefinition extends FieldDefinition
{		
	public DateFieldDefinition(EntityDefinition entityType, Map<String, String> map)
	{		
		super(FieldType.DATE, entityType, map);
	}
	
	@Override
	public String getDeclaredType()
	{
		return "Date";
	}

	@Override
	protected String getDefaultConverter()
	{
		return "DATE";
	}
	
	@Override
	protected String getSqlType()
	{
		return "TIMESTAMP"+getSqlConstraints();
	}
	
	@Override
	public Object getProperty(Object obj, String path)
	{
		Object value=BeanHelper.getProperty(obj, path, null);
		if (value!=null && value instanceof LocalDate)
			return value;
		return value;
	}
}

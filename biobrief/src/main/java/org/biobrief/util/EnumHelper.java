package org.biobrief.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class EnumHelper
{
	private static final Logger logger=LoggerFactory.getLogger(EnumHelper.class);
	
	private EnumHelper(){}
	
	// finds an enum inside of a class, e.g. Constants.Period
	@SuppressWarnings("rawtypes")
	public static Class findEnumClass(Class maincls, String name)
	{
		for (Class<?> cls : maincls.getDeclaredClasses())
		{
			if (cls.isEnum() && cls.getSimpleName().equals(name))
				return cls;
		}
		throw new CException("cannot find enum class: "+name);
	}
	
	// checks to see if a class has the specified enum name
	@SuppressWarnings("rawtypes")
	public static boolean hasEnumClass(Class maincls, String name)
	{
		for (Class<?> cls : maincls.getDeclaredClasses())
		{
			if (cls.isEnum() && cls.getSimpleName().equals(name))
				return true;
		}
		return false;
	}
	
	//////////////////////////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	public static boolean hasEnum(Class<?> type, String name)
	{
		Enum enm=findEnum(type, name, false);
		return enm!=null;
	}
	
	@SuppressWarnings("rawtypes")
	public static Enum findEnum(Class<?> type, String name, boolean strict)
	{
		for (Object cnstnt : Arrays.asList(type.getEnumConstants()))
		{
			Enum constant=(Enum)cnstnt;
			if (name.equals(constant.name()))
				return constant;
		}
		if (strict)
			throw new CException("cannot find enum named "+name+" in type "+type.getCanonicalName());
		return null;
	}
	
	///////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	public static boolean hasEnumValue(Class<?> type, String value)
	{
		Enum enm=findEnumByValue(type, value, false);
		return enm!=null;
	}
	
	// checks the string 
	@SuppressWarnings("rawtypes")
	public static Enum findEnumByValue(Class<?> type, String value, boolean strict)
	{
		for (Object cnstnt : Arrays.asList(type.getEnumConstants()))
		{
			Enum constant=(Enum)cnstnt;
			if (value.equals(constant.toString()))
				return constant;
		}
		if (strict)
			throw new CException("cannot find enum with value "+value+" in type "+type.getCanonicalName());
		return null;
	}
	
	/////////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	public static Map<String,List<?>> getEnums(Class maincls)
	{
		Map<String,List<?>> map=Maps.newLinkedHashMap();
		for (Class<?> cls : maincls.getDeclaredClasses())
		{
			if (!cls.isEnum())
				continue;
			//logger.debug("declared class: "+cls.getName());
			map.put(cls.getSimpleName(), getEnumValues(cls));
		}
		//logger.debug(StringHelper.toString(map.keySet()));
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Map<String,String>> getEnumValues(Class cls)
	{
		if (!cls.isEnum())
			throw new CException("Class is not an enum: "+cls.getSimpleName());
		List<Map<String,String>> list=Lists.newArrayList();
		for (Object constant : Arrays.asList(cls.getEnumConstants()))
		{
			getEnumProperties((Enum)constant, list);
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private static void getEnumProperties(Enum constant, List<Map<String,String>> list)
	{
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("value",constant.name());
		map.put("display",constant.toString());
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(constant);
			for (PropertyDescriptor property : wrapper.getPropertyDescriptors())
			{
				Method method=property.getReadMethod();
				if (property.getName().equals("class") || property.getName().equals("declaringClass"))
					continue;
				//logger.debug("method"+method.getName());
				Object value=method.invoke(constant);
				if (value!=null)
					map.put(property.getName(),value.toString());		
			}
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
		list.add(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static List<String> getNames(Class cls)
	{
		if (!cls.isEnum())
			throw new CException("Class is not an enum: "+cls.getSimpleName());
		List<String> names=Lists.newArrayList();
		for (Object constant : Arrays.asList(cls.getEnumConstants()))
		{
			names.add(((Enum)constant).name());
		}
		logger.debug("names="+StringHelper.toString(names));
		return names;
	}
	
//	@SuppressWarnings("rawtypes")
//	public static List<Map<String,Object>> getEnumList(Class maincls)
//	{
//		List<Map<String,Object>> list=Lists.newArrayList();
//		for (Class<?> cls : maincls.getDeclaredClasses())
//		{
//			if (!cls.isEnum())
//				continue;
//			for (Object cnstnt : Arrays.asList(cls.getEnumConstants()))
//			{
//				Enum constant=(Enum)cnstnt;
//				Map<String,Object> map=Maps.newLinkedHashMap();
//				map.put("value", constant.name());//id
//				map.put("label", replaceNotSet(constant.toString())); //name
//				map.put("type", cls.getSimpleName());
//				list.add(map);
//			}
//		}
//		return list;
//	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String,Object>> getEnumList(Class maincls)
	{
		List<Map<String,Object>> list=Lists.newArrayList();
		getEnumList(maincls, list);
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Map<String,Object>> getEnumList(List<Class> enumclasses)
	{
		List<Map<String, Object>> list=EnumHelper.getEnumList(Constants.class);
		for (Class enumclass : enumclasses)
		{
			EnumHelper.getEnumList(enumclass, list);
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public static void getEnumList(Class maincls, List<Map<String,Object>> list)
	{
		for (Class<?> cls : maincls.getDeclaredClasses())
		{
			if (!cls.isEnum())
				continue;
			if (!cls.isAnnotationPresent(ExportedEnum.class))
				continue;
			for (Object cnstnt : Arrays.asList(cls.getEnumConstants()))
			{
				Enum constant=(Enum)cnstnt;
				Map<String,Object> map=Maps.newLinkedHashMap();
				String value=constant.name();
				String label=replaceNotSet(constant.toString());
				
				map.put("value", value);
				if (!value.equals(label))
					map.put("label", label);
				map.put("type", cls.getSimpleName());
				for (Field field : cls.getDeclaredFields())
				{
					if (field.isAnnotationPresent(ExportedField.class))
					{
						//System.out.println("readable property: "+field.getName());
						field.setAccessible(true);
						map.put(field.getName(), StringHelper.dflt(getValue(field, constant)));
					}
				}
				list.add(map);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Object getValue(Field field, Enum constant)
	{
		try
		{
			return field.get(constant);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String replaceNotSet(String name)
	{
		if (name.equals("NULL"))
			return "";
		else return name;
	}
	
	@SuppressWarnings("rawtypes")
	public static String join(Collection<? extends Enum> collection, String delimiter)
	{
		List<String> values=Lists.newArrayList();
		for (Enum enm : collection)
		{
			values.add(enm.name());
		}
		return StringHelper.join(values, delimiter);
	}
	
	//////////////////////////////////////////////////////////////////
	
	//https://dzone.com/articles/java-enum-lookup-by-name-or-field-without-throwing?edition=298008&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202017-05-07
	public static <T, E extends Enum<E>> Function<T, E> lookupMap(Class<E> clazz, Function<E, T> mapper)
	{
		@SuppressWarnings("unchecked")
		E[] emptyArray = (E[]) Array.newInstance(clazz, 0);
		return lookupMap(EnumSet.allOf(clazz).toArray(emptyArray), mapper);
	}
	
	public static <T, E extends Enum<E>> Function<T, E> lookupMap(E[] values, Function<E, T> mapper)
	{
		Map<T, E> index = Maps.newHashMapWithExpectedSize(values.length);
		for (E value : values)
		{
			index.put(mapper.apply(value), value);
		}
		return (T key) -> index.get(key);
	}
}

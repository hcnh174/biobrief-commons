package org.biobrief.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.reflections.ReflectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
//import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BeanHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(BeanHelper.class);
	
	private static BeanHelper instance;
	
	public static BeanHelper getInstance()
	{
		if (instance==null)
			instance=new BeanHelper();
		return instance;
	}	
	
	private List<String> datepatterns=Lists.newArrayList();
	private ExpressionParser parser=new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED, null));
	
	public BeanHelper()
	{
		addDatePattern(LocalDateHelper.YYYYMMDD_PATTERN);
		addDatePattern(LocalDateHelper.MMDDYYYY_PATTERN);
	}
	
	public BeanHelper(String datepattern)
	{
		this.addDatePattern(datepattern);
	}
	
	public void addDatePattern(String datepattern)
	{
		this.datepatterns.add(datepattern);
	}
	
	public void copyPropertiesAsString(Object target, Object src)
	{
		if (target==null)
			throw new CException("BeanHelper.copy(): target object is null");
		if (src==null)
			throw new CException("BeanHelper.copy(): source object is null");
		try
		{
			BeanUtils.copyProperties(src, target);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println("target="+target.getClass().getCanonicalName()+", source="+src.getClass().getCanonicalName());
			//FileHelper.appendFile(logfile, e.getMessage());
		}
	}
	
	//https://stackoverflow.com/questions/19737626/how-to-ignore-null-values-using-springframework-beanutils-copyproperties
	public static String[] getNullPropertyNames(Object source)
	{
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		
		Set<String> emptyNames = new HashSet<String>();
		for(java.beans.PropertyDescriptor pd : pds)
		{
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	// then use Spring BeanUtils to copy and ignore null using our function
	public static void copyNonNullProperties(Object target, Object src)
	{
		BeanUtils.copyProperties(target, src, getNullPropertyNames(src));
	}
	
	// then use Spring BeanUtils to copy and ignore null using our function
//	public static void copyPropertiesIgnoreNull(Object src, Object target)
//	{
//		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
//	}
	
	public void copyProperties(Object target, Object src)
	{
		if (target==null)
			throw new CException("BeanHelper.copy(): target object is null");
		if (src==null)
			throw new CException("BeanHelper.copy(): source object is null");
		try
		{
			BeanUtils.copyProperties(src, target);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println("target="+target.getClass().getCanonicalName()+", source="+src.getClass().getCanonicalName());
			//FileHelper.appendFile(logfile, e.getMessage());
		}
	}
	
	public boolean copyProperties(Object target, Object src, Collection<String> ignore)
	{
		String[] ignoreProperties=new String[ignore.size()];
		ignore.toArray(ignoreProperties);
		copyProperties(target, src, ignoreProperties);
		return true;
	}
	
	public boolean copyProperties(Object target, Object src, String...ignore)
	{
		if (target==null)
			throw new CException("BeanHelper.copy(): target object is null");
		if (src==null)
			throw new CException("BeanHelper.copy(): source object is null");
		try
		{
			BeanUtils.copyProperties(src, target, ignore);
			return true;
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println("target="+target.getClass().getCanonicalName()+", source="+src.getClass().getCanonicalName());
			return false;
		}	
	}
	
	public static Object getProperty(Object target, String property)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			return wrapper.getPropertyValue(property);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			//FileHelper.appendFile(logfile, e.getMessage());
			return null;
		}
	}
	
	public static Object getProperty(Object target, String property, Object dflt)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			return wrapper.getPropertyValue(property);
		}
		catch(Exception e)
		{
			//System.err.println(e.getMessage());
			//FileHelper.appendFile(logfile, e.getMessage());
			return dflt;
		}
	}
	
	public static boolean setProperty(Object target, String property, Object value)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			//wrapper.registerCustomEditor(LocalDate.class, new CustomDateEditor(new SimpleDateFormat(datepattern), true));
			if (!wrapper.isWritableProperty(property))
				return false;
			wrapper.setPropertyValue(property, value);
			return true;
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			//FileHelper.appendFile(logfile, e.getMessage());
			return false;
		}
	}
	
	public static boolean isReadable(Object target, String property)
	{
		BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
		return wrapper.isReadableProperty(property);
	}
	
	public static boolean isWritable(Object target, String property)
	{
		BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
		return wrapper.isWritableProperty(property);
	}
	
//	public boolean setPropertyFromString(Object target, String property, String value)
//	{
//		try
//		{
//			value=StringHelper.trim(value);
//			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
//			wrapper.setPropertyValue(property, value);
//			return true;
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			//FileHelper.appendFile(logfile, e.getMessage());
//			return false;
//		}
//	}
	
//	public boolean setPropertyFromString(Object target, String property, String value, String datepattern)
//	{
//		try
//		{
//			value=StringHelper.trim(value);
//			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
//			wrapper.registerCustomEditor(LocalDate.class, new CustomDateEditor(new SimpleDateFormat(datepattern), true));
//			wrapper.setPropertyValue(property, value);
//			return true;
//		}
//		catch(Exception e)
//		{
//			System.err.println(e.getMessage());
//			//FileHelper.appendFile(logfile, e.getMessage());
//			return false;
//		}
//	}
	
	public boolean setPropertiesFromStrings(Object obj, Map<String, String> map, boolean strict)
	{
		for (String field : map.keySet())
		{
			setPropertyFromString(obj, field, map.get(field), strict);
		}
		return true;
	}
	
	public boolean setPropertyFromString(Object obj, String property, String value)
	{
		return setPropertyFromString(obj, property, value, false);
	}
	
	public boolean setPropertyFromString(Object obj, String property, String value, boolean strict)
	{
		if (obj==null)
			return false;
		if (!isWritableProperty(obj, property))
		{
			if (strict)
				throw new CException("property \""+property+"\" not found on "+obj);
			return false;
		}			
		Class<?> cls=getPropertyType(obj, property);
		return setPropertyFromString(obj, property, value, cls);
	}

	public boolean setPropertyFromString(Object obj, String property, String value, Class<?> cls)
	{
		if (obj==null || cls==null)
			return false;
		DataType type=DataType.getDataTypeByPropertyType(cls);
		//System.out.println("setPropertyFromString obj="+obj.getClass().getName()+" property="+property+" value="+value+" cls="+cls+" type="+type);
		switch(type)
		{
		case STRING:
			return setProperty(obj, property, value);
		case MAP:
		case LIST:
			return setProperty(obj, property, value);
		case INTEGER:
			return setProperty(obj, property, MathHelper.parseInt(value));
		case DOUBLE:
			return setProperty(obj, property, MathHelper.parseDouble(value));
		case FLOAT:
			return setProperty(obj, property, MathHelper.parseFloat(value));
		case BOOLEAN:
			return setProperty(obj, property, MathHelper.parseBoolean(value));
		case DATE:
			return setProperty(obj, property, LocalDateHelper.parseDate(value, datepatterns, false));
		case ENUM:
			return setEnumProperty(obj, property, value, cls);
		default:
			throw new CException("no handler for data type: "+type);
		}
	}
	
	public boolean setEnumProperty(Object obj, String property, String value, Class<?> cls)
	{
		if (value.equals(""))
			return false;
		for (Object constant : Arrays.asList(cls.getEnumConstants()))
		{
			//logger.debug("constant value="+constant.toString());
			if (constant.toString().equals(value))
			{
				//logger.debug("found value: constant="+constant+" value="+value);
				setProperty(obj, property, constant);
				return true;
			}
		}
		throw new CException("can't find enum property: "+property+"="+value+" for class "+cls.getName());
	}
	
	public static boolean isReadableProperty(Object target, String property)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			return wrapper.isReadableProperty(property);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static boolean isWritableProperty(Object target, String property)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			return wrapper.isWritableProperty(property);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static Class<?> getPropertyType(Object target, String property)
	{
		try
		{
			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(target);
			return wrapper.getPropertyType(property);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			//FileHelper.appendFile(logfile, e.getMessage());
			return Class.class;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// for direct field access of private or public fields (case-insensitive, cached, and class-specific)
	//////////////////////////////////////////////////////////////////////////////////////
	
	private Map<String, Field> fieldmap=Maps.newHashMap();

	public void setFields(Object obj, Map<String, String> props)
	{
		for (String field : props.keySet())
		{
			setField(obj, field, props.get(field));
		}
	}
	
	public void setField(Object obj, String name, String value)
	{
		try
		{
			value=StringHelper.normalize(value);
			if (!StringHelper.hasContent(value))
				return;
			Field field = getFieldAccessor(obj, name);
			if (field==null)
				return;
			field.set(obj, value);
		}
		catch (Exception e)
		{
			StringHelper.println("Can't set field: "+name+": "+e);
		}
	}
	
	public void setField(Object obj, String name, Number value)
	{
		try
		{
			if (value==null)
				return;
			Field field = getFieldAccessor(obj, name);
			if (field==null)
				return;
			field.set(obj, value);
		}
		catch (Exception e)
		{
			StringHelper.println("Can't set field: "+name+": "+e);
		}
	}

	public void setField(Object obj, String name, LocalDate value)
	{
		try
		{
			if (value==null)
				return;
			Field field = getFieldAccessor(obj, name);
			if (field==null)
				return;
			field.set(obj, value);
		}
		catch (Exception e)
		{
			StringHelper.println("Can't set field: "+name+": "+e);
		}
	}
	
	public void setField(Object obj, String name, Object value)
	{
		try
		{
			if (value==null)
				return;
			Field field = getFieldAccessor(obj, name);
			if (field==null)
				return;
			field.set(obj, value);
		}
		catch (Exception e)
		{
			StringHelper.println("Can't set field: "+name+": "+e);
		}
	}
	
	public Object getField(Object obj, String name)
	{
		try
		{
			Field field = getFieldAccessor(obj, name);
			if (field==null)
				return null;
			return field.get(obj);
		}
		catch (Exception e)
		{
			StringHelper.println("Can't get field: "+name+": "+e);
			return null;
		}
	}
	
	private Field getFieldAccessor(Object obj, String name) throws NoSuchFieldException
	{
		if (!StringHelper.hasContent(name))
		{
			StringHelper.println("Field name is null or empty: "+name);
			return null;
		}
		String key=getFieldAccessorKey(obj, name);
		if (!fieldmap.containsKey(key))
			cacheFieldAccessors(obj);
		Field field=fieldmap.get(key);
		if (field==null)
		{
			StringHelper.println("Can't find field: "+name);
			return null;
		}
		return field;
	}
		
	private void cacheFieldAccessors(Object obj)
	{
		String classkey=obj.getClass().getCanonicalName().toLowerCase();
		if (fieldmap.containsKey(classkey))
			return;
		//StringHelper.println("caching field accessors for class: "+obj.getClass().getName());
		for (Field field : obj.getClass().getDeclaredFields())
		{
			String key=getFieldAccessorKey(obj, field.getName());
			//StringHelper.println("caching field accessor: "+key);
			field.setAccessible(true);
			fieldmap.put(key, field);
		}
		fieldmap.put(classkey, null); // hack!
	}
	
	private String getFieldAccessorKey(Object obj, String field)
	{
		String key=obj.getClass().getCanonicalName()+":"+field;
		key=key.toLowerCase();
		return key;
	}	
	
	
	public static boolean isDate(Object obj, String property)
	{
		Class<?> cls=getPropertyType(obj, property);
		if (cls==null)
			return false;
		//return cls.getName().equals("java.util.Date") || cls.getName().equals("java.time.LocalDate");
		return isDate(cls);
	}
	
	public static boolean isDate(Class<?> cls)
	{
		return cls.getName().equals("java.util.Date") || cls.getName().equals("java.time.LocalDate");
	}
	
	///////////////////////////////////////////////////////////////////////
	
//	public static void setValue(Object obj, String property, String value)
//	{
//		ExpressionParser parser=new SpelExpressionParser();
//		StandardEvaluationContext simpleContext=new StandardEvaluationContext(obj);
//		parser.parseExpression(property).setValue(simpleContext, value);
//	}
//	
//	public static Object getValue(Object obj, String property)
//	{
//		ExpressionParser parser=new SpelExpressionParser();
//		StandardEvaluationContext simpleContext=new StandardEvaluationContext(obj);
//		return parser.parseExpression(property).getValue(simpleContext);
//	}
	
	
	//http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html
	//https://www.mkyong.com/spring3/spring-el-lists-maps-example/
	//http://www.baeldung.com/spring-expression-language
	public void setValue(Object obj, String property, String value)
	{
		StandardEvaluationContext context=new StandardEvaluationContext(obj);
		parser.parseExpression(property).setValue(context, value);
	}
	
	public void setValueLenient(Object obj, String property, String value)
	{
		StandardEvaluationContext context=new StandardEvaluationContext(obj);
		SpelParserConfiguration config = new SpelParserConfiguration(true, true);
		ExpressionParser parser = new SpelExpressionParser(config);
		parser.parseExpression(property).setValue(context, value);
	}
	
	public void setValue(Object obj, String property, String value, boolean strict)
	{
		if (strict)
			setValue(obj, property, value);
		else setValueLenient(obj, property, value);
	}
	
	public void setValueIfEmpty(Object obj, String property, String value)
	{
		StandardEvaluationContext context=new StandardEvaluationContext(obj);
		SpelParserConfiguration config = new SpelParserConfiguration(true, true);
		ExpressionParser parser = new SpelExpressionParser(config);
		Object oldvalue=parser.parseExpression(property).getValue(context);
		if (!StringHelper.hasContent(oldvalue))
			parser.parseExpression(property).setValue(context, value);
	}
	
	public Object getValue(Object obj, String property)
	{
		StandardEvaluationContext context=new StandardEvaluationContext(obj);
		return parser.parseExpression(property).getValue(context);
	}	
	
	//////////////////////////////////////////////////////////////////////////

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static Set<String> getFields(Class cls)
//	{		
//		Set<String> names=Sets.newLinkedHashSet();
//		Set<Field> fields=ReflectionUtils.getAllFields(cls);
//		for (Field field : fields)
//		{
//			names.add(field.getName());
//		}
//		return names;
//	}
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static Set<String> getFieldsWithAnnotation(Class cls, Class annotationCls)
//	{		
//		Set<String> names=Sets.newLinkedHashSet();
//		Set<Field> fields=ReflectionUtils.getAllFields(cls, ReflectionUtils.withAnnotation(annotationCls));
//		for (Field field : fields)
//		{
//			names.add(field.getName());
//		}
//		return names;
//	}
	
	///////////////////////////
	
	public static boolean isTransient(Field field)
	{
		return Modifier.isTransient(field.getModifiers());
	}
	
//	public static Map<String, Object> getPropertyMap(Object obj)
//	{
//		Map<String, Object> map=Maps.newLinkedHashMap();
//		for (Field field : obj.getClass().getDeclaredFields())
//		{
//			try
//			{
//				if (field.getName().equals("this$0"))
//					continue;
//				if (isTransient(field))
//					continue;
//				boolean accessible=field.isAccessible();
//				field.setAccessible(true);
//				String name=field.getName();
//				Object value=field.get(obj);
//				map.put(name, value);
//				field.setAccessible(accessible);
//			}
//			catch (Exception e)
//			{
//				System.err.println(e);
//			}
//		}
//		return map;
//	}
	
//	public static Map<String, Object> getPropertyMap(Object obj)
//	{
//		Map<String, Object> map=Maps.newLinkedHashMap();
//		for (Field field : obj.getClass().getDeclaredFields())
//		{
//			try
//			{
//				if (field.getName().equals("this$0"))
//					continue;
//				if (isTransient(field))
//					continue;
//				String name=field.getName();
//				map.put(name, getValue(obj, name));
////				boolean accessible=field.isAccessible();
////				field.setAccessible(true);
////				String name=field.getName();
////				Object value=field.get(obj);
////				map.put(name, value);
////				field.setAccessible(accessible);
//			}
//			catch (Exception e)
//			{
//				System.err.println(e);
//			}
//		}
//		return map;
//	}
	
//	public boolean setField(Object target, String property, Object value)
//	{
//		try
//		{
//			ConfigurablePropertyAccessor accessor=PropertyAccessorFactory.forDirectFieldAccess(this);
//			accessor.setPropertyValue(property, value);
//			return true;
//		}
//		catch(Exception e)
//		{
//			throw(new CException(e));
//			//System.err.println(e.getMessage());
//			//return false;
//		}
//	}
//	
//	public Object getField(Object target, String property)
//	{
//		try
//		{
//			ConfigurablePropertyAccessor accessor=PropertyAccessorFactory.forDirectFieldAccess(this);
//			return accessor.getPropertyValue(property);
//		}
//		catch(Exception e)
//		{
//			throw(new CException(e));
//			//System.err.println(e.getMessage());
//			//return null;
//		}
//	}
	
//	public static List<String> getProperties(Object obj)
//	{
//		List<String> properties=new ArrayList<String>();
//		try
//		{
//			BeanWrapper wrapper=PropertyAccessorFactory.forBeanPropertyAccess(obj);
//			for (PropertyDescriptor property : wrapper.getPropertyDescriptors())
//			{
//				properties.add(property.getName());
//			}
//			return properties;
//		}
//		catch(Exception e)
//		{
//			throw new CException(e);
//			//FileHelper.appendFile(logfile, e.getMessage());
//		}
//	}
//	
//	public static List<String> getProperties(Object obj)
//	{
//		BeanPropertySqlParameterSource props=new BeanPropertySqlParameterSource(obj);
//		return StringHelper.asList(props.getReadablePropertyNames());
//	}
	
	public static List<String> getReadableProperties(Object obj)
	{
		List<String> names=Lists.newArrayList();
		BeanWrapper beanWrapper=PropertyAccessorFactory.forBeanPropertyAccess(obj);
		for (PropertyDescriptor property : beanWrapper.getPropertyDescriptors())
		{
			if (property.getName().equals("class"))
				continue;
			if (beanWrapper.isReadableProperty(property.getName()))
				names.add(property.getName());
		}
		return names;
	}
	
	public static List<String> getReadWriteProperties(Object obj)
	{
		List<String> names=Lists.newArrayList();
		BeanWrapper beanWrapper=PropertyAccessorFactory.forBeanPropertyAccess(obj);
		for (PropertyDescriptor property : beanWrapper.getPropertyDescriptors())
		{
			String name=property.getName();
			if (name.equals("class"))
				continue;
			if (beanWrapper.isReadableProperty(name) && beanWrapper.isWritableProperty(name))
				names.add(property.getName());
		}
		return names;
	}
	
	////////////////////////////////////////////////////////////
	
	public static Map<?, ?> flatten(Object obj)
	{
		try
		{
			JavaPropsMapper mapper = new JavaPropsMapper();
			JavaPropsSchema schema = JavaPropsSchema.emptySchema()
					.withFirstArrayOffset(0)
					.withWriteIndexUsingMarkers(true);
			Map<Object, Object> map=(Map<Object, Object>)mapper.writeValueAsProperties(obj, schema);
			return map;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
//	//@SuppressWarnings("unchecked")
//	public static Map<?, ?> flatten(Object obj)
//	{
//		try
//		{
//			JavaPropsMapper mapper = new JavaPropsMapper();
//			Properties properties = mapper.writeValueAsProperties(obj);
//			Map<Object, Object> map = properties;
//			return map;
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	public static Map<String, String> flatten(Object obj)
//	{
//		try
//		{
//			return org.apache.commons.beanutils.BeanUtils.describe(obj);
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
	//https://stackoverflow.com/questions/25627913/flattening-java-bean-to-a-map
//	@SuppressWarnings("unchecked")
//	public static Map flatten(Object obj)
//	{
//		ObjectMapper objectMapper = new ObjectMapper();
//		Map<String, Object> map = objectMapper.convertValue(obj, Map.class);
//		return map;
//	}
	
//	public static Map<String, Object> flatten(Object obj)
//	{
//		try
//		{
//			return BeanFlattener.deepToMap(obj);
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
	////////////////////////////////////////////////////////////
	
//	public static Object forName(String cls)
//	{
//		try
//		{
//			return Class.forName(cls);
//		}
//		catch (ClassNotFoundException e)
//		{
//			throw new CException(e);
//		}
//	}
	
	//https://stackoverflow.com/questions/10119956/getting-class-by-its-name
	public static Class<?> lookupClass(String name)
	{
		try
		{
			Class<?> cls=Class.forName(name);
			return cls;
		}
		catch(ClassNotFoundException e)
		{
			throw new CException(e);
		}
	}
	
//	public static Object newInstance(Class<?> cls)
//	{
//		try
//		{
//			return cls.getDeclaredConstructor().newInstance();//return cls.newInstance();
//		}
//		catch(Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
	public static Object instantiateClass(Class<?> cls)
	{
		try
		{
			return BeanUtils.instantiateClass(cls);
		}
		catch(Exception e)
		{
			return new CException("cannpt instantiate class: "+cls.getName());
		}
	}
	
	//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/BeanUtils.html#findPrimaryConstructor-java.lang.Class-
	//http://tutorials.jenkov.com/java-reflection/constructors.html
	public static Object instantiateClass(Class<?> cls, Object arg)
	{
		try
		{
			Constructor<?> constructor=cls.getConstructor(new Class[]{arg.getClass()});
			return BeanUtils.instantiateClass(constructor, arg);
		}
		catch(Exception e)
		{
			System.out.println("cannot instantiate class: "+cls.getName()+" with args "+arg);
			return new CException("cannot instantiate class: "+cls.getName()+" "+arg.getClass().getName());
		}
	}
	
	public static Object instantiateClass(Class<?> cls, Object arg1, Object arg2)
	{
		try
		{
			Constructor<?> constructor=cls.getConstructor(new Class[]{arg1.getClass(), arg2.getClass()});
			return BeanUtils.instantiateClass(constructor, arg1, arg2);
		}
		catch(Exception e)
		{
			return new CException("cannot instantiate class: "+cls.getName()+" "+arg1.getClass().getName()+" "+arg2.getClass().getName());
		}
	}
	
	public static Object instantiateClass(Class<?> cls, Object arg1, Object arg2, Object arg3)
	{
		try
		{
			Constructor<?> constructor=cls.getConstructor(new Class[]{arg1.getClass(), arg2.getClass(), arg3.getClass()});
			return BeanUtils.instantiateClass(constructor, arg1, arg2, arg3);
		}
		catch(Exception e)
		{
			return new CException("cannot instantiate class: "+cls.getName()+" "+arg1.getClass().getName()+" "+arg2.getClass().getName()+" "+arg3.getClass().getName());
		}
	}
	
	//https://stackoverflow.com/questions/25627913/flattening-java-bean-to-a-map
//	public static final class BeanFlattener
//	{
//		private BeanFlattener() {}
//
//		public static Map<String, Object> deepToMap(Object bean)
//				throws IllegalAccessException
//		{
//			Map<String, Object> map = new LinkedHashMap<>();
//			putValues(bean, map, null);
//			return map;
//		}
//
//		private static void putValues(Object bean, Map<String, Object> map, String prefix)
//				throws IllegalAccessException
//		{
//			Class<?> cls = bean.getClass();
//			for(Field field : cls.getDeclaredFields())
//			{
//				field.setAccessible(true);
//				Object value = field.get(bean);
//				String key;
//				if(prefix == null)
//					key = field.getName();
//				else key = prefix + "." + field.getName();
//				if(isValue(value))
//					map.put(key, value);
//				else putValues(value, map, key);
//			}
//		}
//
//		private static final Set<Class<?>> valueClasses = (
//			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
//				Object.class, 	String.class, Boolean.class, 
//				Character.class, Byte.class, Short.class, 
//				Integer.class, Long.class, Float.class, 
//				Double.class
//			)))
//		);
//
//		private static boolean isValue(Object value)
//		{
//			return value == null || valueClasses.contains(value.getClass());
//		}
//	}
}

/*
BeanHelper helper=new BeanHelper();
//BeanHelper helper=new BeanHelper();
PegribaPatient patient=new PegribaPatient();
String field1="IFN既往";
String field2="fsfsdf";
//helper.setField(patient, field1, "test");
helper.setField(patient, field2, "test");
helper.setField(patient, field2, "test");
helper.setField(patient, field1, "test");
logger.debug("patient.ifn既往="+helper.getField(patient, field1));//patient.ifn既往);
*/
package org.biobrief.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class JsHelper
{
	private JsHelper(){}

	public static String format(String js)
	{
		return JsBeautifier.jsBeautify(js);
	}
	
	////////////////////////////////////////////////////
	
	public abstract static class JsObject
	{
		public Map<String,Object> getParams()
		{
			Map<String,Object> params=Maps.newLinkedHashMap();
			getParams(params);
			return params;
		}
		
		protected void getParams(Map<String,Object> params){}
		
		public void setParam(Map<String,Object> params, String name, JsList<?> list)
		{
			params.put(name, list.getItems());
		}
		
		public void setParam(Map<String,Object> params, String name, JsMap<?> map)
		{
			params.put(name, map.getMap());
		}
		
		public void setParam(Map<String,Object> params, String name, Object value)
		{
			if (StringHelper.hasContent(value))
				params.put(name,value);
		}
		
		public void setStringParam(Map<String,Object> params, String name, Object value)
		{
			if (StringHelper.hasContent(value))
				params.put(name,quote(value.toString()));
		}
		
		protected void setI18nStringParam(Map<String,Object> params, String name, Object value)
		{
			if (!StringHelper.hasContent(value))
				return;
			if (value.toString().startsWith("i18n"))
				setParam(params, name, "this."+value);
			else setStringParam(params, name, value);
		}
		
		public boolean hasParams()
		{
			return !getParams().isEmpty();
		}
	}

	public static class JsList<T extends JsObject> extends JsObject
	{
		protected List<T> items=Lists.newArrayList();
		
		public void add(T item)
		{
			items.add(item);
		}
		
		public void addAll(Collection<T> items)
		{
			this.items.addAll(items);
		}
		
		public List<Map<String, Object>> getItems()
		{
			List<Map<String, Object>> list=Lists.newArrayList();
			for (T item : items)
			{
				list.add(item.getParams());
			}
			return list;
		}
	}
	
	public static class JsMap<T extends JsObject> extends JsObject
	{
		protected Map<String, T> map=Maps.newLinkedHashMap();
		
		public void put(String name, T value)
		{
			map.put(name, value);
		}
		
		public Map<String, Object> getMap()
		{
			Map<String, Object> result=Maps.newLinkedHashMap();
			for (String name : map.keySet())
			{
				result.put(name,  map.get(name).getParams());
			}
			return result;
		}
	}
	
	public static class Function
	{
		protected String name;
		protected List<String> args=Lists.newArrayList();
		protected List<String> commands=Lists.newArrayList();
		
		public Function(){}
		
		public Function(String name)
		{
			this.name=name;
		}
		
		public Function(String name, List<String> args)
		{
			this(name);
			this.args.addAll(args);
		}
		
		public Function(String name, List<String> args, String body)
		{
			this(name, args);
			commands.add(body);
		}
		
		public Function setArgs(String... args)
		{
			this.args=Lists.newArrayList(args);
			return this;
		}
		
		public Function addCommand(String command)
		{
			if (!command.endsWith(";"))
				command=command+";";
			this.commands.add(command);
			return this;
		}
		
		public Function addCommands(List<String> commands)
		{
			this.commands.addAll(commands);
			return this;
		}
		
		public String toJavascript()
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("function(").append(StringHelper.join(args, ", ")).append(")\n");
			buffer.append("{").append(StringHelper.join(commands, "\n")).append("}\n");
			return buffer.toString();
		}
		
		public int getNumArgs(){return args.size();}
		public int getNumCommands(){return commands.size();}
		
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public List<String> getArgs(){return this.args;}
		public Function setArgs(final List<String> args){this.args=args; return this;}
	}
	
	
	//////////////////////////////////////////////////////
	
	public static String quote(String str)
	{
		if (str.startsWith("i18n"))
			return str;
		return StringHelper.singleQuote(str);
	}
	
	@SuppressWarnings("unchecked")
	public static String toJavascript(Object obj)
	{
		if (obj==null)
			throw new CException("null object passed to toJavascript");
		if (obj instanceof List)
			return listToJavascript((List<Map<String,?>>)obj);
		else if (obj instanceof Map)
			return mapToJavascript((Map<String,?>)obj);
		else return obj.toString();
	}
	
	private static String mapToJavascript(Map<String,?> params)
	{
		List<String> list=Lists.newArrayList();
		for (String name : params.keySet())
		{
			Object obj=params.get(name);
			list.add(name+": "+toJavascript(obj));
		}
		StringBuilder buffer=new StringBuilder();
		buffer.append("{");
		buffer.append(StringHelper.join(list,","));
		buffer.append("}");
		return buffer.toString();
	}
	
	private static String listToJavascript(List<?> array)
	{
		List<String> list=Lists.newArrayList();
		for (Object params : array)
		{
			list.add(toJavascript(params));
		}
		StringBuilder buffer=new StringBuilder();
		buffer.append("[");
		buffer.append(StringHelper.join(list,","));
		buffer.append("]");
		return buffer.toString();
	}
}

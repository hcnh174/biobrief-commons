package org.biobrief.dictionary;

import java.util.List;
import java.util.Map;

import org.biobrief.dictionary.MergeSources.MergeSource;
import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
//import org.biobrief.util.JpaHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MergeSourceMap
{
	private static final String FILTER_SYMBOL="|";
	private static final String DELIMITER=",";
	private static final String MANUAL="manual";
	
	private final MergeSources mergeSources;
	private final String field;
	private final Integer defaultPriority;
	private final Map<String, Integer> priorities=Maps.newLinkedHashMap();
	private final List<Source> sources=Lists.newArrayList();
	
	public MergeSourceMap(MergeSources mergeSources, Map<String, String> map)
	{
		this.mergeSources=mergeSources;
		this.field=map.get("name");
		String prioritylist=DictUtil.nullIfEmpty(map.get("priority"));
		this.defaultPriority=parsePriorities(prioritylist);
		for (String key : map.keySet())
		{
			if (isConverterSource(key))
				addMerge(key, map.get(key));
		}
	}
	
	private Integer parsePriorities(String str)
	{
		priorities.put(MANUAL, 0);
		int priority=1;
		if (str==null)
			return priority;
		for (String set : StringHelper.split(str,">"))
		{
			for (String item : StringHelper.split(set,","))
			{
				MergeSource mergesource=mergeSources.find(item);
				if (mergesource==null)
				{
					LogUtil.log("cannot find MergeSource value: "+item);
					throw new CException("cannot find MergeSource value: "+item);
				}
				priorities.put(mergesource.getName(), priority);
			}
			priority++;
		}
		return priority;
	}
	
	public List<Source> getSources(){return sources;}
	
//	public List<Source> getSources(IdType idType)
//	{
//		List<Source> list=Lists.newArrayList();
//		for (Source source : sources)
//		{
//			if (source.getSource().getIdentifier()==idType)
//				list.add(source);
//		}
//		return list;
//	}
	
	public Integer getPriority(String mergesource)
	{
		if (!priorities.containsKey(mergesource))
		{
			//System.out.println("field="+field+". no priority for mergesource: "+mergesource+" using defaultPriority: "+defaultPriority);
			return defaultPriority;
		}
		return priorities.get(mergesource);
	}
	
	public void addMerge(String source, String columns)
	{
		addMerge(mergeSources.find(source), columns);
	}
	
	// now allows a converter to be configured
	// 初診後転帰 | CONTAINS:外来follow
	// uses quotes if column has
	public void addMerge(MergeSource source, String columns)
	{
		if (!StringHelper.hasContent(columns) || isIgnored(columns))
			return;
		for (String column : StringHelper.split(columns, DELIMITER))
		{
			if (column.contains(FILTER_SYMBOL))
			{
				int index=column.indexOf(FILTER_SYMBOL);
				String colname=column.substring(0, index).trim();
				String converter=column.substring(index+1).trim();
				sources.add(new Source(source, colname, converter));
			}
			else sources.add(new Source(source, column));
		}
	}
	
	public boolean isIgnored(String column)
	{
		return (!StringHelper.hasContent(column) || column.startsWith("("));
	}
	
	// checks if the column name is a legacy entity
	public boolean isConverterSource(String name)
	{
		return mergeSources.find(name)!=null;
	}
	
	@Override
	public String toString()
	{
		return "MergeSource: "+
			" field="+field+
			" defaultPriority="+defaultPriority+
			"\npriority="+StringHelper.toString(priorities)+
			"\nsources="+StringHelper.join(sources);
	}
	
	public class Source
	{
		private final MergeSource source;
		private final String table;
		private final String column;
		private String converter="";
		
		public Source(MergeSource source, String column)
		{
			this.source=source;
			this.table=source.getName();
			this.column=column;
		}
		
		public Source(MergeSource source, String column, String converter)
		{
			this(source, column);
			this.converter=converter;
		}

		public String getField()
		{
			return field;
		}
		
		@Override
		public String toString()
		{
			return "MergeSourceMap$Source:"+
					" source="+source.getName()+
					" priority="+source.getPriority()+
					" table="+table+
					" converter="+converter;
		}
				
		public MergeSource getSource(){return this.source;}
		public String getTable(){return this.table;}
		public String getColumn(){return this.column;}
		public String getConverter(){return this.converter;}
	}
}

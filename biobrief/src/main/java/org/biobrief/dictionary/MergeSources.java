package org.biobrief.dictionary;

import java.util.Collection;
import java.util.Map;

import org.biobrief.util.DataFrame;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Maps;

public class MergeSources
{
	private Map<String, MergeSource> sources=Maps.newLinkedHashMap();
	
	public Collection<MergeSource> getSources(){return sources.values();}
	
	public MergeSources(String filename)
	{
		StringDataFrame dataframe=DataFrame.parseTabFile(filename);
		for (String rowname : dataframe.getRowNames())
		{
			Map<String, String> map=dataframe.getRowAsStrings(rowname);
			String name=map.get("name");
			String identifier=map.get("identifier");//IdType.valueOf(map.get("identifier"));
			Integer priority=DictUtil.asInteger(DictUtil.get(map, "priority"));//, 1);
			Boolean escape=DictUtil.asBoolean(DictUtil.get(map, "escaped"));//, true);
			Boolean wrapped=DictUtil.asBoolean(DictUtil.get(map, "wrapped"));//, true);
			MergeSource source=new MergeSource(name, identifier, priority, escape, wrapped);
			add(source);
		}
		//System.out.println("MERGESOURCES:\n"+toString());
	}
	
	public void add(MergeSource source)
	{
		sources.put(source.getName().toLowerCase(), source);
	}
	
	public MergeSource find(String value)
	{
		return sources.get(value.toLowerCase());
	}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
	
	public static class MergeSource
	{
		private String name;
		private String identifier;//IdType
		private Integer priority;
		private boolean escape=true;
		private boolean wrapped=true;

		MergeSource(String name, String identifier, Integer priority, boolean escape, boolean wrapped)
		{
			this.name=name;
			this.identifier=identifier;
			this.priority=priority;
			this.escape=escape;
			this.wrapped=wrapped;
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public String getName(){return name;}
		public String getIdentifier(){return identifier;}
		public Integer getPriority() {return priority;}
		public boolean getEscape(){return escape;}
		public boolean getWrapped(){return wrapped;}
	}
}

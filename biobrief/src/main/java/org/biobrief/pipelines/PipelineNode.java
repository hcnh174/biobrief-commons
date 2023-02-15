package org.biobrief.pipelines;

import java.util.List;
import java.util.Map;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.entities.Task;
import org.biobrief.pipelines.entities.Task.AbstractTaskParams;
import org.biobrief.pipelines.util.PipelineUtil;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PipelineNode
{
	protected String dir;
	protected int num;
	protected int level;
	protected TaskType type;
	protected String name;
	protected Map<String, String> params=Maps.newLinkedHashMap();
	@JsonIgnore protected PipelineNode parent;
	protected List<PipelineNode> children=Lists.newArrayList();
	protected TypeCounter counter=new TypeCounter();
	
	public void add(PipelineNode node)
	{
		node.setParent(this);
		this.children.add(node);
		Integer num=counter.add(node.getType());
		String name=node.getType().getFolder()+"_"+num;
		node.setName(name);
		node.setDir(FileHelper.normalize(dir+"/@"+name));
	}

	public void setParam(String name, String value)
	{
		this.params.put(name, value);
	}
	
	public void setParamIf(String name, String value)
	{
		if (!this.params.containsKey(name))
			this.setParam(name, value);
	}
	
	public String getFullName()
	{
		if (getNum()<1)
			return null;
		String fullName=parent.getFullName();
		if (StringHelper.hasContent(fullName))
			return fullName+"__"+name;
		return name;
	}
	
	public <P extends AbstractTaskParams> P createParams()
	{
		setParam("parentDir", parent.getDir());
		setParam("dir", dir);
		setParamIf("samples", parent.params.get("samples"));
		setParamIf("fastqMode", parent.params.get("fastqMode"));
		return PipelineUtil.createTaskParamsClass(type, params);
	}
	
	public <B extends Task> B createTask()
	{
		return PipelineUtil.createTaskClass(type, createParams());
	}
	
	protected int getMaxDepth()
	{
		int max=0;
		for (PipelineNode child : children)
		{
			int depth=child.getMaxDepth();
			if (depth>max)
				max=depth;
		}
		return max+1;
	}
	
	protected void getNodesAtDepth(int level, List<PipelineNode> list)
	{
		if (this.level==level)
		{
			list.add(this);
			return;
		}
		for (PipelineNode child : children)
		{
			child.getNodesAtDepth(level, list);
		}
	}
	
	public void setParams(Map<String, String> params)
	{
		this.params=params;
	}
	
	public int getNum(){return this.num;}
	public void setNum(final int num){this.num=num;}
	
	public String getDir(){return this.dir;}
	public void setDir(final String dir){this.dir=dir;}
	
	public String getName(){return this.name;}
	public void setName(final String name){this.name=name;}

	public int getLevel(){return this.level;}
	public void setLevel(final int level){this.level=level;}

	public TaskType getType(){return this.type;}
	public void setType(final TaskType type){this.type=type;}

	public PipelineNode getParent(){return this.parent;}
	public void setParent(final PipelineNode parent){this.parent=parent;}

	public List<PipelineNode> getChildren(){return this.children;}
	public void setChildren(final List<PipelineNode> children){this.children=children;}

	@Override
	public String toString()
	{
		List<String> pairs=Lists.newArrayList();
		for (String name : params.keySet())
		{
			String value=params.get(name);
			pairs.add(name+PipelineParser.DELIMITER+value);
		}
		StringBuilder buffer=new StringBuilder();
		buffer.append(StringHelper.repeatString(StringHelper.TAB, level));
		buffer.append(StringHelper.SPACE);
		buffer.append(type.name().toLowerCase());
		buffer.append(StringHelper.SPACE);
		buffer.append("name="+getName());
		buffer.append(StringHelper.SPACE);
		buffer.append(StringHelper.join(pairs, StringHelper.SPACE));
		buffer.append(StringHelper.NEWLINE);
		toStringChildren(buffer);
		return buffer.toString();
	}
	
	protected void toStringChildren(StringBuilder buffer)
	{
		for (PipelineNode child : children)
		{
			buffer.append(child.toString());
		}
	}
	
	protected void createTreeChildren(PipelineTree.Node parent)
	{
		for (PipelineNode child : children)
		{
			PipelineTree.Node treenode=new PipelineTree.Node(child);
			parent.add(treenode);
			child.createTreeChildren(treenode);
		}
	}
	
	private static class TypeCounter
	{
		private Map<TaskType, Integer> counts=Maps.newLinkedHashMap();
			
		public Integer add(TaskType type)
		{
			increment(type);
			return counts.get(type);
		}
		
		private void increment(TaskType type)
		{
			if (!counts.containsKey(type))
				counts.put(type, 1);
			else counts.put(type, counts.get(type)+1);
		}
	}
}
//public void initTasks()
//{
//	this.task=createTask();
//	//System.out.println("created batch job: "+JsonHelper.toJson(this.task));
//	initTasksChildren();
//}
//
//protected void initTasksChildren()
//{
//	for (PipelineNode child : children)
//	{
//		child.initTasks();
//	}
//}
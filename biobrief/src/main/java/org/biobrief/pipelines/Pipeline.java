package org.biobrief.pipelines;

import java.util.List;

import org.biobrief.pipelines.PipelineConstants.FastqMode;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.util.PipelineUtil;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class Pipeline extends PipelineNode
{
	private final FastqMode fastqMode;
	private final String winDir;
	private final List<String> samples;
	
	public Pipeline(String name, String dir, String winDir)
	{
		this.name=name;
		this.dir=dir;
		this.winDir=winDir;
		this.type=TaskType.IMPORT_FASTQ;
		this.level=-2;
		this.fastqMode=PipelineUtil.findFastqTypeInBaseDir(winDir);
		this.samples=PipelineUtil.findSamplesInBaseDir(winDir);
		
		PipelineNode rootnode=new PipelineNode();
		rootnode.setParam("samples", StringHelper.join(samples, ","));
		rootnode.setParam("fastqMode", fastqMode.name());
		rootnode.setType(TaskType.PIPELINE);
		rootnode.setNum(0);
		rootnode.setLevel(-1);
		add(rootnode);
		rootnode.setDir(dir);//dir+"/@pipeline_"+name);
	}	
	
	public int getMaxDepth()
	{
		return super.getMaxDepth()-1;
	}

	public PipelineNode getRootNode()
	{
		return getNodesAtDepth(-1).get(0);
	}
	
	public List<PipelineNode> getNodesAtDepth(int level)
	{
		List<PipelineNode> list=Lists.newArrayList();
		getNodesAtDepth(level, list);
		return list;
	}
	
	public PipelineTree createTree()
	{
		PipelineTree tree=new PipelineTree(this);
		return tree;
	}
	
	public String getSnakefile()
	{
		return dir+"/"+name+".smk";
	}
	
	@Override
	public String toString()
	{
		StringBuilder buffer=new StringBuilder();
		toStringChildren(buffer);
		return buffer.toString();
	}
}

//public List<Task> getTasks()
//{
//	List<Task> tasks=Lists.newArrayList();
//	tasks.add(getRootNode().getTask());
//	for (int depth=0; depth<getMaxDepth(); depth++)
//	{
//		for (PipelineNode node : getNodesAtDepth(depth))
//		{
//			tasks.add(node.getTask());
//		}
//	}
//	return tasks; 
//}
//public void createTasks()
//{
//	initTasksChildren();
//}


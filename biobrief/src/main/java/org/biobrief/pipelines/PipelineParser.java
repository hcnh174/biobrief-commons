package org.biobrief.pipelines;

import java.util.List;
import java.util.Map;

import org.biobrief.pipelines.PipelineConstants.TaskType;
//import org.biobrief.batchjobs.BatchJobConstants.BatchJobType;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PipelineParser
{
	public static final String DELIMITER="=";
	public static final String COMMENT="#";
	
	public static Pipeline parseFile(String filename, String dir, String winDir)
	{
		FileHelper.checkExists(filename);
		FileHelper.checkExists(winDir);
		
		String name=FileHelper.stripExtension(FileHelper.stripPath(filename));
		
		Pipeline pipeline=new Pipeline(name, dir, winDir);
		PipelineNode rootnode=pipeline.getRootNode();
		
		List<String> lines=FileHelper.readLines(filename);
		List<PipelineNode> nodes=Lists.newArrayList();
		int num=1;
		for (String line : clean(lines))
		{
			PipelineNode node=parseLine(pipeline, line);
			node.num=num++;
			nodes.add(node);
		}
		
		int lastlevel=-1;
		PipelineNode lastnode=rootnode;
		for (PipelineNode node : nodes)
		{
			System.out.println("checking node: "+node.num+" command="+node.type+" level="+node.level);
			int diff=node.getLevel()-lastlevel;
			if (node.getLevel()==0)
				rootnode.add(node);//pipeline.add(node);
			else if (diff==0) // same level as previous node - add to previous node's parent
				lastnode.getParent().add(node);
			else if (diff==1) // child of previous node - add
				lastnode.add(node);
			else if (diff<0) // find ancestor
			{
				PipelineNode parent=lastnode;
				for (int i=0; i<=Math.abs(diff); i++)
				{
					parent=parent.getParent();
					if (parent==null)
						throw new CException("cannot find parent with diff="+diff);
				}
				parent.add(node);
			}
			else throw new CException("no handler for diff: "+diff);
			lastnode=node;
			lastlevel=node.getLevel();
		}
		//pipeline.createTasks();
		return pipeline;
	}
	
	private static List<String> clean(List<String> lines)
	{
		List<String> list=Lists.newArrayList();
		for (String line : lines)
		{
			line=stripComments(line);
			if (StringHelper.hasContent(line))
				list.add(line);
		}
		return list;
	}
	
	private static String stripComments(String line)
	{
		int index=line.indexOf(COMMENT);
		if (index==-1)
			return line;
		return StringHelper.trimTrailing(line.substring(0, index));
	}
	
	private static PipelineNode parseLine(Pipeline pipeline, String line)
	{
		List<String> parts=StringHelper.split(line.trim(), " ");
		PipelineNode node=new PipelineNode();
		node.setLevel(countTabs(line));
		node.setType(TaskType.get(parts.get(0)));
		node.setParams(parseParams(parts));
		return node;
	}
	
	private static Map<String, String> parseParams(List<String> parts)
	{
		Map<String, String> params=Maps.newLinkedHashMap();
		if (parts.size()==1)
			return params;
		for (int index=1; index<parts.size(); index++)
		{
			String part=parts.get(index);
			int i=part.indexOf(DELIMITER);
			if (i==-1)
				throw new CException("cannot find delimiter ("+DELIMITER+") in parameter: "+part);
			String name=part.substring(0, i);
			String value=part.substring(i+1);
			params.put(name, value);
		}
		return params;
	}
	
	private static int countTabs(String line)
	{
		for (int index=0; index<line.length(); index++)
		{
			if (!line.substring(index, index+1).equals(StringHelper.TAB))
				return index;
		}
		throw new CException("cannot determine tab count in line: ["+line+"]");
	}
}


//public static Pipeline parseString(String parentId, String str)
//{
//	return parse(parentId, StringHelper.splitLines(str));
//}

// run pipe 5e888fd87442ac2d518c34de "kraken | krakenfilter | minimap ref=KR819180primer | clairvoyante"
//public static Pipeline parseSteps(String parentId, String str)
//{
//	List<String> lines=Lists.newArrayList();
//	int depth=0;
//	for (String command : StringHelper.split(str, "|", true))
//	{
//		String indent=StringHelper.repeatString(StringHelper.TAB, depth++);
//		lines.add(indent+command);
//	}
//	String pipeline=StringHelper.join(lines, "\n");
//	System.out.println("pipeline="+pipeline);
//	return parseString(parentId, pipeline);
//}
package org.biobrief.pipelines;

import org.biobrief.pipelines.entities.Task;
import org.biobrief.util.StringHelper;

public class ShellScriptPipelineBuilder extends AbstractPipelineBuilder
{
	public static String build(Pipeline pipeline)
	{
		PipelineNode root=pipeline.getRootNode();
		System.out.println("root node="+StringHelper.toString(root));
		
		StringBuilder buffer=new StringBuilder();
		createCommands(root, null, buffer);
		return buffer.toString();
	}
	
	private static void createCommands(PipelineNode node, Task parent, StringBuilder buffer)
	{
		for (PipelineNode child : node.getChildren())
		{
			Task task=createCommand(child, parent, buffer);
			createCommands(child, task, buffer);
		}
	}
	
	private static Task createCommand(PipelineNode node, Task parent, StringBuilder buffer)
	{
		Task task=node.createTask();
		String name=node.getFullName();
		buffer.append("# "+name+"\n");
		String command=task.getCommand(parent).format();
		buffer.append("declare -a samples=("+StringHelper.join(task.getParams().getSamples(), " ")+")\n"); 
		buffer.append("for sample in \"${samples[@]}\"; do\n");
		buffer.append("  "+command+"\n");
		buffer.append("done\n");
		buffer.append("\n");
		return task;
	}
	
}

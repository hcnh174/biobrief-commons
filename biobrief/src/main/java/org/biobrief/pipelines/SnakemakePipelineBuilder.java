package org.biobrief.pipelines;

import java.util.List;

import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.CommandTemplate.Parameter;
import org.biobrief.pipelines.entities.Task;
import org.biobrief.pipelines.util.SnakemakeHelper;
import org.biobrief.util.Constants.IO;
import org.biobrief.util.StringHelper;

//cd /mnt/work/hbv-nanopore/@pipeline_test-hbv-nanopore
//snakemake --snakefile test-hbv-nanopore.smk -r --dry-run
//snakemake --snakefile test-hbv-nanopore.smk -r --cores 8
//snakemake --snakefile test-hbv-nanopore.smk --cluster sbatch -j 32
public class SnakemakePipelineBuilder extends AbstractPipelineBuilder
{
	public static SnakemakeHelper.Script build(Pipeline pipeline)
	{
		PipelineNode root=pipeline.getRootNode();
		System.out.println("root node="+StringHelper.toString(root));
		
		SnakemakeHelper.Script script=new SnakemakeHelper.Script(root.getDir());
		script.getWildcardConstraints().add("sample", "[a-zA-Z0-9]+");
		Task task=root.createTask();
		createRules(root, task, script);
		return script;
	}
	
	private static void createRules(PipelineNode node, Task parent, SnakemakeHelper.Script script)
	{
		for (PipelineNode child : node.getChildren())
		{
			Task task=createRule(child, parent, script);
			createRules(child, task, script);
		}
	}
	
	private static Task createRule(PipelineNode node, Task parent, SnakemakeHelper.Script script)
	{
		Task task=node.createTask();
		String name=node.getFullName();
		String dir=task.getParams().getDir();
		List<String> samples=task.getParams().getSamples();
		
		SnakemakeHelper.Rule allrule=script.addRule(name+"_all");
		String input="expand(\""+dir+"/{sample}/.done\", sample=["+StringHelper.join(StringHelper.singleQuote(samples))+"])";
		allrule.getInput().add(input, false);
		allrule.getOutput().add(dir+"/.done");
		allrule.getShell().add("touch {output}");
		
		SnakemakeHelper.Rule rule=script.addRule(name);
		Command command=task.getCommand(parent);
				
		for (Parameter parameter : command.getTemplate().getParameters(IO.input))
		{
			if (parameter.getValue().contains("${sample}"))
				rule.getInput().add(parameter.getName(), fixVars(parameter.getValue()));
		}
		for (Parameter parameter : command.getTemplate().getParameters(IO.output))
		{
			rule.getOutput().add(parameter.getName(), fixVars(parameter.getValue()));
		}
		
		//rule.getOutput().add("startfile", dir+"/{sample}/.started");
		rule.getOutput().add("endfile", dir+"/{sample}/.done");
		
		rule.setLog(dir+"/{sample}/.log");
		
		//rule.getShell().add("touch {output.startfile}");
		rule.getShell().add(formatCommand(command));
		rule.getShell().add("touch {output.endfile}");

		return task;
	}
	
	// converts from bash format (${sample} to snakemake format ({sample})
	private static String fixVars(String value)
	{
		return StringHelper.replace(value, "${", "{");
	}
	
	// replaces variable names ($fastqfile) with input or output symbols ({input.fastqfile})
	private static String formatCommand(Command command)
	{
		String template=command.getTemplate().getTemplate();
		for (Parameter parameter : command.getTemplate().getParameters(IO.input))
		{
			String name=parameter.getName();
			if (parameter.getValue().contains("${sample}"))
				template=StringHelper.replace(template, "$"+name, "{input."+name+"}");
			else template=StringHelper.replace(template, "$"+name, parameter.getValue());
		}
		for (Parameter parameter : command.getTemplate().getParameters(IO.output))
		{
			String name=parameter.getName();
			template=StringHelper.replace(template, "$"+name, "{output."+name+"}");
		}
		return template;
	}	
}

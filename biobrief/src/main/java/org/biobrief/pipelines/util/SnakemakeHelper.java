package org.biobrief.pipelines.util;

import java.util.List;

import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class SnakemakeHelper
{
	//time snakemake --snakefile snakefile -j 999 --cluster-config config/cluster-config.json \
	// --cluster "sbatch --nodes={cluster.nodes} --ntasks={cluster.ntasks} --error={cluster.error} --output={cluster.output} --job-name={cluster.name}" -n

	private static final String TAB="    ";
	private static final String NEWLINE="\n";
	private static final String TRIPLE_QUOTES="\"\"\"";
	
	public static String createCommand(String snakefile, boolean dryrun)
	{
		String dir=FileHelper.getDirFromFilename(snakefile);
		return "run_snakemake.sh --snakefile "+snakefile;
	}
	
	////////////////////////////////////////////
	
	private static String tab(int indent)
	{
		return StringHelper.repeatString(TAB, indent);
	}
	
	@Data @EqualsAndHashCode
	public static class Script
	{
		protected String dir;
		protected Values wildcardConstraints=new Values("wildcard_constraints", 0);
		protected List<Rule> rules=Lists.newArrayList();
		
		public Script(String dir)
		{
			this.dir=dir;
		}
		
		public Rule addRule(String name)
		{
			Rule rule=new Rule(name);
			rules.add(rule);
			return rule;
		}
		
		public Rule addRule(String name, Integer threads)
		{
			Rule rule=new Rule(name, threads);
			rules.add(rule);
			return rule;
		}
		
		public String format()
		{			
			StringBuilder buffer=new StringBuilder();
			buffer.append(wildcardConstraints.format()).append(NEWLINE);
			//buffer.append("wildcard_constraints:"+NEWLINE); // hack!
			//buffer.append(TAB+"sample=\"[a-zA-Z0-9]+\""+NEWLINE+NEWLINE);
			buffer.append(createAllRule().format());
			for (Rule rule : rules)
			{
				buffer.append(rule.format());
			}
			return buffer.toString();
		}
		
		private Rule createAllRule()
		{
			Rule allrule=new Rule("all");
			for (Rule rule : rules)
			{
				if (!rule.getName().endsWith("_all"))
					continue;
				String filename=rule.getOutput().getValues().get(0).getValue();
				allrule.getInput().add(filename);
			}
			allrule.getOutput().add(dir+"/.done");
			allrule.getShell().add("touch {output}");
			return allrule;
		}
	}
	
	@Data @EqualsAndHashCode
	public static class Rule
	{
		protected String name;
		protected Values input=new Values("input", 1);
		protected Values output=new Values("output", 1);
		protected Values params=new Values("params", 1);
		protected Values log=new Values("log", 1);
		protected Integer threads;
		protected Shell shell=new Shell();
		
		public Rule(String name)
		{
			this.name=name;
		}
		
		public Rule(String name, Integer threads)
		{
			this(name);
			this.threads=threads;
		}
		
		public void setLog(String filename)
		{
			this.log.add(filename);
		}
		
		public String format()
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("rule "+name+":"+NEWLINE);
			buffer.append(input.format());
			buffer.append(output.format());
			buffer.append(params.format());
			buffer.append(log.format());
			if (threads!=null)
				buffer.append(TAB+"threads: "+threads+NEWLINE);
			buffer.append(shell.format());
			buffer.append(NEWLINE);
			return buffer.toString();
		}
	}
	
	@Data @EqualsAndHashCode
	public static class Values
	{
		protected String name;
		protected int indent;
		protected String delimiter=","+NEWLINE;
		
		protected List<Value> values=Lists.newArrayList();
		
		public Values(String name, int indent)
		{
			this.name=name;
			this.indent=indent;
		}
		
		public Values(String name, int indent, String delimiter)
		{
			this(name, indent);
			this.delimiter=delimiter;
		}
		
		public void add(String value)
		{
			this.values.add(new Value(value));
		}
		
		public void add(String value, boolean quote)
		{
			this.values.add(new Value(value, quote));
		}
		
		public void add(String name, String value)
		{
			this.values.add(new NamedValue(name, value));
		}
		
		public void add(String name, String value, boolean quote)
		{
			this.values.add(new NamedValue(name, value, quote));
		}
		
		public String format()
		{
			if (values.isEmpty())
				return "";
			List<String> buffer=Lists.newArrayList();
			for (Value value : values)
			{
				buffer.add(tab(indent+1)+value.format());
			}
			return tab(indent)+name+":"+NEWLINE+StringHelper.join(buffer, delimiter)+NEWLINE;
		}		
	}
	
	@Data @EqualsAndHashCode
	public static class Value
	{
		protected String value;
		protected boolean quote;
		
		public Value(String value, boolean quote)
		{
			this.value=value;
			this.quote=quote;
		}
		
		public Value(String value)
		{
			this(value, true);
		}
		
		public String format()
		{
			return formatValue(value);//tab(indent)+
		}
		
		protected String formatValue(String value)
		{
			if (quote)
				return StringHelper.doubleQuote(value);
			else return value;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class NamedValue extends Value
	{
		protected String name;
		
		public NamedValue(String name, String value)
		{
			super(value);
			this.name=name;
		}
		
		public NamedValue(String name, String value, boolean quote)
		{
			super(value, quote);
			this.name=name;
		}
		
		@Override
		public String format()
		{
			return name+"="+formatValue(value);//tab(indent)+
		}
	}
	
	@Data @EqualsAndHashCode
	public static class Shell
	{
		protected List<String> commands=Lists.newArrayList();
		
		public void add(String command)
		{
			this.commands.add(command);
		}
		
		public String format()
		{
			if (commands.isEmpty())
				return "";
			if (commands.size()==1)
				return tab(1)+"shell:"+NEWLINE+tab(2)+StringHelper.doubleQuote(commands.get(0))+NEWLINE;
			List<String> buffer=Lists.newArrayList();
			buffer.add(tab(2)+TRIPLE_QUOTES);
			for (String command : commands)
			{
				buffer.add(tab(2)+command);
			}
			buffer.add(tab(2)+TRIPLE_QUOTES);
			
			return tab(1)+"shell:"+NEWLINE+StringHelper.join(buffer, NEWLINE)+NEWLINE;
		}		
	}
}

/*
#configfile: "config.yaml"

SAMPLES = ["sample1", "sample2", "sample3"]

#fq1=expand("/mnt/work/snakemake/fastq/{sample}_R1.fq.gz", sample=SAMPLES),
#fq2=expand("/mnt/work/snakemake/fastq/{sample}_R2.fq.gz", sample=SAMPLES)

rule all:
    input:
        expand("/mnt/work/snakemake/bwa/{sample}.bam", sample=SAMPLES)

rule bwa_map:
    input:
        ref="/mnt/work/snakemake/ref/hsv2.fasta",
        fastqfile1="/mnt/work/snakemake/fastq/{sample}_R1.fq.gz",
        fastqfile2="/mnt/work/snakemake/fastq/{sample}_R2.fq.gz"
    output:
        "/mnt/work/snakemake/bwa/{sample}.bam"
    params:
        rg=r"@RG\tID:{sample}\tSM:{sample}"
    log:
    	"/mnt/work/snakemake/bwa/{sample}.log"
    threads: 8
    shell:
        "($BWA_HOME/bwa mem -R '{params.rg}' -t {threads} {input.ref} {input.fastqfile1} {input.fastqfile2} | "
        "samtools view -Sb - > {output}) 2> {log}"

*/


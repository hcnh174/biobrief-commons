package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.MarsMethod;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.MarsCommand;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class MarsTask extends AbstractFastqAnalyzerTask
{	
	protected Params params;
	
	public MarsTask(){}

	public MarsTask(Params params)
	{
		super(TaskType.MARS, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqAnalyzerTaskParams
	{			
		protected String ref;
		protected MarsMethod method;
		
		@Override
		public Command getCommand(Task parent)
		{
			MarsCommand command=new MarsCommand();
			command.fastqfile(new FastqFile(parentDir+"/${sample}/polished.fastq"));
			command.reffile(new FastaFile(ref));
			command.outfile(new FastaFile(dir+"/${sample}/${sample}.mars.fasta"));
			command.method(method);
			return command;
		}
	}
}

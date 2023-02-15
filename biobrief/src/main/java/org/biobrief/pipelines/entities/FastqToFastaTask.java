package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FastqToFastaCommand;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class FastqToFastaTask extends AbstractFastqTask
{
	protected Params params;
	
	public FastqToFastaTask(){}

	public FastqToFastaTask(Params params)
	{
		super(TaskType.FASTQ_TO_FASTA, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			FastqToFastaCommand command=new FastqToFastaCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(new FastaFile(dir+"/${sample}/${sample}.fasta"));
			return command;
		}
	}
}

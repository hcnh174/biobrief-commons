package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanopolishCommand;
import org.biobrief.pipelines.commands.Output.Dir;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanopolishTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public NanopolishTask(){}

	public NanopolishTask(Params params)
	{
		super(TaskType.NANOPOLISH, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		private String fast5dir;
		
		@Override
		public Command getCommand(Task parent)
		{
			NanopolishCommand command=new NanopolishCommand();
			command.fast5dir(new Dir(fast5dir));
			command.fastqfile(getFastqFile(parent));
			//command.fastqfile(new FastqFile(parentDir+"/${sample}/${sample}.fastq"));
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			command.consensusfile(new FastaFile(dir+"/${sample}/${sample}.fasta"));
			return command;
		}	
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.CanuCommand;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class CanuTask extends AbstractDenovoTask
{
	protected Params params;
		
	public CanuTask(){}

	public CanuTask(Params params)
	{
		super(TaskType.CANU, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractDenovoTaskParams
	{	
		protected String genomeSize;
		
		@Override
		public Command getCommand(Task parent)
		{
			CanuCommand command=new CanuCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFile());
			command.genomeSize(genomeSize);
			return command;
		}
		
		@JsonIgnore
		protected FastaFile getOutFile()
		{
			return new FastaFile(dir+"/${sample}/${sample}.contigs.fasta");
		}
	}
}

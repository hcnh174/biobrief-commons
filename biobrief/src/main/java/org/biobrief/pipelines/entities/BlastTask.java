package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.BlastCommand;
import org.biobrief.pipelines.commands.Command;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class BlastTask extends AbstractFastaTask
{
	protected Params params;
	
	public BlastTask(){}

	public BlastTask(Params params)
	{
		super(TaskType.BLAST, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastaTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			BlastCommand command=new BlastCommand();
			//command.fastafile(new FastaFile(fastafile));
			return command;
		}
	}
}

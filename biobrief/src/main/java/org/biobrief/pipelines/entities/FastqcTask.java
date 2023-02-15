package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FastqcCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="batchjobs") @Data @EqualsAndHashCode(callSuper=true)
public class FastqcTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public FastqcTask(){}

	public FastqcTask(Params params)
	{
		super(TaskType.FASTQC, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{		
		@Override
		public Command getCommand(Task parent)
		{
			FastqcCommand command=new FastqcCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			return command;
		}
	}
}

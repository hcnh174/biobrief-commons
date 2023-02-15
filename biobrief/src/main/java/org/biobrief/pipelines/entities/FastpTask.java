package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FastpCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="batchjobs") @Data @EqualsAndHashCode(callSuper=true)
public class FastpTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public FastpTask(){}

	public FastpTask(Params params)
	{
		super(TaskType.FASTP, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{		
		@Override
		public Command getCommand(Task parent)
		{
			FastpCommand command=new FastpCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.outfile1(getOutFastqFile1());
			command.outfile2(getOutFastqFile2());
			return command;
		}
	}
}

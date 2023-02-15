package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.LastFilterCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class LastFilterTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public LastFilterTask(){}

	public LastFilterTask(Params params)
	{
		super(TaskType.LAST_FILTER, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			LastFilterCommand command=new LastFilterCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFastqFile());
			return command;
		}
	}
}

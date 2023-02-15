package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.PorechopCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class PorechopTask extends AbstractFastqFilterTask
{
	protected Params params;

	public PorechopTask(){}

	public PorechopTask(Params params)
	{
		super(TaskType.PORECHOP, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			PorechopCommand command=new PorechopCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFastqFile());
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FiltLongCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class FiltLongTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public FiltLongTask(){}

	public FiltLongTask(Params params)
	{
		super(TaskType.FILT_LONG, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected Integer minlength=1000;
		
		@Override
		public Command getCommand(Task parent)
		{
			FiltLongCommand command=new FiltLongCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFastqFile());
			command.minlength(minlength);
			return command;
		}
	}
}

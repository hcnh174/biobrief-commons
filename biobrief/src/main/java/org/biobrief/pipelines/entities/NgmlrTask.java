package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NgmlrCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NgmlrTask extends AbstractMappingTask
{	
	protected Params params;
	
	public NgmlrTask(){}

	public NgmlrTask(Params params)
	{
		super(TaskType.NGMLR, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			NgmlrCommand command=new NgmlrCommand();
			command.fastqfile(getInFastqFile());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

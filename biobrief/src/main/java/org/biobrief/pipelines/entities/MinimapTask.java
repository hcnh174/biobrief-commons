package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.MinimapCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class MinimapTask extends AbstractMappingTask
{	
	protected Params params;
	
	public MinimapTask(){}

	public MinimapTask(Params params)
	{
		super(TaskType.MINIMAP, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			MinimapCommand command=new MinimapCommand();
			command.fastqfile(getInFastqFile());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

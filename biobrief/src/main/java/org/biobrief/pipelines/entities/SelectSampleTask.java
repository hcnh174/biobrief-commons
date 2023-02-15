package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.LinkFastqFileCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SelectSampleTask extends AbstractFastqFilterTask
{	
	protected Params params;
	
	public SelectSampleTask(){}

	public SelectSampleTask(Params params)
	{
		super(TaskType.SELECT_SAMPLE, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			LinkFastqFileCommand command=new LinkFastqFileCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFastqFile());
			return command;
		}
	}
}

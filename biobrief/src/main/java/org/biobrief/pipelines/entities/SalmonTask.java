package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.SalmonCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SalmonTask extends AbstractMappingTask
{
	protected Params params;
	
	public SalmonTask(){}

	public SalmonTask(Params params)
	{
		super(TaskType.SALMON, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{		
		@Override
		public Command getCommand(Task parent)
		{
			SalmonCommand command=new SalmonCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.reffile(getRefFile());
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.SubreadCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SubreadTask extends AbstractMappingTask
{
	protected Params params;
	
	public SubreadTask(){}

	public SubreadTask(Params params)
	{
		super(TaskType.SUBREAD, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			SubreadCommand command=new SubreadCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.BwaCommand;
import org.biobrief.pipelines.commands.Command;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class BwaTask extends AbstractMappingTask
{
	protected Params params;
	
	public BwaTask(){}

	public BwaTask(Params params)
	{
		super(TaskType.BWA, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			BwaCommand command=new BwaCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

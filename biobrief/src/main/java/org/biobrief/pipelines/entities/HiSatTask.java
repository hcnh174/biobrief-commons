package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.HiSatCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class HiSatTask extends AbstractMappingTask
{
	protected Params params;
	
	public HiSatTask(){}

	public HiSatTask(Params params)
	{
		super(TaskType.HISAT, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			HiSatCommand command=new HiSatCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

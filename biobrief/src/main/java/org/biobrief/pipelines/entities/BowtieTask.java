package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.BowtieCommand;
import org.biobrief.pipelines.commands.Command;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class BowtieTask extends AbstractMappingTask
{
	protected Params params;
	
	public BowtieTask(){}

	public BowtieTask(Params params)
	{
		super(TaskType.BOWTIE, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractMappingTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			BowtieCommand command=new BowtieCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.reffile(getRefFile());
			command.outfile(getBamFile());
			return command;
		}
	}
}

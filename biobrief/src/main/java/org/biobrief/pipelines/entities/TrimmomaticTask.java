package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.PipelineConstants.TrimmomaticAdapters;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.TrimmomaticCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class TrimmomaticTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public TrimmomaticTask(){}

	public TrimmomaticTask(Params params)
	{
		super(TaskType.TRIMMOMATIC, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected TrimmomaticAdapters adapters;
		
		@Override
		public Command getCommand(Task parent)
		{
			TrimmomaticCommand command=new TrimmomaticCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.outfile1(getOutFastqFile1());
			command.outfile2(getOutFastqFile2());
			command.adapters(adapters);
			return command;
		}
	}
}

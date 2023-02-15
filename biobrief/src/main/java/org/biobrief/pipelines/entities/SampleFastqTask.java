package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.SampleFastqCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SampleFastqTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public SampleFastqTask(){}

	public SampleFastqTask(Params params)
	{
		super(TaskType.SAMPLE_FASTQ, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected Integer num;
		
		@Override
		public Command getCommand(Task parent)
		{
			SampleFastqCommand command=new SampleFastqCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(getOutFastqFile());
			command.num(num);
			return command;
		}
	}
}

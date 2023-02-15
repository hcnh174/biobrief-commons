package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.SamplePairedFastqCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SamplePairedFastqTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public SamplePairedFastqTask(){}

	public SamplePairedFastqTask(Params params)
	{
		super(TaskType.SAMPLE_PAIRED_FASTQ, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected Integer num;
		
		@Override
		public Command getCommand(Task parent)
		{
			SamplePairedFastqCommand command=new SamplePairedFastqCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.outfile1(getOutFastqFile1());
			command.outfile2(getOutFastqFile2());
			command.num(num);
			return command;
		}
	}
}

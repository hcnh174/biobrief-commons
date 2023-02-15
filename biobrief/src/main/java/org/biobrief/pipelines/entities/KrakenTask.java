package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.KrakenCommand;
import org.biobrief.pipelines.commands.KrakenPairedCommand;
import org.biobrief.pipelines.commands.Output.TsvFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class KrakenTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public KrakenTask(){}

	public KrakenTask(Params params)
	{
		super(TaskType.KRAKEN, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			if (fastqMode.isPaired())
			{
				KrakenPairedCommand command=new KrakenPairedCommand();
				command.fastqfile1(getInFastqFile1());//new FastqFile(parentDir+"/${sample}/${sample}_R1.fastq"));
				command.fastqfile2(getInFastqFile2());//new FastqFile(parentDir+"/${sample}/${sample}_R2.fastq"));
				command.krakenfile(getKrakenFile());
				command.outfile1(getOutFastqFile1());
				command.outfile2(getOutFastqFile2());
				return command;
			}
			else
			{
				KrakenCommand command=new KrakenCommand();
				command.fastqfile(getInFastqFile());//new FastqFile(parentDir+"/${sample}/${sample}.fastq"));
				command.krakenfile(getKrakenFile());
				command.outfile(getOutFastqFile());
				return command;
			}
		}
		
		private TsvFile getKrakenFile()
		{
			return new TsvFile(dir+"/${sample}/kraken.txt");
		}
	}
}

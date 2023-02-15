package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.KrakenFilterCommand;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.pipelines.commands.Output.TsvFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class KrakenFilterTask extends AbstractFastqFilterTask
{
	protected Params params;
	
	public KrakenFilterTask(){}

	public KrakenFilterTask(Params params)
	{
		super(TaskType.KRAKEN_FILTER, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected Integer taxid=PipelineConstants.TAXID_HBV;//10407;
		
		@Override
		public Command getCommand(Task parent)
		{
			KrakenFilterCommand command=new KrakenFilterCommand();
			command.fastqfile(new FastqFile(parentDir+"/${sample}/${sample}.fastq"));
			command.krakenfile(new TsvFile(parentDir+"/${sample}/kraken.txt"));
			command.outfile(new FastqFile(dir+"/${sample}/${sample}.fastq"));
			command.taxid(taxid);
			return command;
		}
	}
}

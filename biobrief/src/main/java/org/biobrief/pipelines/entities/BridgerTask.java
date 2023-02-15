package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.BridgerCommand;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class BridgerTask extends AbstractDenovoTask
{
	protected Params params;
	
	public BridgerTask(){}

	public BridgerTask(Params params)
	{
		super(TaskType.BRIDGER, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractDenovoTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			BridgerCommand command=new BridgerCommand();
			command.fastqfile1(getInFastqFile1());
			command.fastqfile2(getInFastqFile2());
			command.outfile(new FastaFile(dir+"/${sample}/${sample}.fasta"));
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.ConsensusCommand;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class ConsensusTask extends AbstractBamTask
{
	protected Params params;
	
	public ConsensusTask(){}

	public ConsensusTask(Params params)
	{
		super(TaskType.CONSENSUS, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractBamTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			ConsensusCommand command=new ConsensusCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.consensusfile(new FastaFile(dir+"/${sample}/${sample}.fasta"));
			return command;
		}
	}
}

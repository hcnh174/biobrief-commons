package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.LastMode;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.LastCommand;
import org.biobrief.pipelines.commands.Output.BlastTabFile;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class LastTask extends AbstractFastqFilterTask
{	
	protected Params params;
	
	public LastTask(){}

	public LastTask(Params params)
	{
		super(TaskType.LAST, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected String ref;
		protected LastMode mode=LastMode.DEFAULT;
		
		@Override
		public Command getCommand(Task parent)
		{
			LastCommand command=new LastCommand();
			command.fastqfile(getInFastqFile());
			command.reffile(new FastaFile(ref));
			command.outfile(new BlastTabFile("/${sample}/last.txt"));
			command.mode(mode);
			return command;
		}
	}
}

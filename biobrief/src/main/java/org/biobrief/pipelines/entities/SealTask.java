package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.TextFile;
import org.biobrief.pipelines.commands.SealCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SealTask extends AbstractFastqTask
{
	protected Params params;
	
	public SealTask(){}

	public SealTask(Params params)
	{
		super(TaskType.SEAL, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqTaskParams
	{
		protected String reffile1;
		protected String reffile2;
		
		@Override
		public Command getCommand(Task parent)
		{
			SealCommand command=new SealCommand();
			command.fastqfile(getInFastqFile());
			command.reffile1(new FastaFile(reffile1));
			command.reffile2(new FastaFile(reffile2));
			command.statsfile(new TextFile(dir+"/${sample}/sealstats.txt"));
			return command;
		}
	}
}

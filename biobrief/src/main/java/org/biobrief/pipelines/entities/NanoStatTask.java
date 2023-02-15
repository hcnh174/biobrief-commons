package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoStatCommand;
import org.biobrief.pipelines.commands.Output.TextFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoStatTask extends AbstractFastqAnalyzerTask
{	
	protected Params params;
	
	public NanoStatTask(){}

	public NanoStatTask(Params params)
	{
		super(TaskType.NANO_STAT, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqAnalyzerTaskParams
	{
		protected String suffix;
		
		@Override
		public Command getCommand(Task parent)
		{
			NanoStatCommand command=new NanoStatCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(new TextFile(dir+"/${sample}/nanostat.txt"));
			return command;
		}
	}
}

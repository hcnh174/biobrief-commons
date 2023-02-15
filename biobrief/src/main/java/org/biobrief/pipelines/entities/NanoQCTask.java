package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoQCCommand;
import org.biobrief.pipelines.commands.Output.HtmlFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoQCTask extends AbstractFastqAnalyzerTask
{	
	protected Params params;
	
	public NanoQCTask(){}

	public NanoQCTask(Params params)
	{
		super(TaskType.NANO_Q_C, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqAnalyzerTaskParams
	{
		protected String suffix;
		
		@Override
		public Command getCommand(Task parent)
		{
			NanoQCCommand command=new NanoQCCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(new HtmlFile(dir+"/${sample}/nanoQC.html"));
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoPlotCommand;
import org.biobrief.pipelines.commands.Output.TextFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoPlotTask extends AbstractFastqAnalyzerTask
{	
	protected Params params;
	
	public NanoPlotTask(){}

	public NanoPlotTask(Params params)
	{
		super(TaskType.NANO_PLOT, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractFastqAnalyzerTaskParams
	{
		protected String suffix;
		
		@Override
		public Command getCommand(Task parent)
		{
			NanoPlotCommand command=new NanoPlotCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(new TextFile(dir+"/${sample}/NanoStats.txt"));
			return command;
		}
	}
}

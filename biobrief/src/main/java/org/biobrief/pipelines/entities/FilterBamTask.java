package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FilterBamCommand;
import org.biobrief.pipelines.commands.Output.BamFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class FilterBamTask extends AbstractBamTask
{
	protected Params params;
	
	public FilterBamTask(){}

	public FilterBamTask(Params params)
	{
		super(TaskType.FILTER_BAM, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractBamTaskParams
	{
		protected String flags="-F 4";
		protected Integer quality=10;

		@Override
		public Command getCommand(Task parent)
		{
			FilterBamCommand command=new FilterBamCommand();
			command.bamfile(getBamFile());
			command.outfile(new BamFile(dir+"/${sample}/reads.bam"));
			command.flags(flags);
			command.quality(quality);
			return command;
		}
	}
}

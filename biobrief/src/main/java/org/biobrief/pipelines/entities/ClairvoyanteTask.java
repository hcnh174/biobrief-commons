package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.ClairvoyanteCommand;
import org.biobrief.pipelines.commands.Command;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class ClairvoyanteTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public ClairvoyanteTask(){}

	public ClairvoyanteTask(Params params)
	{
		super(TaskType.CLAIRVOYANTE, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		protected Float threshold=0.25f;
		protected Integer minCoverage=4;
		
		@Override
		public Command getCommand(Task parent)
		{
			ClairvoyanteCommand command=new ClairvoyanteCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			command.threshold(threshold);
			command.minCoverage(minCoverage);
			return command;
		}
	}
}

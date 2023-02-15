package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.ClairModel;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.ClairCommand;
import org.biobrief.pipelines.commands.Command;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class ClairTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public ClairTask(){}

	public ClairTask(Params params)
	{
		super(TaskType.CLAIR, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		protected ClairModel model=ClairModel.ont;
		protected Float threshold=0.2f;

		@Override
		public Command getCommand(Task parent)
		{
			ClairCommand command=new ClairCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			command.model(model);
			command.threshold(threshold);
			return command;
		}
	}
}

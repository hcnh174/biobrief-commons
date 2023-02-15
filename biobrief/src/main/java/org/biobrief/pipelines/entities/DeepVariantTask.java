package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.DeepVariantCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class DeepVariantTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public DeepVariantTask(){}

	public DeepVariantTask(Params params)
	{
		super(TaskType.DEEP_VARIANT, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			DeepVariantCommand command=new DeepVariantCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoVarCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoVarTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public NanoVarTask(){}

	public NanoVarTask(Params params)
	{
		super(TaskType.NANO_VAR, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			NanoVarCommand command=new NanoVarCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			return command;
		}
	}
}

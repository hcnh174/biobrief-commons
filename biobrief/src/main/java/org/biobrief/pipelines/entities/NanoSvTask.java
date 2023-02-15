package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoSvCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoSvTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public NanoSvTask(){}

	public NanoSvTask(Params params)
	{
		super(TaskType.NANO_SV, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			NanoSvCommand command=new NanoSvCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			return command;
		}
	}
}

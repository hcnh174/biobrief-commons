package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.LongshotCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class LongshotTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public LongshotTask(){}

	public LongshotTask(Params params)
	{
		super(TaskType.LONGSHOT, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			LongshotCommand command=new LongshotCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			return command;
		}
	}
}

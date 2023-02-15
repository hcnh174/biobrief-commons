package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FilterVcfCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class VcfFilterTask extends AbstractVcfFilterTask
{
	protected Params params;
	
	public VcfFilterTask(){}

	public VcfFilterTask(Params params)
	{
		super(TaskType.VCF_FILTER, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractVcfFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			FilterVcfCommand command=new FilterVcfCommand();
			command.vcffile(getInVcfFile());
			command.outfile(getOutVcfFile());
			return command;
		}
	}
}

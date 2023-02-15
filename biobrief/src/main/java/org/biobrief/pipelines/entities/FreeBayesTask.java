package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.FreeBayesCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class FreeBayesTask extends AbstractVariantCallerTask
{
	protected Params params;
		
	public FreeBayesTask(){}

	public FreeBayesTask(Params params)
	{
		super(TaskType.FREE_BAYES, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractVariantCallerTaskParams
	{
		protected Integer ploidy=2;
		
		@Override
		public Command getCommand(Task parent)
		{
			FreeBayesCommand command=new FreeBayesCommand();
			command.bamfile(getBamFile());
			command.reffile(getRefFile(parent));
			command.vcffile(getVcfFile());
			command.ploidy(ploidy);
			return command;
		}
	}
}

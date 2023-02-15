package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.VcfFile;
import org.biobrief.pipelines.commands.PickyCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class PickyTask extends AbstractFastqFilterTask
{	
	protected Params params;
	
	public PickyTask(){}

	public PickyTask(Params params)
	{
		super(TaskType.PICKY, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected String ref;
		
		@Override
		public Command getCommand(Task parent)
		{
			PickyCommand command=new PickyCommand();
			command.fastqfile(getInFastqFile());
			command.reffile(new FastaFile(ref));
			command.vcffile(new VcfFile(dir+"/${sample}/${sample}.vcf"));
			return command;
		}
	}
}

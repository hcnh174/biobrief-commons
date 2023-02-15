package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.NanoFiltCommand;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class NanoFiltTask extends AbstractFastqFilterTask
{
	protected Params params;

	public NanoFiltTask(){}

	public NanoFiltTask(Params params)
	{
		super(TaskType.NANO_FILT, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected Integer quality=0;
		protected Integer minlength=0;
		protected Integer maxlength=Integer.MAX_VALUE;
		protected Integer headcrop=0;
		protected Integer tailcrop=0;
		
		@Override
		public Command getCommand(Task parent)
		{
			NanoFiltCommand command=new NanoFiltCommand();
			command.fastqfile(getInFastqFile());
			command.outfile(new FastqFile(dir+"/${sample}/${sample}.fastq"));
			command.quality(quality);
			command.minlength(minlength);
			command.maxlength(maxlength);
			command.headcrop(headcrop);
			command.tailcrop(tailcrop);
			return command;
		}
	}
}

package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.LinkFastqFileCommand;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class SelectFastqTask extends AbstractFastqFilterTask
{	
	protected Params params;
	
	public SelectFastqTask(){}

	public SelectFastqTask(Params params)
	{
		super(TaskType.SELECT_FASTQ, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		protected String fastqfile;
		
		@Override
		public Command getCommand(Task parent)
		{
			LinkFastqFileCommand command=new LinkFastqFileCommand();
			command.fastqfile(new FastqFile(fastqfile));
			command.outfile(getOutFastqFile());
			return command;
		}
	}
}

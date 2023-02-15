package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.util.PipelineUtil.UsesRef;
import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.pipelines.commands.Output.FastaFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractBamTask extends Task
{
	public AbstractBamTask(){}

	public AbstractBamTask(TaskType type, AbstractBamTaskParams params)
	{
		super(type, params);
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractBamTaskParams extends AbstractTaskParams
	{
		@JsonIgnore
		protected BamFile getBamFile()
		{
			return new BamFile(parentDir+"/${sample}/reads.bam");
		}

		@JsonIgnore
		protected FastaFile getRefFile(Task parent)
		{
			return new FastaFile(((UsesRef)parent.getParams()).getRef());
		}
	}
}

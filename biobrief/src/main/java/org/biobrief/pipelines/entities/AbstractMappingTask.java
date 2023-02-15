package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.util.PipelineUtil.UsesRef;
import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.pipelines.commands.Output.FastaFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractMappingTask extends AbstractFastqTask
{
	protected String ref;
	
	public AbstractMappingTask(){}

	public AbstractMappingTask(TaskType type, AbstractMappingTaskParams params)
	{
		super(type, params);
		this.ref=params.getRef();
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractMappingTaskParams extends AbstractFastqTaskParams
		implements UsesRef
	{
		protected String ref;
		
		@JsonIgnore
		protected FastaFile getRefFile()
		{
			return new FastaFile(ref);
		}
	
		@JsonIgnore
		protected BamFile getBamFile()
		{
			return new BamFile(dir+"/${sample}/reads.bam");
		}
	}
}

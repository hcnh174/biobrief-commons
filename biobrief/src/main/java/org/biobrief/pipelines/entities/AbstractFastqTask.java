package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.pipelines.util.PipelineUtil.UsesFastq;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractFastqTask extends Task
{
	public AbstractFastqTask(){}

	public AbstractFastqTask(TaskType type, AbstractFastqTaskParams params)
	{
		super(type, params);
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractFastqTaskParams extends AbstractTaskParams
		implements UsesFastq
	{		
		@JsonIgnore
		public FastqFile getInFastqFile()
		{
			return new FastqFile(parentDir+"/${sample}/${sample}.fastq");
		}
		
		@JsonIgnore
		public FastqFile getInFastqFile1()
		{
			return new FastqFile(parentDir+"/${sample}/${sample}_R1.fastq");
		}
		
		@JsonIgnore
		public FastqFile getInFastqFile2()
		{
			return new FastqFile(parentDir+"/${sample}/${sample}_R2.fastq");
		}
	}
}

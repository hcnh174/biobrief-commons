package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Output.FastqFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractFastqFilterTask extends AbstractFastqTask
{
	public AbstractFastqFilterTask(){}

	public AbstractFastqFilterTask(TaskType type, AbtractFastqFilterTaskParams params)
	{
		super(type, params);
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbtractFastqFilterTaskParams extends AbstractFastqTaskParams
	{		
		@JsonIgnore
		protected FastqFile getOutFastqFile()
		{
			return new FastqFile(dir+"/${sample}/${sample}.fastq");
		}
		
		@JsonIgnore
		protected FastqFile getOutFastqFile1()
		{
			return new FastqFile(dir+"/${sample}/${sample}_R1.fastq");
		}
		
		@JsonIgnore
		protected FastqFile getOutFastqFile2()
		{
			return new FastqFile(dir+"/${sample}/${sample}_R2.fastq");
		}
	}
}

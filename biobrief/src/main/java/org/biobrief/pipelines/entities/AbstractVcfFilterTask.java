package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Output.VcfFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractVcfFilterTask extends Task
{
	public AbstractVcfFilterTask(){}

	public AbstractVcfFilterTask(TaskType type, AbtractVcfFilterTaskParams params)
	{
		super(type, params);
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbtractVcfFilterTaskParams extends AbstractTaskParams
	{
		@JsonIgnore
		public VcfFile getInVcfFile()
		{
			return new VcfFile(parentDir+"/${sample}/${sample}.vcf");
		}
		
		@JsonIgnore
		public VcfFile getOutVcfFile()
		{
			return new VcfFile(dir+"/${sample}/${sample}.vcf");
		}
	}
}

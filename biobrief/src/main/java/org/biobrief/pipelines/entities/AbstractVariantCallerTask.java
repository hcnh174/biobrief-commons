package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.pipelines.commands.Output.VcfFile;
import org.biobrief.pipelines.util.PipelineUtil.UsesFastq;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractVariantCallerTask extends AbstractBamTask
{
	public AbstractVariantCallerTask(){}

	public AbstractVariantCallerTask(TaskType type, AbstractVariantCallerTaskParams params)
	{
		super(type, params);
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractVariantCallerTaskParams extends AbstractBamTaskParams
	{		
		@JsonIgnore
		public VcfFile getVcfFile()
		{
			return new VcfFile(dir+"/${sample}/${sample}.vcf");
		}
		
		@JsonIgnore
		protected FastqFile getFastqFile(Task parent)
		{
			return (((UsesFastq)parent.getParams()).getInFastqFile());
		}
	}
}

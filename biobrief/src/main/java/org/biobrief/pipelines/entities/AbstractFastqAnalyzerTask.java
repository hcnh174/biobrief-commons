package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractFastqAnalyzerTask extends AbstractFastqTask
{
	public AbstractFastqAnalyzerTask(){}

	public AbstractFastqAnalyzerTask(TaskType type, AbstractFastqAnalyzerTaskParams params)
	{
		super(type, params);
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractFastqAnalyzerTaskParams extends AbstractFastqTaskParams
	{

	}
}

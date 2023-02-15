package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractFastaTask extends Task
{
	public AbstractFastaTask(){}

	public AbstractFastaTask(TaskType type, AbstractFastaTaskParams params)
	{
		super(type, params);
	}
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractFastaTaskParams extends AbstractTaskParams
	{

	}
}

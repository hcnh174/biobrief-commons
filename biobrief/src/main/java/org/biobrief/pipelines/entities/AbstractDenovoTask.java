package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public abstract class AbstractDenovoTask extends AbstractFastqTask
{
	public AbstractDenovoTask(){}

	public AbstractDenovoTask(TaskType type, AbtractDenovoTaskParams params)
	{
		super(type, params);
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbtractDenovoTaskParams extends AbstractFastqTaskParams
	{

	}
}

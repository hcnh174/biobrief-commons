package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.util.CException;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class PipelineTask extends AbstractFastqFilterTask
{	
	protected Params params;
	
	public PipelineTask(){}

	public PipelineTask(Params params)
	{
		super(TaskType.PIPELINE, params);
		this.params=params;
	}

	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbtractFastqFilterTaskParams
	{
		@Override
		public Command getCommand(Task parent)
		{
			throw new CException("PipelineTask does not implement getCommand()");
		}
	}

	
//	@Data @EqualsAndHashCode(callSuper=true)
//	public static class Result extends AbstractFastqResult
//	{
//		public Result(){}
//
//		public Result(PipelineTask batchjob, Sample sample)
//		{
//			super(batchjob, sample);
//		}
//	}
}

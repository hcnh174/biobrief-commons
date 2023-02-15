package org.biobrief.pipelines.entities;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.pipelines.commands.TestCommand;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public class TestTask extends Task
{
	protected Params params;
	
	public TestTask(){}

	public TestTask(Params params)
	{
		super(TaskType.TEST, params);
		this.params=params;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Params extends AbstractTaskParams
	{
		protected Integer num=0;
		
		@Override	
		public Command getCommand(Task parent)
		{
			TestCommand command=new TestCommand();
			command.num(num);
			return command;
		}
	}
}

package org.biobrief.pipelines;

import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.junit.jupiter.api.Test;

//https://junit.org/junit5/docs/current/user-guide/
//https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-assertions-with-hamcrest/

//gradle --stacktrace --info :hlsg-pipelines:test --tests *TestPipelineConstants
@SuppressWarnings("unused")
public class TestPipelineConstants
{	
	@Test
	public void test()
	{
		TaskType type=TaskType.SALMON;
//		for (TaskType tasktype : type.getAvailable())
//		{
//			System.out.println("task: "+tasktype.name());
//		}
	}
}


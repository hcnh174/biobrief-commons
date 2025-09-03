package org.biobrief.pipelines;

import java.io.PrintStream;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info :hlsg-pipelines:test --tests *TestPipelineParser
@SuppressWarnings("unused")
public class TestPipelineParser
{
	private Integer counter=100;
	
	//run pipe 5e888fd87442ac2d518c34de "kraken | krakenfilter | minimap ref=KR819180primer | clairvoyance | filtervcf"
	//@Test
//	public void parsePipe()
//	{
//		String parentId="5e888fd87442ac2d518c34de";
//		String steps="kraken | krakenfilter | minimap ref=KR819180primer | clairvoyante";
//		Pipeline pipeline=PipelineParser.parseSteps(parentId, steps);
//		System.out.println(pipeline.toString());
//	}
	
	@Test
	public void parsePipeline()
	{
		Pipeline pipeline=TestUtils.loadPipeline("test-hbv-nanopore");
		
		System.out.println("max depth="+pipeline.getMaxDepth());
		PrintStream out=System.out;
		
		/*
		JobDependencyMap jobs=new JobDependencyMap();
		jobs.put(runTask(pipeline.getRootNode().getTask(), out));
		for (int depth=0; depth<pipeline.getMaxDepth(); depth++)
		{
			out.println("****************************\ndepth="+depth);
			for (PipelineNode node : pipeline.getNodesAtDepth(depth))
			{
				out.println(node.toString());
				Task task=node.getTask();
				task.setDependency(jobs.get(node));
				jobs.put(runTask(task, out));
			}
			System.out.println("jobs=\n"+jobs);
		}
		*/
	}

	//https://www.baeldung.com/junit-assert-exception
	//https://howtodoinjava.com/junit5/expected-exception-example/
	//@Test
//	public void nonexistentParams()
//	{
//		String parentId="5c19bc20c1c6891ed0d3c52f";
//		String str="nanofilt nonexistent=42 quality=10 minlength=1500 maxlength=4000 headcrop=24 tailcrop=30";
//		Exception exception = Assertions.assertThrows(CException.class, () -> {
//			Pipeline pipeline=PipelineParser.parseString(parentId, str);
//		});
//	}

//	private Task runTask(Task task, PrintStream out)
//	{
//		task.setJobId(counter++);
//		return task;
//	}
}

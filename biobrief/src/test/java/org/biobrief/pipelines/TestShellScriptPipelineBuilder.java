package org.biobrief.pipelines;

import org.biobrief.util.Constants;
import org.biobrief.util.FileHelper;
import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestShellScriptPipelineBuilder
public class TestShellScriptPipelineBuilder
{
	@Test
	public void buildPipeline()
	{
		Pipeline pipeline=TestUtils.loadPipeline("test-hbv-nanopore");
		
		String script=ShellScriptPipelineBuilder.build(pipeline);
		System.out.println(script);
		
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/pipeline.sh", script);
	}
}

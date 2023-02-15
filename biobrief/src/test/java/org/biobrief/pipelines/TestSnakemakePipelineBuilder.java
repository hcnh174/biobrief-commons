package org.biobrief.pipelines;

import org.biobrief.pipelines.util.SnakemakeHelper;
import org.biobrief.util.FileHelper;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info :hlsg-pipelines:test --tests *TestSnakemakePipelineBuilder
public class TestSnakemakePipelineBuilder
{
	@Test
	public void buildPipeline()
	{
		Pipeline pipeline=TestUtils.loadPipeline("test-hbv-nanopore");
		
		SnakemakeHelper.Script script=SnakemakePipelineBuilder.build(pipeline);
		System.out.println(script.format());
		//FileHelper.writeFile(Constants.TMP_DIR+"/pipeline.smk", script.format());
		String outfile=pipeline.getWinDir()+"/"+pipeline.getName()+".smk";
		FileHelper.writeFile(outfile, script.format());
		//snakemake --snakefile test-minimap.smk -r --dry-run
		//snakemake --snakefile test-hbv-nanopore.smk -j 32 --latency-wait 60 --cluster "sbatch --ntasks-per-node=1 --exclusive"
	}

}

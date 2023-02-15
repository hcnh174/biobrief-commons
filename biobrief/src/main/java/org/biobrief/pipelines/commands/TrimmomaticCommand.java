package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.PipelineConstants.TrimmomaticAdapters;

public class TrimmomaticCommand extends AbstractFastqFilterCommand
{
	public TrimmomaticCommand()
	{
		super("run_trimmomatic.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --outdir $outdir --adapters $adapters");
	}
	
	public Command adapters(TrimmomaticAdapters adapters)
	{
		return param("adapters", adapters.getFilename());
	}
}

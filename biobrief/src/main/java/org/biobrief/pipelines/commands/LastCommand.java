package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.PipelineConstants.LastMode;
import org.biobrief.pipelines.commands.Output.BlastTabFile;
import org.biobrief.util.Constants.IO;

public class LastCommand extends AbstractFastqAnalyzerCommand
{
	public LastCommand()
	{
		super("run_last.sh --fastqfile $fastqfile --reffile $reffile --outfile $outfile --mode $mode");
	}
	
	public Command outfile(BlastTabFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
	
	public Command mode(LastMode mode)
	{
		return param("mode", mode.getValue());
	}
}

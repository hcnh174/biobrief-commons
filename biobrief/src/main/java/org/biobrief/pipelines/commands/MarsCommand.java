package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.PipelineConstants.MarsMethod;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.util.Constants.IO;

public class MarsCommand extends AbstractFastqAnalyzerCommand
{
	public MarsCommand()
	{
		super("run_mars.sh --infile $infile --reffile $reffile --outfile $outfile --method $method");
	}
	
	public Command reffile(FastaFile outfile)
	{
		return param("reffile", outfile);
	}
	
	public Command outfile(FastaFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
	
	public Command method(MarsMethod method)
	{
		return param("method", method.getValue().toString());
	}
}

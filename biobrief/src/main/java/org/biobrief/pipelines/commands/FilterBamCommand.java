package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.util.Constants.IO;

public class FilterBamCommand extends AbstractBamCommand
{
	public FilterBamCommand()
	{
		super("run_filter_bam.sh --bamfile $bamfile --outfile $outfile --flags $flags --quality $quality");
	}

	public Command flags(String flags)//"-F 4" 
	{
		return param("flags", flags);
	}
	
	public Command quality(Integer quality)//10
	{
		return param("quality", quality);
	}
	
	public Command outfile(BamFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
}
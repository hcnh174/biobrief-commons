package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.util.Constants.IO;

public class BowtieCommand extends AbstractMappingCommand
{
	public BowtieCommand()
	{
		super("run_bowtie.sh --fastqfile1 \"$fastqfile1\" --fastqfile2 \"$fastqfile2\" --reffile \"$reffile\" --outfile \"$outfile\" --flags \"$flags\" --quality \"$quality\" --fixed invariant");
	}
	
	public Command outfile(BamFile bamfile)
	{
		return param("outfile", bamfile, IO.output);
	}
	
	public Command flags(String flags)
	{
		return param("flags", flags);
	}
	
	public Command quality(Integer quality)
	{
		return param("quality", quality);
	}
}

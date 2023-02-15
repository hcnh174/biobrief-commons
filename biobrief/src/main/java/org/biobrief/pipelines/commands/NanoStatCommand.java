package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.TextFile;
import org.biobrief.util.Constants.IO;

public class NanoStatCommand extends AbstractFastqAnalyzerCommand
{
	public NanoStatCommand()
	{
		super("run_nanostat.sh --fastqfile $fastqfile --outfile $outfile");
	}
	
	public Command outfile(TextFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
}

package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.HtmlFile;
import org.biobrief.util.Constants.IO;

public class NanoQCCommand extends AbstractFastqAnalyzerCommand
{
	public NanoQCCommand()
	{
		super("run_nanoqc.sh --fastqfile $fastqfile --outfile $outfile");
	}
	
	public Command outfile(HtmlFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
}

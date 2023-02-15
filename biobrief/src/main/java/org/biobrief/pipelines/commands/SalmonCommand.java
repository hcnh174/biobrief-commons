package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.TextFile;
import org.biobrief.util.Constants.IO;

public class SalmonCommand extends AbstractMappingCommand
{
	public SalmonCommand()
	{
		super("run_salmon.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outfile $outfile");
	}
	
	public Command outfile(TextFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
}

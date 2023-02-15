package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.util.Constants.IO;

public class BridgerCommand extends AbstractDenovoCommand
{
	public BridgerCommand()
	{
		super("run_bridger.sh --infile $infile --outfile $outfile");
	}
	
	public Command outfile(FastaFile fastafile)
	{
		return param("outfile", fastafile, IO.output);
	}
}

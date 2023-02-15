package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.TextFile;
import org.biobrief.util.Constants.IO;

public class SealCommand extends AbstractFastqCommand
{
	public SealCommand()
	{
		super("run_seal.sh --fastqfile $fastqfile --reffile1 $reffile1 --reffile2 $reffile2 --statsfile $statsfile");
	}
	
	public Command reffile1(FastaFile reffile)
	{
		return param("reffile1", reffile);
	}
	
	public Command reffile2(FastaFile reffile)
	{
		return param("reffile2", reffile);
	}
	
	public Command statsfile(TextFile statsfile)
	{
		return param("statsfile", statsfile, IO.output);
	}
}

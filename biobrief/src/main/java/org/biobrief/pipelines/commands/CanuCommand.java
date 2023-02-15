package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.util.Constants.IO;

public class CanuCommand extends AbstractDenovoCommand
{
	public CanuCommand()
	{
		super("run_canu.sh --fastqfile $fastqfile --outfile $outfile --genome-size $genomeSize");
	}
	
	public Command outfile(FastaFile fastafile)
	{	
		return param("outfile", fastafile, IO.output);
	}
	
	public Command genomeSize(String genomeSize)
	{
		return param("genomeSize", genomeSize);
	}
}
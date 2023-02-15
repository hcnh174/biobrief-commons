package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.util.Constants.IO;

public class FastqToFastaCommand extends AbstractFastqCommand
{
	public FastqToFastaCommand()
	{
		super("run_fastq_to_fasta.sh --fastqfile $fastqfile --outfile $outfile");
	}
	
	public Command outfile(FastaFile fastafile)
	{
		return param("outfile", fastafile, IO.output);
	}
}

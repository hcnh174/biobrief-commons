package org.biobrief.pipelines.commands;

public class HiSatCommand extends AbstractMappingCommand
{
	public HiSatCommand()
	{
		super("run_hisat.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outfile $outfile");
	}
}

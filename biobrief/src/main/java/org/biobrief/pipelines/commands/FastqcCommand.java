package org.biobrief.pipelines.commands;

public class FastqcCommand extends AbstractFastqFilterCommand
{
	public FastqcCommand()
	{
		super("run_fastqc.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --outdir $outdir");
	}
}

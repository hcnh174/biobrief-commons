package org.biobrief.pipelines.commands;

public class FastpCommand extends AbstractFastqFilterCommand
{
	public FastpCommand()
	{
		super("run_fastp.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --outdir $outdir");
	}
}

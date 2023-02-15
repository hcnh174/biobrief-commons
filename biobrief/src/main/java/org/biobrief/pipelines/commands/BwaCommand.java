package org.biobrief.pipelines.commands;

public class BwaCommand extends AbstractMappingCommand
{
	public BwaCommand()
	{
		super("run_bwa.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outdir $outdir");
	}
}
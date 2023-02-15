package org.biobrief.pipelines.commands;

public class SubreadCommand extends AbstractMappingCommand
{
	public SubreadCommand()
	{
		super("run_subread.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outfile $outfile");
	}
}

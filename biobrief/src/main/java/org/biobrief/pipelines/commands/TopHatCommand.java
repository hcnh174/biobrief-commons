package org.biobrief.pipelines.commands;

public class TopHatCommand extends AbstractMappingCommand
{
	public TopHatCommand()
	{
		super("run_tophat.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outfile $outfile");
	}
}

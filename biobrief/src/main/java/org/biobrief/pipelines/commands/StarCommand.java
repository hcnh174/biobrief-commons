package org.biobrief.pipelines.commands;

public class StarCommand extends AbstractMappingCommand
{
	public StarCommand()
	{
		super("run_star.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --reffile $reffile --outfile $outfile");
	}
}

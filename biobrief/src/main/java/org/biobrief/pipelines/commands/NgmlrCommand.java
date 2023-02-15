package org.biobrief.pipelines.commands;

public class NgmlrCommand extends AbstractMappingCommand
{
	public NgmlrCommand()
	{
		super("run_ngmlr.sh --fastqfile $fastqfile --reffile $reffile --outfile $outfile");
	}
}

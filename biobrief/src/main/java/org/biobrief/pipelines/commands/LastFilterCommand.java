package org.biobrief.pipelines.commands;

public class LastFilterCommand extends AbstractFastqFilterCommand
{
	public LastFilterCommand()
	{
		super("run_last_filter.sh --fastqfile $fastqfile --outfile $outfile");
	}
}

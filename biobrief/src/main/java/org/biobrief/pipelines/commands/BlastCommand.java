package org.biobrief.pipelines.commands;

public class BlastCommand extends AbstractFastaCommand
{
	public BlastCommand()
	{
		super("run_blast.sh --fastafile $fastafile --outfile $outfile");
	}
}

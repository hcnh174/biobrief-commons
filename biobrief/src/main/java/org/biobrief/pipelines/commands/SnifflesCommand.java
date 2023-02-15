package org.biobrief.pipelines.commands;

public class SnifflesCommand extends AbstractVariantCallerCommand
{
	public SnifflesCommand()
	{
		super("run_sniffles.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

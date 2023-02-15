package org.biobrief.pipelines.commands;

public class NanoSvCommand extends AbstractVariantCallerCommand
{
	public NanoSvCommand()
	{
		super("run_nanosv.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

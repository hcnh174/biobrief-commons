package org.biobrief.pipelines.commands;

public class NanoVarCommand extends AbstractVariantCallerCommand
{
	public NanoVarCommand()
	{
		super("run_nanovar.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

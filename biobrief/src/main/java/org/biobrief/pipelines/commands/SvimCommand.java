package org.biobrief.pipelines.commands;

public class SvimCommand extends AbstractVariantCallerCommand
{
	public SvimCommand()
	{
		super("run_svim.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

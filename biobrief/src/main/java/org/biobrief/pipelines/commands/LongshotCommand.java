package org.biobrief.pipelines.commands;

public class LongshotCommand extends AbstractVariantCallerCommand
{
	public LongshotCommand()
	{
		super("run_longshot.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

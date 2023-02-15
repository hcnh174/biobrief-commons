package org.biobrief.pipelines.commands;

public class DeepVariantCommand extends AbstractVariantCallerCommand
{
	public DeepVariantCommand()
	{
		super("run_deepvariant.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile");
	}
}

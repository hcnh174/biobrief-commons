package org.biobrief.pipelines.commands;

public class FilterVcfCommand extends AbstractVcfCommand
{
	public FilterVcfCommand()
	{
		super("run_filter_vcf.sh --vcffile $vcffile --outfile $outfile");
	}
}

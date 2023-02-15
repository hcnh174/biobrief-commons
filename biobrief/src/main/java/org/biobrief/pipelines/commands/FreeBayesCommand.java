package org.biobrief.pipelines.commands;

public class FreeBayesCommand extends AbstractVariantCallerCommand
{
	public FreeBayesCommand()
	{
		super("run_freebayes.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile --ploidy $ploidy");
	}
	
	public Command ploidy(Integer ploidy)
	{
		return param("ploidy", ploidy);
	}
}

package org.biobrief.pipelines.commands;

public class ClairvoyanteCommand extends AbstractVariantCallerCommand
{
	public ClairvoyanteCommand()
	{
		super("run_clairvoyante.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile --threshold $threshold --min-coverage $minCoverage");
	}
	
	public Command threshold(Float threshold)
	{
		return param("threshold", threshold);
	}
	
	public Command minCoverage(Integer minCoverage)
	{
		return param("minCoverage", minCoverage);
	}
}

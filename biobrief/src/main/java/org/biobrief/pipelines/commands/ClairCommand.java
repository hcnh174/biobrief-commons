package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.PipelineConstants.ClairModel;

public class ClairCommand extends AbstractVariantCallerCommand
{
	public ClairCommand()
	{
		super("run_clair.sh --bamfile $bamfile --reffile $reffile --vcffile $vcffile --model $model --threshold $threshold");
	}

	public Command model(ClairModel model)
	{
		return param("model", model.getValue());
	}
	
	public Command threshold(Float threshold)
	{
		return param("threshold", threshold);
	}
}
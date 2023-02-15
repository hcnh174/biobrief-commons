package org.biobrief.pipelines.commands;

public class FiltLongCommand extends AbstractFastqFilterCommand
{
	public FiltLongCommand()
	{
		super("run_filtlong.sh --fastqfile $fastqfile --outfile $outfile --minlength $minlength");
	}
	
//	public Command quality(Integer quality)
//	{
//		return param("quality", quality);
//	}
	
	public Command minlength(Integer minlength)
	{
		return param("minlength", minlength);
	}
	
//	public Command maxlength(Integer maxlength)
//	{
//		return param("maxlength", maxlength);
//	}
//	
//	public Command headcrop(Integer headcrop)
//	{
//		return param("headcrop", headcrop);
//	}
}
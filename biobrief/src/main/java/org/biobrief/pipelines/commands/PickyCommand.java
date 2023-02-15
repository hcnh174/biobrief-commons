package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.VcfFile;
import org.biobrief.util.Constants.IO;

public class PickyCommand extends AbstractFastqAnalyzerCommand
{
	public PickyCommand()
	{
		super("run_picky.sh --fastqfile $fastqfile --reffile $reffile --vcffile $vcffile");
	}
		
	public Command vcffile(VcfFile vcffile)
	{
		return param("vcffile", vcffile, IO.output);
	}
}

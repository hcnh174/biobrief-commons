package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.pipelines.commands.Output.FastaFile;

public abstract class AbstractBamCommand extends AbstractCommand
{	
	public AbstractBamCommand(String command)
	{
		super(command);
	}
	
	public Command bamfile(BamFile bamfile)
	{
		return param("bamfile", bamfile);
	}
	
	public Command reffile(FastaFile reffile)
	{
		return param("reffile", reffile);
	}
}
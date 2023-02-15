package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;

public abstract class AbstractFastaCommand extends AbstractCommand
{	
	public AbstractFastaCommand(String command)
	{
		super(command);
	}
	
	public Command fastafile(FastaFile fastafile)
	{
		return param("fastafile", fastafile);
	}
}
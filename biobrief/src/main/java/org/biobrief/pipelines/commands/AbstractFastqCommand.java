package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.FastqFile;

public abstract class AbstractFastqCommand extends AbstractCommand
{	
	public AbstractFastqCommand(String command)
	{
		super(command);
	}
	
	public Command fastqfile(FastqFile fastqfile)
	{
		return param("fastqfile", fastqfile);
	}
	
	public Command fastqfile1(FastqFile fastqfile1)
	{
		return param("fastqfile1", fastqfile1.getFilename());
	}
	
	public Command fastqfile2(FastqFile fastqfile2)
	{
		return param("fastqfile2", fastqfile2.getFilename());
	}
	
	public Command reffile(FastaFile reffile)
	{
		return param("reffile", reffile);
	}
}


//public Command fastqfiles(List<FastqFile> fastqfiles)
//{
//	if (fastqfiles.isEmpty())
//		throw new CException("no fastqfiles found");
//	if (fastqfiles.size()==1)
//		return fastqfile(fastqfiles.get(0));
//	if (fastqfiles.size()==2)
//	{
//		fastqfile1(fastqfiles.get(0));
//		fastqfile2(fastqfiles.get(1));
//	}
//	return this;
//}
package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public abstract class AbstractFastqFilterCommand extends AbstractFastqCommand
{	
	public AbstractFastqFilterCommand(String script)
	{
		super(script);
	}
	
	public Command outfile(FastqFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}
	
	public Command outfile1(FastqFile outfile1)
	{
		return param("outfile1", outfile1, IO.output);
	}
	
	public Command outfile2(FastqFile outfile2)
	{
		return param("outfile2", outfile2, IO.output);
	}
}

/*
@Override
public void addOutputFiles(VirtualDirectory vdir)
{
	super.addOutputFiles(vdir);
	vdir.add(new FastqFile(getLocalFile("outfile")));
	vdir.add(new TsvFile("fastqinfo.txt"));
	vdir.add(new TsvFile("log.txt"));
}
public Command outfiles(List<FastqFile> outfiles)
{
	if (outfiles.isEmpty())
		throw new CException("no outfiles found");
	if (outfiles.size()==1)
		return fastqfile(outfiles.get(0));
	if (outfiles.size()==2)
	{
		outfile1(outfiles.get(0));
		outfile2(outfiles.get(1));
	}
	return this;
}
*/

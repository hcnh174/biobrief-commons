package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.util.Constants.IO;

public abstract class AbstractMappingCommand extends AbstractFastqCommand
{	
	public AbstractMappingCommand(String script)
	{
		super(script);
	}
	
	public Command outfile(BamFile bamfile)
	{
		return param("outfile", bamfile, IO.output);
	}
	
//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//		vdir.add(new BamFile(getLocalFile("outfile")));
//		vdir.add(new BaiFile(getLocalFile("outfile")+".bai"));
//		vdir.add(new TsvFile("stats.txt"));
//		vdir.add(new FlagStatFile("flagstat.txt"));
//	}
}

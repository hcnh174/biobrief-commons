package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.BamFile;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.VcfFile;
import org.biobrief.util.Constants.IO;

public abstract class AbstractVariantCallerCommand extends AbstractCommand
{	
	public AbstractVariantCallerCommand(String script)
	{
		super(script);
	}
	
	public Command bamfile(BamFile bamfile)
	{
		return param("bamfile", bamfile);
	}
	
	public Command reffile(FastaFile reffile)
	{
		return param("reffile", reffile);
	}
	
	public Command vcffile(VcfFile vcffile)
	{
		return param("vcffile", vcffile, IO.output);
	}
	
//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//		vdir.add(new VcfFile(getLocalFile("vcffile")));
//		vdir.add(new TsvFile(getLocalFile("vcffile")+".txt"));
//	}
}
package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.VcfFile;
import org.biobrief.util.Constants.IO;

public abstract class AbstractVcfCommand extends AbstractCommand
{	
	public AbstractVcfCommand(String command)
	{
		super(command);
	}
	
	public Command vcffile(VcfFile vcffile)
	{
		return param("vcffile", vcffile);
	}
	
	public Command outfile(VcfFile outfile)
	{
		return param("outfile", outfile, IO.output);
	}

//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//		String vcffile=template.getByName("vcffile").getValue();
//		vdir.add(new VcfFile(FileHelper.stripPath(vcffile)));
//		vdir.add(new TsvFile(FileHelper.stripPath(vcffile)+".txt"));
//	}
}
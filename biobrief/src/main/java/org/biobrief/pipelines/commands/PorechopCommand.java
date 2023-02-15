package org.biobrief.pipelines.commands;

public class PorechopCommand extends AbstractFastqFilterCommand
{
	public PorechopCommand()
	{
		super("run_porechop.sh --fastqfile $fastqfile --outfile $outfile");
	}
	
//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//	}
}

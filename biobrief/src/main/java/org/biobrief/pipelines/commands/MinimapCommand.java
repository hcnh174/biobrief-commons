package org.biobrief.pipelines.commands;

public class MinimapCommand extends AbstractMappingCommand
{
	public MinimapCommand()
	{
		super("run_minimap.sh --fastqfile $fastqfile --reffile $reffile --outfile $outfile");
	}
	
//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//		vdir.add(new LogFile("minimap.log"));
//	}
}

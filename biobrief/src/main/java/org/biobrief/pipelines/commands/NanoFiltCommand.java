package org.biobrief.pipelines.commands;

public class NanoFiltCommand extends AbstractFastqFilterCommand
{
	public NanoFiltCommand()
	{
		super("run_nanofilt.sh --fastqfile $fastqfile --outfile $outfile"
				+ " --quality $quality"
				+ " --minlength $minlength --maxlength $maxlength"
				+ " --headcrop $headcrop --tailcrop $tailcrop");
	}
	
	public Command quality(Integer quality)
	{
		return param("quality", quality);
	}
	
	public Command minlength(Integer minlength)
	{
		return param("minlength", minlength);
	}
	
	public Command maxlength(Integer maxlength)
	{
		return param("maxlength", maxlength);
	}
	
	public Command headcrop(Integer headcrop)
	{
		return param("headcrop", headcrop);
	}
	
	public Command tailcrop(Integer tailcrop)
	{
		return param("tailcrop", tailcrop);
	}
	
//	@Override
//	public void addOutputFiles(VirtualDirectory vdir)
//	{
//		super.addOutputFiles(vdir);
//		String outfile=template.getByName("outfile").getValue();
//		vdir.add(new FastqFile(FileHelper.stripPath(outfile)));
//		vdir.add(new TsvFile("fastqinfo.txt"));
//	}
}

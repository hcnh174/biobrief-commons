package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public class LinkFastqFileCommand extends AbstractCommand
{
	public LinkFastqFileCommand()
	{
		super("run_link_fastq_file.sh --fastqfile $fastqfile --outfile $outfile");
	}
	
	public Command fastqfile(FastqFile fastqfile)
	{
		return param("fastqfile", fastqfile);
	}
	
	public Command outfile(FastqFile fastqfile)
	{
		return param("outfile", fastqfile, IO.output);
	}
}

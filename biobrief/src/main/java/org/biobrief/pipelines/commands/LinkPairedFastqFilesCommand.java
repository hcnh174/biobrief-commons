package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public class LinkPairedFastqFilesCommand extends AbstractCommand
{
	public LinkPairedFastqFilesCommand()
	{
		super("run_link_paired_fastq_files.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --outfile1 $outfile1 --outfile2 $outfile2");
	}
	
	public Command fastqfile1(FastqFile fastqfile1)
	{
		return param("fastqfile1", fastqfile1);
	}
	
	public Command fastqfile2(FastqFile fastqfile2)
	{
		return param("fastqfile2", fastqfile2);
	}
	
	public Command outfile1(FastqFile fastqfile)
	{
		return param("outfile1", fastqfile, IO.output);
	}
	
	public Command outfile2(FastqFile fastqfile)
	{
		return param("outfile2", fastqfile, IO.output);
	}
}

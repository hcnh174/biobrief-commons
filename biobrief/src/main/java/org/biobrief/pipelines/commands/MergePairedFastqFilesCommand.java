package org.biobrief.pipelines.commands;

import java.util.List;

import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public class MergePairedFastqFilesCommand extends AbstractCommand
{
	public MergePairedFastqFilesCommand()
	{
		super("run_merge_paired_fastq_files.sh --fastqfiles1 \"$fastqfiles1\" --fastqfiles2 \"$fastqfiles2\" --outfile1 $outfile1 --outfile2 $outfile2");
	}
	
	public Command fastqfiles1(List<FastqFile> fastqfiles1)
	{
		return param("fastqfiles1", Output.joinFiles(fastqfiles1));
	}
	
	public Command fastqfiles2(List<FastqFile> fastqfiles2)
	{
		return param("fastqfiles2", Output.joinFiles(fastqfiles2));
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

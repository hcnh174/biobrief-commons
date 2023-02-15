package org.biobrief.pipelines.commands;

import java.util.List;

import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public class MergeFastqFilesCommand extends AbstractCommand
{
	public MergeFastqFilesCommand()
	{
		super("run_merge_fastq_files.sh --fastqfiles \"$fastqfiles\" --outfile $outfile");
	}
	
	public Command fastqfiles(List<FastqFile> fastqfiles)
	{
		return param("fastqfiles", Output.joinFiles(fastqfiles));
	}
	
	public Command outfile(FastqFile fastqfile)
	{
		return param("outfile", fastqfile, IO.output);
	}
}

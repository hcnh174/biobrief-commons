package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.Dir;
import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.util.Constants.IO;

public class NanopolishCommand extends AbstractVariantCallerCommand
{
	public NanopolishCommand()
	{
		super("run_nanopolish.sh --fast5dir $fast5dir --fastqfile $fastqfile --bamfile $bamfile --reffile $reffile --vcffile  $vcffile --consensusfile $consensusfile");
	}
	
	public Command fast5dir(Dir fast5dir)
	{
		return param("fast5dir", fast5dir);
	}
	
	public Command fastqfile(FastqFile fastqfile)
	{
		return param("fastqfile", fastqfile);
	}
	
	public Command consensusfile(FastaFile consensusfile)
	{
		return param("consensusfile", consensusfile, IO.output);
	}
}

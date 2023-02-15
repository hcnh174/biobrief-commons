package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.FastaFile;
import org.biobrief.util.Constants.IO;

public class ConsensusCommand extends AbstractBamCommand
{
	public ConsensusCommand()
	{
		super("run_consensus.sh --bamfile $bamfile --reffile $reffile --consensusfile $consensusfile");
	}
	
	public Command consensusfile(FastaFile consensusfile)
	{
		return param("consensusfile", consensusfile, IO.output);
	}
}
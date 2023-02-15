package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.TsvFile;
import org.biobrief.util.Constants.IO;

public class KrakenCommand extends AbstractFastqFilterCommand
{
	public KrakenCommand()
	{
		super("run_kraken.sh --fastqfile $fastqfile --krakenfile $krakenfile --outfile $outfile");
	}
	
	public Command krakenfile(TsvFile krakenfile)
	{
		return param("krakenfile", krakenfile, IO.output);
	}
}

//@Override
//public void addOutputFiles(VirtualDirectory vdir)
//{
//	vdir.add(new FastqFile("classified.fastq"));
//	vdir.add(new TsvFile("kraken.txt"));
//	vdir.add(new TsvFile("report.txt"));
//	vdir.add(new FastqFile("unclassified.fastq"));
//}
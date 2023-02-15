package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.TsvFile;
import org.biobrief.util.Constants.IO;

public class KrakenPairedCommand extends AbstractFastqFilterCommand
{
	public KrakenPairedCommand()
	{
		super("run_kraken.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --krakenfile $krakenfile --outfile1 $outfile1 --outfile2 $outfile2");
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
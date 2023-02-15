package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.TsvFile;

public class KrakenFilterCommand extends AbstractFastqFilterCommand
{
	public KrakenFilterCommand()
	{
		super("run_kraken_filter.sh --fastqfile $fastqfile --krakenfile $krakenfile --outfile $outfile --taxid $taxid");
	}
	
	public Command krakenfile(TsvFile krakenfile)
	{
		return param("krakenfile", krakenfile);
	}
	
	public Command taxid(Integer taxid)
	{
		return param("taxid", taxid);
	}
}

//@Override
//public void addOutputFiles(VirtualDirectory vdir)
//{
//	super.addOutputFiles(vdir);
//	vdir.add(new FastqFile(getLocalFile("outfile")));
//	vdir.add(new TsvFile("fastqinfo.txt"));// todo!
//	vdir.add(new TsvFile("idfile.txt"));
//}

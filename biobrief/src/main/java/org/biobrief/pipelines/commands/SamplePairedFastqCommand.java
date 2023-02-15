package org.biobrief.pipelines.commands;

public class SamplePairedFastqCommand extends AbstractFastqFilterCommand
{
	public SamplePairedFastqCommand()
	{
		super("run_sample_paired_fastq_files.sh --fastqfile1 $fastqfile1 --fastqfile2 $fastqfile2 --outfile1 $outfile1 --outfile2 $outfile2 --num $num");
	}
	
	public Command num(Integer num)
	{
		return param("num", num);
	}
}

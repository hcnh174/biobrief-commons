package org.biobrief.pipelines.commands;

public class SampleFastqCommand extends AbstractFastqFilterCommand
{
	public SampleFastqCommand()
	{
		super("run_sample_fastq_file.sh --fastqfile $fastqfile --outfile $outfile --num $num");
	}
	
	public Command num(Integer num)
	{
		return param("num", num);
	}
}

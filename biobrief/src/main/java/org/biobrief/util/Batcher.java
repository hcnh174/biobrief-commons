package org.biobrief.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Batcher
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(Batcher.class);
	
	protected int batchsize;
	protected int total;
	protected int delay;
	protected int numbatches;

	public Batcher(int batchsize, int total, int delay)
	{
		this.batchsize=batchsize;
		this.total=total;
		this.delay=delay;
		this.numbatches=MathHelper.getNumbatches(total,batchsize);
	}
	
	public Batcher(int batchsize, int total)
	{
		this(batchsize,total,0);
	}
	
	public void start()
	{
		for (int batchnumber=0;batchnumber<numbatches;batchnumber++)
		{
			int fromIndex=batchnumber*this.batchsize;
			int toIndex=fromIndex+this.batchsize;
			if (toIndex>=this.total)
				toIndex=this.total;
			//logger.debug("batch load ids - from "+fromIndex+" to "+toIndex);
			doBatch(fromIndex,toIndex,batchnumber);
			if (batchnumber<this.numbatches-1)
				sleep();
		}
	}
	
	protected void sleep()
	{
		if (this.delay>0)
			ThreadHelper.sleep(this.delay);
	}
	
	protected abstract void doBatch(int fromIndex, int toIndex, int batchnumber);
}
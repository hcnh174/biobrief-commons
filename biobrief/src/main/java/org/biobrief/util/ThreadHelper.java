package org.biobrief.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThreadHelper
{	
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(ThreadHelper.class);
	
	private ThreadHelper(){}
	
	public static void sleep(long millis)
	{
		//logger.debug("going to sleep: "+millis+" milliseconds");
		//Stopwatch stopwatch=Stopwatch.createStarted();
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			StringHelper.println("sleep interrupted: "+e);
		}
		//logger.debug("waking up after "+stopwatch.stop()+" milliseconds");
	}
}
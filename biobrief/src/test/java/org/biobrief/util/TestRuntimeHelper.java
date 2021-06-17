package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestRuntimeHelper
public class TestRuntimeHelper
{
	@Test
	public void execute()
	{
		String output=RuntimeHelper.execute("java", "-version");
		System.out.println("output="+output);
	}
	
	@Test
	public void killProcess()
	{
		RuntimeHelper.killProcess(999999999);
	}
}
	
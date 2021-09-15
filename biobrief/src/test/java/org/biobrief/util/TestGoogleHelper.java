package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestGoogleHelper
public class TestGoogleHelper
{	
	@Test
	public void testSomething() throws Exception
	{
		System.out.println("testing something");
		GoogleHelper.main();
		assertThat("google").endsWith("gle");
	}
}

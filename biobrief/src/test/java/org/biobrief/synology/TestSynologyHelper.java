package org.biobrief.synology;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestSynologyHelper
public class TestSynologyHelper
{	
	@Test
	public void setPassword()
	{
		SynologyHelper.SynouserSetPasswordCommand command=SynologyHelper.setPassword("hopedbtestuser", "8U7gT6f$q");
		System.out.println("command="+command.format());
	}
}

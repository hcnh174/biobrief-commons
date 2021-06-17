package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestEmailHelper
public class TestEmailHelper
{
	@Test
	public void sendEmail()
	{
		EmailHelper.sendEmail("TODO", "sending email from localhost", "body of the messsage");
	}
}
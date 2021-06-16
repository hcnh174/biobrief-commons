package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info :biobrief-util:test --tests *TestSendGridHelper
public class TestSendGridHelper
{
	//@Test
	public void sendEmail()
	{
		SendGridHelper.sendEmail("hlsgdata@gmail.com", "nelsonhayes4@gmail.com", "test SendMail 2", "body of the messsage");
	}
}
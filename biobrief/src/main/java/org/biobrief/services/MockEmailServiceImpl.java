package org.biobrief.services;

import org.biobrief.util.CException;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

//http://docs.spring.io/spring/docs/4.1.6.RELEASE/spring-framework-reference/htmlsingle/#mail
//https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
//https://tecadmin.net/ways-to-send-email-from-linux-command-line/
@Component
public class MockEmailServiceImpl extends AbstractEmailService
{	
	@Override
	public void sendEmail(SimpleMailMessage message, MessageWriter out)
	{
		if (message.getTo().length==0)
			throw new CException("No To: addresses specified in email with subject: "+message.getSubject());
		out.println("MockEmailServiceImpl.sendEmal: "+JsonHelper.toJson(message));
		
//		try
//		{
//			if (isEmailException())
//				return;
//			logEmail(message, out);
//			this.mailSender.send(message);
//		}
//		catch(MailException e)
//		{
//			//log.debug(e.getMessage());
//			//e.printStackTrace();
//			logEmailError(message, e, out);
//			//throw new CException(e);
//		}
	}
}

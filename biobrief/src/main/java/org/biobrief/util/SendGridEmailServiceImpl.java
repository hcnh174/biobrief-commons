package org.biobrief.util;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

//http://docs.spring.io/spring/docs/4.1.6.RELEASE/spring-framework-reference/htmlsingle/#mail
//https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
//https://tecadmin.net/ways-to-send-email-from-linux-command-line/
@Component
public class SendGridEmailServiceImpl extends AbstractEmailService
{
	public static final Integer WARNING=50;
	public static final Integer MAX=80;
	
	protected Integer counter=0;
	
	@Override
	public void sendEmail(SimpleMailMessage message)
	{
		if (message.getTo().length==0)
			throw new CException("No To: addresses specified in email with subject: "+message.getSubject());
		try
		{
			if (isEmailException())
				return;
			if (counter>WARNING)
			{
				System.out.println("email counter is greater than warning. ignoring: "+counter);
				return;
			}
			logEmail(message);
			SendGridHelper.sendEmail(message);
			counter++;
			System.out.println("email counter="+counter);
		}
		catch(MailException e)
		{
			e.printStackTrace();
			logEmailError(message, e);
			throw new CException(e);
		}
	}
}

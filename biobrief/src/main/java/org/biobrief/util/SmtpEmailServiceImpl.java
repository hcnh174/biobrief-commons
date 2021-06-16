package org.biobrief.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

//http://docs.spring.io/spring/docs/4.1.6.RELEASE/spring-framework-reference/htmlsingle/#mail
//https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
//https://tecadmin.net/ways-to-send-email-from-linux-command-line/
@Component
public class SmtpEmailServiceImpl extends AbstractEmailService
{	
	@Autowired private MailSender mailSender;
	
	//////////////////////////////////////////////////
	
	@Override
	public void sendEmail(SimpleMailMessage message)
	{
		if (message.getTo().length==0)
			throw new CException("No To: addresses specified in email with subject: "+message.getSubject());
		try
		{
			if (isEmailException())
				return;
//			if (message.getFrom()==null)
//				message.setFrom(this.fromAddress);
			logEmail(message);
			this.mailSender.send(message);
		}
		catch(MailException e)
		{
			//log.debug(e.getMessage());
			e.printStackTrace();
			logEmailError(message, e);
			throw new CException(e);//todo
		}
	}
}

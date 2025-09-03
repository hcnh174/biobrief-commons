package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.springframework.mail.SimpleMailMessage;

//http://docs.spring.io/spring/docs/4.1.6.RELEASE/spring-framework-reference/htmlsingle/#mail
//https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
//https://tecadmin.net/ways-to-send-email-from-linux-command-line/
public abstract class AbstractEmailService implements EmailService
{
	@Override
	public void sendEmail(String from, List<String> to, String subject, String body, MessageWriter out)
	{	
		checkEmailAddress(from, "from");
		checkEmailAddresses(to, "to");
		check(subject, "subject");
		check(body, "body");
		
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(StringHelper.convertToArray(to));
		message.setSubject(subject);
		message.setText(body);
		sendEmail(message, out);
	}
	
	@Override
	public void sendEmail(String from, String to, String subject, String body, MessageWriter out)
	{	
		checkEmailAddress(from, "from");
		checkEmailAddress(to, "to");
		check(subject, "subject");
		check(body, "body");
		
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		sendEmail(message, out);
	}
	
	public abstract void sendEmail(SimpleMailMessage message, MessageWriter out);
	
	////////////////////////////////////////////////
	
	protected boolean isEmailException()
	{
		return false;
	}

	protected void logEmail(SimpleMailMessage email, MessageWriter out)
	{
		String logfile="email.txt";
		String message="Sent email to "+StringHelper.join(email.getTo(), ",")+" subject="+email.getSubject();
		out.println(message);
		LogUtil.logMessage(logfile, message);
	}
	
	protected void logEmailError(SimpleMailMessage email, Exception e, MessageWriter out)
	{
		String logfile="email-errors.txt";
		String message="Failed to send email to "+StringHelper.join(email.getTo(), ",")+" subject="+email.getSubject()+" reason="+e.getMessage();
		out.println(message);
		LogUtil.logMessage(logfile, message);
	}
	
	protected void check(String value, String field)
	{
		if (!StringHelper.hasContent(value))
			throw new CException(field+" field is not set: ["+value+"]");
	}
	
	protected void check(List<String> values, String field)
	{
		if (!StringHelper.hasContent(values))
			throw new CException(field+" field is not set: ["+StringHelper.join(values)+"]");
	}
	
	protected void checkEmailAddress(String value, String field)
	{
		if (!StringHelper.hasContent(value))
			throw new CException(field+" field is not set: ["+value+"]");
		if (!StringHelper.isEmailAddress(value))
			throw new CException(field+" field does not seem to be an email address: ["+value+"]");	
	}
	
	protected void checkEmailAddresses(List<String> values, String field)
	{
		if (values.isEmpty())
			throw new CException(field+" email address list is empty");
		for (String value : values)
		{
			checkEmailAddress(value, field);
		}
	}
}

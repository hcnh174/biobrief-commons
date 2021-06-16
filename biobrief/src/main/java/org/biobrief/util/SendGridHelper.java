package org.biobrief.util;

import java.io.IOException;

import org.springframework.mail.SimpleMailMessage;

// using SendGrid's Java Library
// https://github.com/sendgrid/sendgrid-java
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class SendGridHelper
{
	public static void sendEmail(String from, String to, String subject, String body)
	{
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		sendEmail(message);
	}
	
	public static void sendEmail(SimpleMailMessage message)
	{
		try
		{
			Email from = new Email(message.getFrom());
			Email to = new Email(message.getTo()[0]);
			Content content = new Content("text/plain", message.getText());
			Mail mail = new Mail(from, message.getSubject(), to, content);
		
			SendGrid sg = new SendGrid(RuntimeHelper.getEnvironmentVariable("SENDGRID_API_KEY", true));
			Request request = new Request();
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
//	public static void sendEmail(SimpleMailMessage message)
//	{
//		try
//		{
//			Email from = new Email("hlsgdata@gmail.com");
//			Email to = new Email(toAddress);
//			Content content = new Content("text/plain", subject);
//			Mail mail = new Mail(from, subject, to, content);
//		
//			SendGrid sg = new SendGrid(RuntimeHelper.getEnvironmentVariable("SENDGRID_API_KEY", true));
//			Request request = new Request();
//			request.setMethod(Method.POST);
//			request.setEndpoint("mail/send");
//			request.setBody(mail.build());
//			Response response = sg.api(request);
//			System.out.println(response.getStatusCode());
//			System.out.println(response.getBody());
//			System.out.println(response.getHeaders());
//		}
//		catch (IOException e)
//		{
//			throw new CException(e);
//		}
//	}
}
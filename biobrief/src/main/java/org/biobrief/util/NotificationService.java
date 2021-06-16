package org.biobrief.util;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class NotificationService
{
	private final EmailService emailService;
	private final Boolean notify;
	private final List<String> ignoreUsers;
	private final String fromEmailAddress;
	private final String toEmailAddress;
	
	public NotificationService(EmailService emailService, Boolean notify, 
			String fromEmailAddress, String toEmailAddress,
			List<String> ignoreUsers)
	{
		if (!StringHelper.hasContent(fromEmailAddress))
			throw new CException("notification from: email address has not been set");
		if (!StringHelper.hasContent(toEmailAddress))
			throw new CException("notification to: email address has not been set");
		this.emailService=emailService;
		this.notify=notify;
		this.fromEmailAddress=fromEmailAddress;
		this.toEmailAddress=toEmailAddress;
		this.ignoreUsers=ignoreUsers;
//		System.out.println("notificationService: "+StringHelper.toString(this));
	}
	
	public void notify(String username, String subject, String message)
	{
		if (isIgnored(username))
			return;
		notify(subject, message);
	}
	
	public void notify(String subject, String message)
	{
		try
		{
			if (notify)
				emailService.sendEmail(fromEmailAddress, toEmailAddress, subject, message);
		}
		catch(Exception e)
		{
			LogUtil.log("failed to send email: subject="+subject+" message="+message, e);
		}
	}
	
	public boolean isIgnored(String username)
	{
		System.out.println("isIgnored: username="+username+" ignoreUsers="+StringHelper.toString(ignoreUsers));
		return ignoreUsers.contains(username);
	}
}

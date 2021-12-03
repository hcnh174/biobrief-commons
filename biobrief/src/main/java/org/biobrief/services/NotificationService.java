package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
import org.biobrief.util.StringHelper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class NotificationService
{
	private final EmailService emailService;
	private final Boolean notify;
	private final List<String> ignoreUsers=Lists.newArrayList();
	private final String fromEmailAddress;
	private final List<String> toEmailAddresses=Lists.newArrayList();
	
	public NotificationService(EmailService emailService, Boolean notify, 
			String fromEmailAddress, List<String> toEailAddresses, List<String> ignoreUsers)
	{
		if (!StringHelper.hasContent(fromEmailAddress))
			throw new CException("notification from: email address has not been set");
//		if (!StringHelper.hasContent(toEmailAddress))
//			throw new CException("notification to: email address has not been set");
		this.emailService=emailService;
		this.notify=notify;
		this.fromEmailAddress=fromEmailAddress;
		this.toEmailAddresses.addAll(toEmailAddresses);
		this.ignoreUsers.addAll(ignoreUsers);
//		System.out.println("notificationService: "+StringHelper.toString(this));
	}
	
//	public void notify(String username, List<String> toEmailAddresses, String subject, String message)
//	{
//		if (isIgnored(username))
//			return;
//		notify(toEmailAddresses, subject, message);
//	}
	
	public void notify(String subject, String message)
	{
		notify(this.toEmailAddresses, subject, message);
	}
	
	public void notify(List<String> toEmailAddresses, String subject, String message)
	{
		try
		{
			if (notify)
				emailService.sendEmail(fromEmailAddress, toEmailAddresses, subject, message);
		}
		catch(Exception e)
		{
			LogUtil.log("failed to send email: subject="+subject+" message="+message, e);
		}
	}
	
	public boolean isIgnored(String username)
	{
		//System.out.println("isIgnored: username="+username+" ignoreUsers="+StringHelper.toString(ignoreUsers));
		return ignoreUsers.contains(username);
	}
}

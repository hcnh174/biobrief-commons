package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
import org.biobrief.util.MessageWriter;
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
			String fromEmailAddress, List<String> toEmailAddresses, List<String> ignoreUsers)
	{
		if (!StringHelper.hasContent(fromEmailAddress))
			throw new CException("notification from: email address has not been set");
		if (toEmailAddresses.isEmpty())
			throw new CException("notification to: email address has not been set");
		this.emailService=emailService;
		this.notify=notify;
		this.fromEmailAddress=fromEmailAddress;
		this.toEmailAddresses.addAll(toEmailAddresses);
		this.ignoreUsers.addAll(ignoreUsers);
	}
	
	public void notify(String subject, String message, MessageWriter out)
	{
		notify(this.toEmailAddresses, subject, message, out);
	}
	
	public void notify(List<String> toEmailAddresses, String subject, String message, MessageWriter out)
	{
		try
		{
			if (notify)
				emailService.sendEmail(fromEmailAddress, toEmailAddresses, subject, message, out);
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

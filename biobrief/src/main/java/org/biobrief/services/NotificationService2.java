package org.biobrief.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.LogUtil;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.biobrief.util.YamlHelper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.Data;

@Component @Data
public class NotificationService2
{
	private final EmailService emailService;
	private final String configfile;
	private NotificationConfig config=null;
//	private Boolean notify;
//	private List<String> ignoreUsers=Lists.newArrayList();
//	private String fromEmailAddress;
//	private List<String> toEmailAddresses=Lists.newArrayList();
	
	public NotificationService2(EmailService emailService, String configfile)
	{
		this.emailService=emailService;
		this.configfile=configfile;
//		if (!StringHelper.hasContent(fromEmailAddress))
//			throw new CException("notification from: email address has not been set");
//		if (toEmailAddresses.isEmpty())
//			throw new CException("notification to: email address has not been set");
//		this.emailService=emailService;
//		this.notify=notify;
//		this.fromEmailAddress=fromEmailAddress;
//		this.toEmailAddresses.addAll(toEmailAddresses);
//		this.ignoreUsers.addAll(ignoreUsers);
	}
	
//	public void notify(String subject, String message, MessageWriter out)
//	{
//		notify(this.toEmailAddresses, subject, message, out);
//	}
//	
//	public void notify(List<String> toEmailAddresses, String subject, String message, MessageWriter out)
//	{
//		try
//		{
//			if (notify)
//				emailService.sendEmail(fromEmailAddress, toEmailAddresses, subject, message, out);
//		}
//		catch(Exception e)
//		{
//			LogUtil.log("failed to send email: subject="+subject+" message="+message, e);
//		}
//	}
//	
//	public boolean isIgnored(String username)
//	{
//		//System.out.println("isIgnored: username="+username+" ignoreUsers="+StringHelper.toString(ignoreUsers));
//		return ignoreUsers.contains(username);
//	}
	
	public void notify(String event_name, Map<String, Object> map, MessageWriter out)
	{
		NotificationConfig config=this.getConfig();
		if (!config.getEnabled())
			return;
		NotificationConfig.Event event=config.getEvent(event_name);
		if (!event.getEnabled())
			return;
		String subject=event.formatSubject(map);
		String body=event.formatBody(map);
		out.println("notify: event="+event_name+" subject="+subject+" body="+body);
	}
	
	///////////////////////////////////////////
	
	public NotificationConfig load()
	{
		try
		{
			NotificationConfig config=(NotificationConfig)YamlHelper.readFile(this.configfile, NotificationConfig.class);
			Date date=FileHelper.getLastModifiedDate(this.configfile);
			config.setLastUpdated(date);
			return config;
		}
		catch (Exception e)
		{
			String message="Failed to load facilities file: "+e.getMessage();
			message+=StringHelper.getStackTrace(e);
			LogUtil.logMessage("log-notifications.txt", message, e);
			throw e;
		}
	}
	
	public NotificationConfig getConfig()
	{
		if (config==null)
			return load();
		Date date=FileHelper.getLastModifiedDate(this.configfile);
		if (date.after(config.getLastUpdated()))
			return load();
		return config;
	}
	
	/////////////////////////////////////////////////////////

	@Data
	public static class NotificationConfig
	{
		protected Boolean enabled=true;
		protected String from;
		protected List<Group> groups=Lists.newArrayList();
		protected List<User> users=Lists.newArrayList();
		protected List<Event> events=Lists.newArrayList();
		protected Date lastUpdated;
		
		public Event getEvent(String name)
		{
			for (Event event : this.events)
			{
				if (event.matches(name))
					return event;
			}
			throw new CException("cannot find event: "+name);
		}
		
		@Data
		public static class Group
		{
			protected String name;
			protected String description="";
			
			public boolean matches(String name)
			{
				return this.name.equals(name);
			}
		}
		
		@Data
		public static class User
		{
			protected String username;
			protected String name;
			protected String email;
			protected List<String> groups=Lists.newArrayList();
			
			public boolean matches(String username)
			{
				return this.username.equals(username);
			}
		}
		
		@Data
		public static class Event
		{
			protected String name;
			protected String subject="subject";
			protected String body="body";
			protected Boolean enabled=true;
			protected List<String> groups=Lists.newArrayList();
			
			public boolean matches(String name)
			{
				return this.name.equals(name);
			}
			
			public String formatSubject(Map<String, Object> map)
			{
				return format(this.subject, map);
			}
			
			public String formatBody(Map<String, Object> map)
			{
				return format(this.body, map);
			}
			
			private String format(String template, Map<String, Object> map)
			{
				String formatted=template;
				for (String key : map.keySet())
				{
					formatted=StringHelper.replace(template, "{{"+key+"}}", map.get(key).toString());
				}
				return formatted;
			}
		}
	}
}

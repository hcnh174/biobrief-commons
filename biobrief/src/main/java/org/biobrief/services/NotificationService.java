package org.biobrief.services;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.biobrief.users.entities.Login;
import org.biobrief.users.entities.Route;
import org.biobrief.util.CException;
import org.biobrief.util.Context;
import org.biobrief.util.FileHelper;
import org.biobrief.util.LogUtil;
import org.biobrief.util.StringHelper;
import org.biobrief.util.YamlHelper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component @Data
public class NotificationService
{
	public static final String LOGIN_TOPIC="login";
	public static final String ROUTE_TOPIC="route";
	public static final String FILEMANAGER_TOPIC="filemanager";
	
	private final EmailService emailService;
	private final String configfile;
	private NotificationConfig config=null;
	private Boolean enabled;

	public NotificationService(EmailService emailService, String configfile, Boolean enabled)
	{
		this.emailService=emailService;
		this.configfile=configfile;
		this.enabled=enabled;
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
	
	///////////////////////////////////////////
	
	public void notify(String topic_name, Model model, Context context)
	{
		if (!getEnabled())
			return;
		NotificationConfig config=this.getConfig();
		if (!config.getEnabled())
			return;
		if (!config.findServer(context.getServer()).getNotify())
		{
			//context.println("notify disabled because server is set to notify=false");
			return;
		}
		NotificationConfig.Topic topic=config.getTopic(topic_name);
		if (!topic.getEnabled())
			return;
		if (topic.isIgnored(context.getUsername()))
			return;
		
		Notification notification=new Notification(topic_name);
		notification.setSubject(model.getSubject());//topic.formatSubject(model)
		notification.setBody(model.getBody());//topic.formatBody(model)
		notification.setFromAddress(config.getFromEmailAddress(topic));
		notification.setToAddresses(config.getToEmailAddresses(topic));
		
		notify(notification, context);
	}
	
	public void notify(String topic_name, String subject, String body, Context context)
	{
		Model model=new Model();
		model.setSubject(subject);
		model.setBody(body);
		notify(topic_name, model, context);
	}
	
	///////////////////////////////////////////////////////////////////////////	
	
	public void notifyLogin(Login login, Context context)
	{
		notify(LOGIN_TOPIC, login.getSubject(), login.getMessage(), context);
	}
	
	public void notifyRoute(Route route, Context context)
	{
		notify(ROUTE_TOPIC, Route.getSubject(route), Route.getMessage(route), context);
	}
	
	/////////////////////////////////////////////////////////
	
	private void notify(Notification notification, Context context)
	{
		try
		{
			context.println(notification.toString());
			emailService.sendEmail(notification.getFromAddress(), notification.getToAddresses(), notification.getSubject(), notification.getBody(), context.getOut());
		}
		catch(Exception e)
		{
			throw new CException("failed to send email: "+notification.toString(), e);
		}
	}
	
	private NotificationConfig load()
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
	
	//////////////////////////////////////////
	
	@Data
	public static class Notification
	{
		protected final String topicname;
		protected String fromAddress;
		protected List<String> toAddresses=Lists.newArrayList();
		protected String subject="subject";
		protected String body="body";
		
		public Notification(String topicname)
		{
			this.topicname=topicname;
		}
		
		@Override
		public String toString()
		{
			return "notification: topic="+topicname+" subject="+subject+" body="+body+" from="+fromAddress+" to="+StringHelper.join(toAddresses);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class Model extends LinkedHashMap<String, Object>
	{
		private static final long serialVersionUID = 1L;
		private String subject;
		private String body;
		
		public String format(String template)
		{
			String formatted=template;
			for (String key : keySet())
			{
				formatted=StringHelper.replace(template, "{{"+key+"}}", get(key).toString());
			}
			return formatted;
		}
	}
	
	@Data
	public static class NotificationConfig
	{
		protected Boolean enabled=true;
		protected String from;
		protected List<Server> servers=Lists.newArrayList();
		protected List<Group> groups=Lists.newArrayList();
		protected List<User> users=Lists.newArrayList();
		protected List<Topic> topics=Lists.newArrayList();
		protected Date lastUpdated;
		
		// if hostname is not found, one is created dynamically
		public Server findServer(String hostname)
		{
			System.out.println("findServer hostname="+hostname);
			for (Server server : this.servers)
			{
				if (server.getHostname().equals(hostname))
					return server;
			}
			System.out.println("no config for hostname=["+hostname+"]. creating entry");
			return new Server(hostname);
		}
		
		public String getFromEmailAddress(Topic topic)
		{
			if (StringHelper.hasContent(topic.getFrom()))
				return topic.getFrom();
			else return this.from;
		}
		
		public List<String> getToEmailAddresses(Topic topic)
		{
			Set<String> addresses=Sets.newLinkedHashSet();
			for (User user : users)
			{
				for (String groupname : topic.getGroups())
				{
					if (user.hasGroup(groupname))
						addresses.add(user.getEmail());
				}
			}
			return Lists.newArrayList(addresses);
		}
		
		public Topic getTopic(String name)
		{
			for (Topic topic : this.topics)
			{
				if (topic.matches(name))
					return topic;
			}
			throw new CException("cannot find topic: "+name);
		}
		
		///////////////////////////////////////////
		
		public Group getGroup(String name)
		{
			for (Group group : this.groups)
			{
				if (group.matches(name))
					return group;
			}
			throw new CException("cannot find group: "+name);
		}
		
		public List<Group> getGroups(List<String> names)
		{
			List<Group> list=Lists.newArrayList();
			for (String name : names)
			{
				list.add(getGroup(name));
			}
			return list;
		}
		
		////////////////////////////////////////		
		
		public User getUser(String username)
		{
			for (User user : this.users)
			{
				if (user.matches(username))
					return user;
			}
			throw new CException("cannot find user: "+username);
		}
		
		public List<User> getUsers(List<String> names)
		{
			List<User> list=Lists.newArrayList();
			for (String name : names)
			{
				list.add(getUser(name));
			}
			return list;
		}
		
		@Data
		public static class Server
		{
			protected String name;
			protected String hostname;
			protected Boolean notify=false;
			
			public Server() {}
			
			public Server(String hostname)
			{
				this.hostname=hostname;
				String name=hostname;
				if (name.contains("."))
					name=name.substring(0, name.indexOf("."));
				this.name=name;
			}
			
			public boolean matches(String name)
			{
				return this.name.equals(name);
			}
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
			
			public boolean hasGroup(String group)
			{
				return this.groups.contains(group);
			}
		}
		
		@Data
		public static class Topic
		{
			protected String name;
			protected String from;
			protected String subject="subject";
			protected String body="body";
			protected Boolean enabled=true;
			protected List<String> groups=Lists.newArrayList();
			protected List<String> ignoreUsers=Lists.newArrayList();
			
			public boolean matches(String name)
			{
				return this.name.equals(name);
			}
			
			public boolean isIgnored(String username)
			{
				return ignoreUsers.contains(username);
			}
			
			public String formatSubject(Model model)
			{
				return model.format(this.subject);
			}
			
			public String formatBody(Model model)
			{
				return model.format(this.body);
			}
//			
//			private String format(String template, Model model)
//			{
//				String formatted=template;
//				for (String key : map.keySet())
//				{
//					formatted=StringHelper.replace(template, "{{"+key+"}}", map.get(key).toString());
//				}
//				return formatted;
//			}
		}
	}
}

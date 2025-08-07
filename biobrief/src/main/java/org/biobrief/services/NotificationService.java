package org.biobrief.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.biobrief.users.entities.AngularError;
import org.biobrief.users.entities.Login;
import org.biobrief.users.entities.Route;
import org.biobrief.users.entities.PasswordChange;
import org.biobrief.util.CException;
import org.biobrief.util.Context;
import org.biobrief.util.FileHelper;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.LogUtil;
import org.biobrief.util.StringHelper;
import org.biobrief.util.YamlHelper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;

@Component @Data
public class NotificationService
{
	public enum ServerMode {production, development}
	
	public static final String LOGIN_TOPIC="login";
	public static final String SETPW_TOPIC="setpw";
	public static final String ROUTE_TOPIC="route";
	public static final String ERROR_TOPIC="error";
	public static final String FILEMANAGER_TOPIC="filemanager";
	
	private final EmailService emailService;
	private final String configfile;
	private NotificationConfig config=null;
	private Boolean enabled;
	private Boolean trace=true;

	public NotificationService(EmailService emailService, String configfile, Boolean enabled)
	{
		this.emailService=emailService;
		this.configfile=configfile;
		this.enabled=enabled;
	}
	
	public NotificationConfig reload()
	{
		this.config=null;
		return load();
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
	
	public boolean notify(String topic_name, String subject, String body, Context context)
	{
		if (!getEnabled())
			return done("Notification cancelled because NotificationService.enabled is false", context);
		NotificationConfig config=this.getConfig();
		if (!config.getEnabled())
			return done("Notification cancelled because NotificationConfig.enabled is false", context);
		NotificationConfig.Topic topic=config.getTopic(topic_name);
		if (!topic.getEnabled())
			return done("Notification cancelled because topic ["+topic_name+"] enabled is false", context);
		NotificationConfig.Server server=config.findServer(context.getServer());
		if (!server.getNotify())
			return done("Notification cancelled because server "+server.getName()+" notify is false", context);
		if (server.isIgnored(topic))
			return done("Notification cancelled because server mode does not match topic modes:\nserver:\n"+JsonHelper.toJson(server)+"\ntopic:\n"+JsonHelper.toJson(topic), context);
		
		Notification notification=new Notification(topic_name);
		notification.setSubject(subject);
		notification.setBody(body);
		notification.setFromAddress(config.getFromEmailAddress(topic));
		notification.setToAddresses(config.getToEmailAddresses(topic));
		
		notify(notification, context);
		return done("Notification sent: "+JsonHelper.toJson(notification), context);
	}

	///////////////////////////////////////////////////////////////////////////	
	
	public void notifyLogin(Login login, Context context)
	{
		notify(LOGIN_TOPIC, login.getSubject(), login.getMessage(), context);
	}
	
	public void notifyChangePassword(PasswordChange change, Context context)
	{
		notify(SETPW_TOPIC, change.getSubject(), change.getMessage(), context);
	}
	
	public void notifyRoute(Route route, Context context)
	{
		notify(ROUTE_TOPIC, Route.getSubject(route), Route.getMessage(route), context);
	}
	
	public void notifyError(AngularError error, Context context)
	{
		notify(ERROR_TOPIC, AngularError.getSubject(error), AngularError.getMessage(error), context);
	}
	
	/////////////////////////////////////////////////////////
	
	private void notify(Notification notification, Context context)
	{
		try
		{
			if (notification.getToAddresses().isEmpty())
				return;
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
			config.setLastUpdated(FileHelper.getLastModifiedDate(this.configfile));
			config.check(config);
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
	
	private boolean done(String message, Context context)
	{
		if (!this.trace)
			return true;
		//context.println(message);
		return false;
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
		
		public void check(NotificationConfig config)
		{
			if (!StringHelper.hasContent(this.from))
				throw new NotificationConfigException("from address is not set: ["+from+"]", config);
			if (this.servers.isEmpty())
				throw new NotificationConfigException("no servers are configured", config);
			if (this.groups.isEmpty())
				throw new NotificationConfigException("no groups are configured", config);
			if (this.users.isEmpty())
				throw new NotificationConfigException("no users are configured", config);
			if (this.topics.isEmpty())
				throw new NotificationConfigException("no topics are configured", config);
			
			for (Server server : this.servers)
			{
				server.check(config);
			}
			
			for (Group group : this.groups)
			{
				group.check(config);
			}
			
			for (User user : this.users)
			{
				user.check(config);
			}
			
			for (Topic topic : this.topics)
			{
				topic.check(config);
			}
		}
		
		@Data
		public static class Server
		{
			protected String name;
			protected String hostname;
			protected String description="";
			protected ServerMode mode=ServerMode.development;
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

			public boolean isIgnored(Topic topic)
			{
				if (!notify)
					return true;
				return !topic.matchesMode(this.mode);
			}
			
			public void check(NotificationConfig config)
			{
				if (!StringHelper.hasContent(this.name))
					throw new NotificationConfigException("server name is not set: ["+name+"]", config);
				if (!StringHelper.hasContent(this.hostname))
					throw new NotificationConfigException("server hostname is not set: ["+hostname+"]", config);
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
			
			public void check(NotificationConfig config)
			{
				if (!StringHelper.hasContent(this.name))
					throw new NotificationConfigException("group name is not set: ["+name+"]", config);
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
			
			public void check(NotificationConfig config)
			{
				if (!StringHelper.hasContent(this.username))
					throw new NotificationConfigException("user username is not set: ["+username+"]", config);
				if (!StringHelper.hasContent(this.email))
					throw new NotificationConfigException("user email is not set: ["+email+"]", config);
				
				// make sure each group exists
				for (String group : this.groups)
				{
					config.getGroup(group);
				}
			}
		}
		
		@Data
		public static class Topic
		{
			protected String name;
			protected String from;
			protected Boolean enabled=true;
			protected List<String> groups=Lists.newArrayList();
			protected List<ServerMode> modes=Lists.newArrayList();
			
			public boolean matches(String name)
			{
				return this.name.equals(name);
			}

			public boolean matchesMode(ServerMode mode)
			{
				if (modes.isEmpty())
					return true;
				return modes.contains(mode);
			}
			
			public void check(NotificationConfig config)
			{
				if (!StringHelper.hasContent(this.name))
					throw new NotificationConfigException("topic name is not set: ["+name+"]", config);
				if (this.groups.isEmpty())
					throw new NotificationConfigException("no groups are configured for topic", config);
				
				// make sure each group exists
				for (String group : this.groups)
				{
					config.getGroup(group);
				}
			}
		}
		
		@SuppressWarnings("serial")
		public static class NotificationConfigException extends CException
		{
			public NotificationConfigException(String message, NotificationConfig config)
			{
				super(NotificationConfigException.formatMessage(message, config));
			}
			
			private static String formatMessage(String message, NotificationConfig config)
			{
				return "NotificationConfigException: "+message+"\n"+YamlHelper.toYaml(config);
			}
		}
		
	}
}
//@Data //@EqualsAndHashCode(callSuper=true)
//public static class Model //extends LinkedHashMap<String, Object>
//{
//	//private static final long serialVersionUID = 1L;
//	private String subject;
//	private String body;
//	
//	public Model() {}
//	
//	public Model(String subject, String body)
//	{
//		this(subject, 
//	}
//	
////	public String format(String template)
////	{
////		String formatted=template;
////		for (String key : keySet())
////		{
////			formatted=StringHelper.replace(template, "{{"+key+"}}", get(key).toString());
////		}
////		return formatted;
////	}
//}
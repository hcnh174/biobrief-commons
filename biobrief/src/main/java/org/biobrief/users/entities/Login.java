package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.web.WebHelper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="logins") @Data @EqualsAndHashCode(callSuper=true)
public class Login extends AbstractMongoEntity
{
	protected Date date;
	protected boolean succeeded;
	protected String username;
	protected String message;
	protected String server;

	public Login(){}
	
	public Login(boolean succeeded, String username, String message)
	{
		this.date=new Date();
		this.succeeded = succeeded;
		this.username = username;
		this.message = message;
		this.server=WebHelper.getServerName();
	}
	
	public Login(AbstractAuthenticationEvent event)
	{
		Authentication auth = event.getAuthentication();
		this.date=new Date();
		this.succeeded=auth.isAuthenticated();
		this.username=auth.getName();
		this.message=getMessage(event);
		this.server=WebHelper.getServerName();
	}
	
	public static String getMessage(AbstractAuthenticationEvent event)
	{
		Authentication auth=event.getAuthentication();
		return getMessage(auth.getName(), event.getClass().getName(), auth.isAuthenticated());
	}
	
	public static String getMessage(String username, String event, boolean success)
	{
		String server=WebHelper.getServerName();
		String message="Login attempt with username: " + username + "\n";
		message+="Success: " + success+"\n";
		message+="Event="+event+"\n";
		message+="Server="+server+"\n";
		return message;
	}
	
	public String getSubject()
	{
		return username+" login: "+(succeeded ? "success" : "FAILED");
	}
}

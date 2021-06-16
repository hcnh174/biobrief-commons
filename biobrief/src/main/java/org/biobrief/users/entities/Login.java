package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
//import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

@Document(collection="logins") //@Entity
public class Login extends AbstractMongoEntity
{
	// DECLARATIONS_START
	protected Date date; //date
	protected boolean succeeded; //succeeded
	protected String username; //username
	protected String message; //message
	// DECLARATIONS_END

	public Login(){}
	
	public Login(boolean succeeded, String username, String message)
	{
		this.date=new Date();
		this.succeeded = succeeded;
		this.username = username;
		this.message = message;
	}
	
	public Login(AbstractAuthenticationEvent event)
	{
		Authentication auth = event.getAuthentication();
		this.date=new Date();
		this.succeeded=auth.isAuthenticated();
		this.username=auth.getName();
		this.message="Login attempt with username: " + auth.getName() + "\t\tSuccess: " + auth.isAuthenticated()+"\tevent="+event.getClass().getName();
	}
	
	// ACCESSORS_START
	public Date getDate(){return date;}
	public void setDate(final Date date){this.date=date;}
	
	public boolean getSucceeded(){return succeeded;}
	public void setSucceeded(final boolean succeeded){this.succeeded=succeeded;}
	
	public String getUsername(){return username;}
	public void setUsername(final String username){this.username=username;}
	
	public String getMessage(){return message;}
	public void setMessage(final String message){this.message=message;}
	// ACCESSORS_END
}
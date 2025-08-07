package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.web.WebHelper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.event.LogoutSuccessEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="logouts") @Data @EqualsAndHashCode(callSuper=true)
public class Logout extends AbstractMongoEntity
{
	protected Date date;
	protected String username;
	protected String server;

	public Logout(){}
	
	public Logout(String username)
	{
		this.date=new Date();
		this.username = username;
		this.server=WebHelper.getServerName();
	}
	
	public Logout(LogoutSuccessEvent event)
	{
		this(event.getAuthentication().getName());
	}
}

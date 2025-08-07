package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.web.WebHelper;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="passwordchanges") @Data @EqualsAndHashCode(callSuper=true)
public class PasswordChange extends AbstractMongoEntity
{
	protected Date date;
	protected String username;
	protected String server;
	protected String message;

	public PasswordChange(){}
	
	public PasswordChange(String username)
	{
		this.date=new Date();
		this.username = username;
		this.server=WebHelper.getServerName();
		this.message="password change: usename="+username+" server="+server;
	}
	
	public String getSubject()
	{
		return "password changed: username="+username;
	}
}

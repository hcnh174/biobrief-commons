package org.biobrief.users.entities;

import java.util.Date;

//import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.biobrief.util.AbstractEntity;

@Document(collection="activity") //@Entity
public class Activity extends AbstractEntity<String>
{
	@Id
	private String id;
	private Date date;
	private String username;
	private String activity;

	public Activity(){}
	
	public Activity(String username, String activity)
	{
		this.date=new Date();
		this.username=username;
		this.activity=activity;
	}

	public String getId(){return id;}
	
	public Date getDate(){return this.date;}
	public void setDate(final Date date){this.date=date;}

	public String getUsername(){return this.username;}
	public void setUsername(final String username){this.username=username;}
	
	public String getActivity(){return this.activity;}
	public void setActivity(final String activity){this.activity=activity;}
}
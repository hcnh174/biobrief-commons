package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="activity") @Data @EqualsAndHashCode(callSuper=true)
public class Activity extends AbstractMongoEntity
{
	private Date date;
	private String username;
	private String topic;
	private String activity;
	private String details;

	public Activity(){}
	
	public Activity(String username, String topic, String activity, String details)
	{
		this.date=new Date();
		this.username=username;
		this.topic=topic;
		this.activity=activity;
		this.details=details;
	}
}
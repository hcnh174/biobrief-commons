package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="routes") @Data @EqualsAndHashCode(callSuper=true)
public class Route extends AbstractMongoEntity
{
	// DECLARATIONS_START
	protected Date date;
	protected String username;
	protected String server;
	protected String url;
	// DECLARATIONS_END

	public Route(){}
	
	public Route(String username, String server, String url)
	{
		this.date=new Date();
		this.username=username;
		this.server=server;
		this.url=url;
	}
}
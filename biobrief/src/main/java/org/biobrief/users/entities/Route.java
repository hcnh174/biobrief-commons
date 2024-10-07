package org.biobrief.users.entities;

import java.util.Date;

import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.util.DateHelper;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="routes") @Data @EqualsAndHashCode(callSuper=true)
public class Route extends AbstractMongoEntity
{
	protected Date date;
	protected String username;
	protected String server;
	protected String url;

	public Route(){}
	
	public Route(String username, String server, String url)
	{
		this.date=new Date();
		this.username=username;
		this.server=server;
		this.url=url;
	}
	
	public static String getSubject(Route route)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("["+route.getUsername()+"] ");
		buffer.append(route.getUrl()+" ");
		buffer.append("date: "+DateHelper.format(route.getDate(), DateHelper.DATETIME_PATTERN));
		return buffer.toString();
	}
	
	public static String getMessage(Route route)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("username: "+route.getUsername()+"\n");
		buffer.append("server: "+route.getServer()+"\n");
		buffer.append("url: "+route.getUrl()+"\n");
		buffer.append("date: "+DateHelper.format(route.getDate(), DateHelper.DATETIME_PATTERN)+"\n");
		return buffer.toString();
	}
}

package org.biobrief.users.entities;

import java.util.Date;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.util.DateHelper;
import org.biobrief.util.StringHelper;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="errors") @Data @EqualsAndHashCode(callSuper=true)
public class AngularError extends AbstractMongoEntity
{
	protected String server;
	protected String username;
	protected List<String> additional=Lists.newArrayList();//[]
	protected Integer columnNumber;//17
	protected String fileName;//main.js
	protected Integer level;//5
	protected Integer lineNumber;//8172
	protected String message;//exception message
	protected String timestamp;//"2025-05-02T06:39:42.279Z"
	protected Date date;
	
	public static String getSubject(AngularError error)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("["+error.getUsername()+"] angular exception");
		//buffer.append(error.getUrl()+" ");
		buffer.append("date: "+DateHelper.format(error.getDate(), DateHelper.DATETIME_PATTERN));
		return buffer.toString();
	}
	
	public static String getMessage(AngularError error)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("username: "+error.getUsername()+"\n");
		buffer.append("server: "+error.getServer()+"\n");
		buffer.append("additional: ["+StringHelper.join(error.getAdditional())+"]\n");
		buffer.append("date: "+DateHelper.format(error.getDate(), DateHelper.DATETIME_PATTERN)+"\n");
		buffer.append("message: "+error.getMessage()+"\n");
		return buffer.toString();
	}
}

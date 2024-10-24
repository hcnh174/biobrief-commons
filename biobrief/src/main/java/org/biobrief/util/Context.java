package org.biobrief.util;

import org.biobrief.web.WebHelper;

import lombok.Data;

@Data
public class Context
{
	protected final String username;
	protected final String server;
	protected final MessageWriter out;
	
	public Context(String username, MessageWriter out)
	{
		this.username=username;
		this.out=out;
		this.server=WebHelper.getServerName();
	}
	
	public Context(String username)
	{
		this(username, new MessageWriter());
	}
	
	public Context()
	{
		this("system");
	}

	public void println(String message)
	{
		this.out.println(message);
	}
}

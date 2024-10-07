package org.biobrief.util;

import lombok.Data;

@Data
public class Context
{
	protected final String username;
	protected final MessageWriter out;
	
	public Context()
	{
		this("system", new MessageWriter());
	}
	
	public Context(String username, MessageWriter out)
	{
		this.username=username;
		this.out=out;
	}
	
	public void println(String message)
	{
		this.out.println(message);
	}
}

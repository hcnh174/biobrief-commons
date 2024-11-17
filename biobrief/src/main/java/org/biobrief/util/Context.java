package org.biobrief.util;

import java.util.Map;

import org.biobrief.web.WebHelper;

import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class Context
{
	protected final String username;
	protected final String server;
	protected final MessageWriter out;
	protected final Map<String, Object> parameters=Maps.newLinkedHashMap();
	
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
	
	public void setParameter(String name, Object value)
	{
		this.parameters.put(name, value);
	}
	
	public boolean hasParameter(String name)
	{
		return this.parameters.containsKey(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getParamter(String name, T dflt)
	{
		if (this.hasParameter(name))
			return ((T)this.parameters.get(name));
		return dflt;
	}
}

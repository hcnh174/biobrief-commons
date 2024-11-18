package org.biobrief.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.biobrief.web.WebHelper;

import lombok.Data;

@Data
public class Context
{
	protected final String username;
	protected final String server;
	protected final MessageWriter out;
	protected final Parameters parameters=new Parameters();
	
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

	/////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("serial")
	public static class Parameters extends LinkedHashMap<String, Object>
	{
		private static final String FORCE="force";
		
		public Parameters() {}
		
		public Parameters(Map<String, Object> copy)
		{
			this.putAll(copy);
		}
		
		public void setParameter(String name, Object value)
		{
			this.put(name, value);
		}
		
		public boolean hasParameter(String name)
		{
			return this.containsKey(name);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getParameter(String name, T dflt)
		{
			if (this.hasParameter(name))
				return ((T)this.get(name));
			return dflt;
		}
		
		/////////////////////////////////////////////////////////
		
		public void setForce(boolean value)
		{
			this.setParameter(FORCE, value);
		}
		
		public Boolean getForce()
		{
			return (Boolean)getParameter(FORCE, false);
		}
	}
}

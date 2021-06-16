package org.biobrief.util;

@SuppressWarnings("serial")
public class NotImplementedException extends CException
{	
	public NotImplementedException()
	{
		super();
	}
	
	public NotImplementedException(String message)
	{
		super(message);
	}
}

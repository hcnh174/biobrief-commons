package org.biobrief.util;

@SuppressWarnings("serial")
public class UnhandledCaseException extends CException
{	
	public UnhandledCaseException(String message, Object state)
	{
		super(message+state);
	}
	
	public UnhandledCaseException(Object state)
	{
		this("no handler for case: ", state);
	}
}

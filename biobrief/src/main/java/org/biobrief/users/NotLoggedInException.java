package org.biobrief.users;

import org.biobrief.util.CException;

@SuppressWarnings("serial")
public class NotLoggedInException extends CException
{	
	public NotLoggedInException()
	{
		super("Not logged in or session expired");
	}
}
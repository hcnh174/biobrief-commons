package org.biobrief.users;

import org.biobrief.util.CException;

@SuppressWarnings("serial")
public class UserNotFoundException extends CException
{
	protected String user_id;
	
	public UserNotFoundException(String user_id)
	{
		super("User does not exist: "+user_id);
		this.user_id=user_id;
	}
	
	public String getUser_id(){return this.user_id;}
}
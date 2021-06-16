package org.biobrief.users;

import org.biobrief.users.entities.User;

public class UserForm
{
	protected String id;
	protected String username;
	protected String name;
	protected String email;
	
	public UserForm(){}
	
	public UserForm(User user)
	{
		this.id=user.getId();
		this.username=user.getUsername();
		this.name=user.getName();
		this.email=user.getEmail();
	}
	
	public String getId(){return this.id;}
	public void setId(final String id){this.id=id;}
	
	public String getUsername(){return this.username;}
	public void setUsername(final String username){this.username=username;}
	
	public String getName(){return this.name;}
	public void setName(final String name){this.name=name;}

	public String getEmail(){return this.email;}
	public void setEmail(final String email){this.email=email;}
}

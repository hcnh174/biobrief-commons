package org.biobrief.util;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public class RemoteProperties
{
	@Valid @NotNull private String username;
	@Valid @NotNull private String password;
	@Valid @NotNull private String host;
		
	public String getUsername(){return this.username;}
	public void setUsername(final String username){this.username=username;}

	public String getPassword(){return this.password;}
	public void setPassword(final String password){this.password=password;}

	public String getHost(){return this.host;}
	public void setHost(final String host){this.host=host;}
}
package org.biobrief.synology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.biobrief.synology.SynologyHelper.SynouserGetCommand;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RuntimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

// https://www.baeldung.com/spring-security-authentication-provider
@Service
public class SynologyAuthenticationService implements UserDetailsService
{
	@Autowired protected SynologyService synologyService;
	protected Set<String> logins=Sets.newHashSet();
	protected MessageWriter out=new MessageWriter();//new PrintWriter(System.out));
	protected String admin_username=RuntimeHelper.getEnvironmentVariable("ADMIN_USERNAME", false);
	protected String admin_password=RuntimeHelper.getEnvironmentVariable("ADMIN_PASSWORD", false);
	
	public boolean authenticate(String username, String password)
	{
		if (username.equals(admin_username) && password.equals(admin_password))
			return true;
		String key=username+":"+password;
		if (logins.contains(key))
		{
			//System.out.println("SynologyAuthenticationService.authenticate found login in cache username: "+username);
			return true;
		}
		boolean success=synologyService.login(username, password, out);
		//System.out.println("SynologyAuthenticationProvider.login success: "+success);
		if (success)
			logins.add(key);
		return success;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
	{
		//System.out.println("SynologyAuthenticationProvider.loadUserByUsername username: "+username);
		if (username.equals(admin_username))
			return createAdminUser();
		
		SynouserGetCommand.Result result=synologyService.getUser(username, out);
		//System.out.println("SynologyAuthenticationProvider.loadUserByUsername result: "+JsonHelper.toJson(result));
		List<GrantedAuthority> authorities=getAuthorities(result.getGroups());
		User user=new User(username, username, authorities);
		//System.out.println("SynologyAuthenticationProvider.loadUserByUsername user: "+JsonHelper.toJson(user));
		return user;
	}

	public UserDetails createAdminUser()
	{
		List<GrantedAuthority> authorities=getAuthorities(Lists.newArrayList("administrators"));//Role.ROLE_ADMIN
		User user=new User(admin_username, admin_username, authorities);
		return user;
	}
	
	public List<GrantedAuthority> getAuthorities(Collection<String> groups)
	{
		List<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		//authorities.add(new SimpleGrantedAuthority(username));
		for (String group : groups)
		{
			authorities.add(new SimpleGrantedAuthority(group));
		}
		return authorities;
	}
}

package org.biobrief.synology;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

// https://www.baeldung.com/spring-security-authentication-provider
@Service
public class SynologyAuthenticationService implements UserDetailsService
{
	@Autowired protected SynologyService synologyService;
	protected MessageWriter out=new MessageWriter();//new PrintWriter(System.out));
	protected String admin_username=RuntimeHelper.getEnvironmentVariable("ADMIN_USERNAME", false);
	protected String admin_password=RuntimeHelper.getEnvironmentVariable("ADMIN_PASSWORD", false);
	
	public enum Role
	{
		ROLE_NONE,
		ROLE_ADMIN,
		ROLE_REPORT_CHECK,
		ROLE_CORE_MEMBER;
	}
	
	public boolean authenticate(String username, String password)
	{
		if (username.equals(admin_username) && password.equals(admin_password))
			return true;
		
		boolean success=synologyService.login(username, password, out);
		//System.out.println("SynologyAuthenticationProvider.login success: "+success);
		return success;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
	{
		if (username.equals(admin_username))
			return createAdminUser();
		
		SynouserGetCommand.Result result=synologyService.getUser(username, out);
		
		List<Role> roles=Lists.newArrayList();
		if (result.getGroups().contains("administrators"))
			roles.add(Role.ROLE_ADMIN);
		if (result.getGroups().contains("coremembers"))
			roles.add(Role.ROLE_CORE_MEMBER);
		if (result.getGroups().contains("expertpanel"))
			roles.add(Role.ROLE_REPORT_CHECK);
		
		List<GrantedAuthority> authorities=getAuthorities(roles);
		User user=new User(username, username, authorities);

		return user;
	}

	public UserDetails createAdminUser()
	{
		List<GrantedAuthority> authorities=getAuthorities(Lists.newArrayList(Role.ROLE_ADMIN));
		User user=new User(admin_username, admin_username, authorities);
		//user.setName(admin_username);
		//user.setAdministrators(true);
		return user;
	}
	
	public List<GrantedAuthority> getAuthorities(Collection<Role> roles)
	{
		List<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		for (Role role : roles)
		{
			authorities.add(new SimpleGrantedAuthority(role.name()));
		}
		return authorities;
	}
}

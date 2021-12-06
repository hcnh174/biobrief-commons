package org.biobrief.synology;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

// https://www.baeldung.com/spring-security-authentication-provider
// https://stackoverflow.com/questions/59936785/spring-security-context-is-not-updated-on-authenticationmanager-authenticate
//http://javainsimpleway.com/spring-security-using-custom-authentication-provider/
@Component
public class SynologyAuthenticationProvider implements AuthenticationProvider
{
	@Autowired protected SynologyAuthenticationService authenticationService;
	protected Map<String, UsernamePasswordAuthenticationToken> users=Maps.newLinkedHashMap();
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		return authenticate(username, password);
	}

	private Authentication authenticate(String username, String password)
	{
		if (!authenticationService.authenticate(username, password))
			return null;
		String key=username+":"+password;
		if (users.containsKey(key))
			return users.get(key);
		UserDetails user=authenticationService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken token=createToken(user);
		users.put(key, token);
		return token;
	}
	
	private UsernamePasswordAuthenticationToken createToken(UserDetails user)
	{
		//System.out.println("creating authentication token for user: "+user.getUsername());
		UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());
		//System.out.println("token: "+JsonHelper.toJson(token));
		return token;
	}
	
	@Override
	public boolean supports(Class<?> authentication)
	{
		return true;
	}
}

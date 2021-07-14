package org.biobrief.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public final class LoginHelper
{
	public static final String REMEMBER_ME=TokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;
	public static final String LAST_EXCEPTION=WebAttributes.AUTHENTICATION_EXCEPTION;
	public static final String ACCESS_DENIED=WebAttributes.ACCESS_DENIED_403;
	private static final String EMPTY_PASSWORD="";
	
	private LoginHelper(){}
	
	public static void setUser(UserDetails user)
	{
		Collection<GrantedAuthority> authorities=copyAuthorities(user);
		UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(user,EMPTY_PASSWORD,authorities);
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	private static Collection<GrantedAuthority> copyAuthorities(UserDetails user)
	{
		Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		for (GrantedAuthority authority : user.getAuthorities())
		{
			authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
		}
		return authorities;
	}
	
	public static boolean isAnonymous()
	{
		Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		//logger.debug("auth class="+auth.getClass().getName());
		return (auth instanceof AnonymousAuthenticationToken);
	}
	
	public static boolean isAdmin()
	{
		Optional<UserDetails> user=getUserDetails();
		if (!user.isPresent())
			return false;
		return user.get().getUsername().equals("admin");
	}
	
	@SuppressWarnings("unused")
	private static String getDebugInfo()
	{
		StringBuilder buffer=new StringBuilder();
		Optional<Authentication> authentication=getAuthentication();
		if (authentication.isPresent())
		{
			buffer.append("Authentication class="+getAuthentication().getClass().getName()+"\n");
			Object principal=authentication.get().getPrincipal();
			if (principal!=null)
				buffer.append("principal class="+principal.getClass().getName()+"\n");
		}
		return buffer.toString(); 
	}
	
	public static Optional<UserDetails> getUserDetails()
	{
		//logger.debug("getUserDetails");
		Optional<Authentication> authentication=getAuthentication();
		if (authentication==null)
		{
			//logger.debug("authentication is null");
			return Optional.empty();
		}
		//logger.debug("Authentication class="+authentication.getClass().getName());
		Object principal=authentication.get().getPrincipal();
		//logger.debug("principal class="+principal.getClass().getName());
		if (principal instanceof UserDetails)
		{
			//logger.debug("found instance of CUserDetails");
			return Optional.of((UserDetails)principal);
		}
		else
		{
			//logger.debug("principal is not an instance of CUserDetails. returning null");
			return Optional.empty();
		}
	}

	public static Optional<String> getUsername()
	{
		Optional<UserDetails> user=getUserDetails();
		if (user.isPresent())
			return Optional.of(user.get().getUsername());
		else return Optional.empty();
	}
	
	public static String getUsernameStrict()
	{
		Optional<UserDetails> user=getUserDetails();
		if (user.isPresent())
			return user.get().getUsername();
		else throw new CException("cannot find logged in user");
	}
	
	public static String getUsername(String dflt)
	{
		Optional<String> username=LoginHelper.getUsername();
		if (StringHelper.hasContent(username))
			return username.get();
		else return dflt;
	}
	
	public static Collection<GrantedAuthority> getAuthorities(final Collection<String> roles)
	{
		Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		for (String role : roles)
		{
			authorities.add(createGrantedAuthority(role));
		}
		return authorities;
	}
	
	public static Collection<GrantedAuthority> getAuthorities(final String... roles)
	{
		return getAuthorities(Arrays.asList(roles));
	}
	
	public static GrantedAuthority createGrantedAuthority(String role)
	{
		return new SimpleGrantedAuthority(role);
	}
	
	public static String getSavedRequest(HttpServletRequest request, HttpServletResponse response, String dflt)
	{
		String redirect=getSavedRequest(request,response);
		if (redirect==null)
			return dflt;
		else return redirect;
	}
	
	public static String getSavedRequest(HttpServletRequest request, HttpServletResponse response)
	{
		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
		if (savedRequest==null)
			return null;
		return savedRequest.getRedirectUrl();//.getFullRequestUrl();
	}
	
	public static AuthenticationException getLastException(HttpServletRequest request)
	{
		HttpSession session=request.getSession(false);
		if (session==null)
			return null;
		return (AuthenticationException)session.getAttribute(LAST_EXCEPTION);
	}
	
	public static String getReason(HttpServletRequest request)
	{
		AuthenticationException e=getLastException(request);
		String reason=e.getMessage();
		return reason.substring(reason.lastIndexOf(':')+1);
	}
	
	public static AuthenticationException getUnauthorizedException(HttpServletRequest request)
	{	
		return (AuthenticationException)request.getAttribute(ACCESS_DENIED);
	}
	
	public static Optional<Authentication> getAuthentication()
	{	
		final SecurityContext context=SecurityContextHolder.getContext();
		if (context==null)
			return Optional.empty();
		final Authentication authentication=context.getAuthentication();
		if (authentication==null)
			return Optional.empty();
		return Optional.of(authentication);
	}
	
	public static void logout(HttpServletRequest request, HttpServletResponse response)
	{
		//logger.debug("logging out user: "+request.getRemoteUser());
		forgetMe(response,"/");
		endSession(request);
		clearContext();		
	}
	
	// invalidate session if there is one
	private static void endSession(HttpServletRequest request)
	{
		HttpSession session=request.getSession(false);
		if (session!=null)
			session.invalidate();
	}
	
	// erase the remember me cookie
	private static void forgetMe(HttpServletResponse response, String webapp)
	{
		Cookie cookie = new Cookie(REMEMBER_ME, null);
		cookie.setMaxAge(0);
		cookie.setPath(webapp); //You need to add this!!!!!
		response.addCookie(cookie);
	}
	
	private static void clearContext()
	{
		SecurityContextHolder.clearContext(); //invalidate authentication
	}
	
	////////////////////////////////////////////////////////////////
	
	//https://www.passay.org/
	//https://www.baeldung.com/java-generate-secure-password
	//https://www.passay.org/reference/
	//https://www.baeldung.com/java-passay
	public static String generatePassword(int length)
	{
		CharacterRule specialCharacters=new CharacterRule(new CharacterData() {
			@Override
			public String getErrorCode() {
				return CharacterCharacteristicsRule.ERROR_CODE;
			}

			@Override
			public String getCharacters() {
				return "!@#$%&*+";//return "!@#$%^&*()_+";
			}
		}, 1);
		
		List<CharacterRule> rules = Arrays.asList(
			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),
			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),
			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1),
			// at least one symbol (special character)
			//new CharacterRule(EnglishCharacterData.Special, 1),
			specialCharacters
		);
		PasswordGenerator generator = new PasswordGenerator();
		// Generated password is 12 characters long, which complies with polic
		return generator.generatePassword(length, rules);
	}
	
	////////////////////////////////////////////////////////////////

	public enum LoginStatus
	{
		LOGIN,
		LOGOUT,
		CREDENTIALS,
		EXPIRED,
		DISABLED,
		CONCURRENT,
		LOCKED
	};
}

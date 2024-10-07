package org.biobrief.users.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biobrief.mongo.AbstractMongoEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public abstract class AbstractUser extends AbstractMongoEntity implements UserDetails 
{
	@Indexed(unique=true) protected String username;
	@JsonIgnore protected String password;
	
	public AbstractUser(){}
	
	public AbstractUser(String id, String username, String password)
	{
		this.id=id;
		this.username=username;
		this.password=password;
		init();
	}
	
	public void init(){}
	
	public boolean isEnabled(){return true;}
	public boolean isAccountNonExpired(){return true;}
	public boolean isAccountNonLocked(){return true;}
	public boolean isCredentialsNonExpired(){return true;}

	//public abstract List<Role> getRoles();
	
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities()
	{
		Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
//		for (Role role : getRoles())
//		{
//			authorities.add(new SimpleGrantedAuthority(role.name()));
//		}
		return authorities;
	}
	
	@Override
	public String getUsername(){return this.username;}
	public void setUsername(final String username){this.username=username;}

	@Override
	public String getPassword(){return this.password;}
	public void setPassword(final String password){this.password=password;}
}

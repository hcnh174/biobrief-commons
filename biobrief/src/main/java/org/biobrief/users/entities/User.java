package org.biobrief.users.entities;

import java.util.ArrayList;
import java.util.Collection;

import org.biobrief.mongo.AbstractMongoEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Document(collection="users") @Data @EqualsAndHashCode(callSuper=true)
public class User extends AbstractMongoEntity implements UserDetails 
{
	@Indexed(unique=true) protected String username;
	@JsonIgnore protected String password;
	protected String name;
	protected String email;
	
	public User(){}
	
	public User(String id, String username, String password)
	{
		this.id=id;
		this.username=username;
		this.password=password;
		this.name=username;
		this.email="";
		init();
	}
	
	public void init(){}
	
	public boolean isEnabled(){return true;}
	public boolean isAccountNonExpired(){return true;}
	public boolean isAccountNonLocked(){return true;}
	public boolean isCredentialsNonExpired(){return true;}
	
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
}

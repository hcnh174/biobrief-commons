package org.biobrief.users.entities;

import java.util.Arrays;
import java.util.List;

import org.biobrief.users.UserConstants.Role;
//import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Document(collection="users") //@Entity
public class User extends AbstractUser implements UserDetails
{
	public final static List<String> ROLE_PROPERTIES=Arrays.asList("expertpanel", "administrators");
	
	// DECLARATIONS_START
	protected String name;
	protected String kana;
	protected String romaji;
	protected String hirodaiId;
	protected String affiliation;
	protected String email;
	// DECLARATIONS_END
	protected Boolean expertpanel;
	protected Boolean administrators;
	
	public User(){}
	
	public User(String id, String username, String password)
	{
		super(id, username, password);
	}

	@Override
	public void init()
	{
		super.init();
		// INIT_START
		this.name="";
		this.kana="";
		this.romaji="";
		this.hirodaiId="";
		this.affiliation="";
		this.email="";
		// INIT_END
		this.expertpanel=false;
		this.administrators=false;
	}
	
	@JsonIgnore
	public List<Role> getRoles()
	{
		List<Role> roles=Lists.newArrayList();
		if (expertpanel!=null && expertpanel)
			roles.add(Role.ROLE_EXPERT_PANEL);
		if (administrators!=null && administrators)
			roles.add(Role.ROLE_ADMIN);
		if (roles.isEmpty())
			roles.add(Role.ROLE_NONE);
		return roles;
	}

	public boolean hasRole(Role role)
	{
		return getRoles().contains(role);
	}
	
	public static boolean isRoleProperty(String property)
	{
		return ROLE_PROPERTIES.contains(property);
	}
	
	// ACCESSORS_START
	public String getName(){return name;}
	public void setName(final String name){this.name=name;}
	
	public String getKana(){return kana;}
	public void setKana(final String kana){this.kana=kana;}
	
	public String getRomaji(){return romaji;}
	public void setRomaji(final String romaji){this.romaji=romaji;}
	
	public String getHirodaiId(){return hirodaiId;}
	public void setHirodaiId(final String hirodaiId){this.hirodaiId=hirodaiId;}
	
	public String getAffiliation(){return affiliation;}
	public void setAffiliation(final String affiliation){this.affiliation=affiliation;}
	
	public String getEmail(){return email;}
	public void setEmail(final String email){this.email=email;}
	// ACCESSORS_END
	
	public Boolean getExpertpanel(){return this.expertpanel;}
	public void setExpertpanel(final Boolean expertpanel){this.expertpanel=expertpanel;}

	public Boolean getAdministrators(){return this.administrators;}
	public void setAdministrators(final Boolean administrators){this.administrators=administrators;}
}

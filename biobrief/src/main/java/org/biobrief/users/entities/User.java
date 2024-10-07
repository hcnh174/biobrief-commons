package org.biobrief.users.entities;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Document(collection="users") @Data @EqualsAndHashCode(callSuper=true)

public class User extends AbstractUser implements UserDetails
{
	//public final static List<String> ROLE_PROPERTIES=Arrays.asList("expertpanel", "administrators");
	
	// DECLARATIONS_START
	protected String name;
	protected String kana;
	protected String romaji;
//	protected String hirodaiId;
//	protected String affiliation;
	protected String email;
	// DECLARATIONS_END
//	protected Boolean expertpanel;
//	protected Boolean administrators;
	
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
//		this.hirodaiId="";
//		this.affiliation="";
		this.email="";
		// INIT_END
//		this.expertpanel=false;
//		this.administrators=false;
	}
//	
//	@JsonIgnore
//	public List<Role> getRoles()
//	{
//		List<Role> roles=Lists.newArrayList();
//		return roles;
//	}
	
//	@JsonIgnore
//	public List<Role> getRoles()
//	{
//		List<Role> roles=Lists.newArrayList();
//		if (expertpanel!=null && expertpanel)
//			roles.add(Role.ROLE_EXPERT_PANEL);
//		if (administrators!=null && administrators)
//			roles.add(Role.ROLE_ADMIN);
//		if (roles.isEmpty())
//			roles.add(Role.ROLE_NONE);
//		return roles;
//	}
//
//	public boolean hasRole(Role role)
//	{
//		return getRoles().contains(role);
//	}
//	
//	public static boolean isRoleProperty(String property)
//	{
//		return ROLE_PROPERTIES.contains(property);
//	}
}

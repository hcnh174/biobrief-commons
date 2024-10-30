package org.biobrief.users.dao;

import java.util.List;
import java.util.Optional;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.mongo.MongoHelper;
import org.biobrief.users.UserForm;
import org.biobrief.users.entities.User;
import org.biobrief.users.repositories.UserRepository;
import org.biobrief.util.DataFrame;
import org.biobrief.util.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractMongoDao<User, UserRepository>
{
	@Autowired private PasswordEncoder passwordEncoder;

	public Optional<User> findByUsername(String username)
	{
		return repository.findByUsername(username);
	}
	////////////////////////////////////////////////////
	
	//http://www.programcreek.com/java-api-examples/index.php?class=java.util.Optional&method=orElseThrow
	public User getByUsername(String username)
	{
		Optional<User> user=findByUsername(username);
		return user.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	///////////////////////////////////////////////
	
	public User create(String id, String name)
	{
		return new User(id, name, encodePassword(name));
	}
	
	public User create(String id, String name, String password)
	{
		return new User(id, name, encodePassword(password));
	}
	
	public User findOrCreate(String id, String name)
	{
		Optional<User> entity=findByUsername(name);
		if (entity.isPresent())
			return entity.get();
		return create(id, name);
	}
	
	public User update(UserForm form)
	{
		User user=getOne(form.getId());
		copyProperties(user, form);
		save(user);
		return user;
	}
	
	public User findOrCreateUser(String username, String password)
	{
		Optional<User> opt=findByUsername(username);
		if (opt.isPresent())
			return opt.get();
		User user=create(MongoHelper.newId(), username, password);
		save(user);
		return user;
	}
	
	public boolean loadUsers(String filename, MessageWriter writer)
	{
		DataFrame<String> dataframe=DataFrame.parseTabFile(filename);
		for (String rowname : dataframe.getRowNames())
		{
			String id=dataframe.getStringValue("id", rowname);
			String username=dataframe.getStringValue("username", rowname);
			User user=findOrCreate(id, username);
			for (String property : dataframe.getColNames())
			{
				Object value=dataframe.getValue(property, rowname);
				if (property.equals("id") || property.equals("username"))
					doNothing();
				else if (property.equals("password"))
					setPassword(user, value.toString());
//				else if (User.isRoleProperty(property))
//					setBooleanProperty(user, property, value, false);
				else setProperty(user, property, value);
			}
			save(user);
		}
		return true;
	}
		
	private void setPassword(User user, String password)
	{
		user.setPassword(encodePassword(password));
	}
	
	public String encodePassword(String password)
	{
		return passwordEncoder.encode(password);
	}
	
	protected boolean isIgnored(String property)
	{
		return property.startsWith("_") || property.equals("id") || property.equals("username");
	}
	
	///////////////////////////////////////////////////////
	
	public User add(String username)
	{
		return add(create(MongoHelper.newId(), username));
	}
	
	public boolean changePassword(String username, String oldpassword, String newpassword)
	{
		User user=getByUsername(username);
		if (!user.getPassword().equals(encodePassword(oldpassword)))
			return false;
		setPassword(user, newpassword);
		update(user);
		return true;
	}
	
	public List<User> updateUsers(List<User> users)
	{
		for (User user : users)
		{
			update(user);
		}
		return users;
	}
}

//public User findOrCreateAdminUser(String id, String username, String password)
//{	
//	//System.out.println("findOrCreateAdminUser id="+id+" username="+username+" password="+password);
//	Optional<User> opt=findByUsername(username);
//	if (opt.isPresent())
//		return opt.get();
//	StringHelper.announce("Creating default user");
//	User user=new User(id, username, encodePassword(password));
//	user.setName(username);
//	user.setAdministrators(true);
//	save(user);
//	return user;
//}	
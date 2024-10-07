package org.biobrief.users;

import java.util.List;

import org.biobrief.users.dao.UserDao;
import org.biobrief.users.entities.User;
import org.biobrief.util.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@SuppressWarnings("rawtypes")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService, AuthenticationUserDetailsService
{
	//@Autowired private UsersProperties properties;
	@Autowired private UserDao userDao;
	//@Autowired private RouteDao routeDao;
	
	@Override
	public UserDetails loadUserByUsername(String username)
	{
		//log.debug("username="+username);
		//assert(StringHelper.hasContent(username));
		User user=getUserByUsername(username);
//		if (user==null)
//			user=getUserByHirodaiId(username);
		if (user==null)
			throw new UsernameNotFoundException(username);
		//log.debug("user="+user.toString());
		return user;
	}
	
	@Override
	public UserDetails loadUserDetails(Authentication auth)
	{
		//log.debug("auth="+auth.toString()+": "+auth.getPrincipal());
		String username=(String)auth.getPrincipal();
		return loadUserByUsername(username);
	}
	
	public User getUserById(String id)
	{
		return userDao.getOne(id);
	}
	
	public User getUserByUsername(String username)
	{
		return userDao.getByUsername(username);
	}
	
//	public User getUserByHirodaiId(String hirodaiId)
//	{
//		return userDao.getByHirodaiId(hirodaiId);
//	}

	///////////////////////
	
	public User addUser(String username)
	{
		return userDao.add(username);
	}
	
	public void saveUser(User user)
	{
		userDao.update(user);
	}
	
	public void updateUser(UserForm form)
	{
		userDao.update(form);
	}
	
	public List<User> updateUsers(List<User> users)
	{
		return userDao.updateAll(users);
	}
	
	public List<User> getUsers()
	{
		return userDao.findAll();
	}
	
//	public List<User> getUsers(Role role)
//	{
//		List<User> users=Lists.newArrayList();
//		for (User user : userDao.findAll())
//		{
//			if (user.hasRole(role))
//				users.add(user);
//		}
//		return users;
//	}
	
	
	public Page<User> getUsers(Pageable paging)
	{
		return userDao.findAll(paging);
	}
	
	public boolean changePassword(String username, String oldpassword, String newpassword)
	{
		return userDao.changePassword(username, oldpassword, newpassword);
	}

	/////////////////////////////////////////////////////////////
	
//	public boolean loadUsers(MessageWriter writer)
//	{
//		return loadUsers(properties.getFilename(), writer);
//		//return loadUsers(properties.getDir()+"/users.txt", writer);
//	}
//	
//	public boolean loadUsers(String filename, MessageWriter writer)
//	{
//		return userDao.loadUsers(filename, writer);
//	}

	/////////////////////////////////////////////////////////////////////////////
	
//	public User findOrCreateAdminUser()
//	{
//		return userDao.findOrCreateAdminUser(properties.getAdminId(), properties.getAdminUsername(), properties.getAdminPassword());
//	}
	
	//////////////////////////////////////////////////////////////////////////////
	
//	public void logUrl(Route route)
//	{
//		routeDao.logUrl(route);
//	}
}
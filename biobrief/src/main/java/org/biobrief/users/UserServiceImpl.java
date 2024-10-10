package org.biobrief.users;

import java.util.List;

import org.biobrief.users.dao.UserDao;
import org.biobrief.users.entities.User;
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
	@Autowired private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username)
	{
		User user=getUserByUsername(username);
		if (user==null)
			throw new UsernameNotFoundException(username);
		return user;
	}
	
	@Override
	public UserDetails loadUserDetails(Authentication auth)
	{
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
	
	public Page<User> getUsers(Pageable paging)
	{
		return userDao.findAll(paging);
	}
	
	public boolean changePassword(String username, String oldpassword, String newpassword)
	{
		return userDao.changePassword(username, oldpassword, newpassword);
	}
}
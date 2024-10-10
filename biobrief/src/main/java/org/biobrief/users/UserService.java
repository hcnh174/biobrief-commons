package org.biobrief.users;

import java.util.List;

import org.biobrief.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=false)
public interface UserService
{
	User getUserById(String id);
	User getUserByUsername(String username);
	User addUser(String username);
	void saveUser(User user);
	void updateUser(UserForm form);
	List<User> updateUsers(List<User> users);
	List<User> getUsers();
	Page<User> getUsers(Pageable paging);
	boolean changePassword(String username, String oldpassword, String newpassword);
}

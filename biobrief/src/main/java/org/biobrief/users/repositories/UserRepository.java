package org.biobrief.users.repositories;

import java.util.Optional;

import org.biobrief.users.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

//@RepositoryRestResource(collectionResourceRel="users", path="users")
public interface UserRepository extends MongoRepository<User, String> 
{
	Optional<User> findByUsername(@Param("username") String username);
}

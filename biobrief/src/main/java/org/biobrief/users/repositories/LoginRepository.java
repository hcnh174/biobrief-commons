package org.biobrief.users.repositories;

import org.biobrief.users.entities.Login;
import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel="logins", path="logins")
public interface LoginRepository extends MongoRepository<Login, String> 
{

}

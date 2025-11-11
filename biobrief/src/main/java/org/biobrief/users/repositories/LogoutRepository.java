package org.biobrief.users.repositories;

import org.biobrief.users.entities.Logout;
import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel="logouts", path="logouts")
public interface LogoutRepository extends MongoRepository<Logout, String> 
{

}

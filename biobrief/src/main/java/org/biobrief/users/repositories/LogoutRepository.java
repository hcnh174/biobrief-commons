package org.biobrief.users.repositories;

import org.biobrief.users.entities.Logout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="logouts", path="logouts")
public interface LogoutRepository extends MongoRepository<Logout, String> 
{

}

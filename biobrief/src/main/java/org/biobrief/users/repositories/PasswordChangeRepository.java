package org.biobrief.users.repositories;

import org.biobrief.users.entities.PasswordChange;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="passwordchanges", path="passwordchanges")
public interface PasswordChangeRepository extends MongoRepository<PasswordChange, String> 
{

}

package org.biobrief.users.repositories;

import org.biobrief.users.entities.AngularError;
import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel="angular-errors", path="angular-errors")
public interface AngularErrorRepository extends MongoRepository<AngularError, String>
{

}

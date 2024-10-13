package org.biobrief.users.repositories;

import org.biobrief.users.entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="activity", path="activity")
public interface ActivityRepository extends MongoRepository<Activity, String> 
{

}

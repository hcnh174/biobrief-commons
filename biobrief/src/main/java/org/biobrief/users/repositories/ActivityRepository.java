package org.biobrief.users.repositories;

import org.biobrief.users.entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel="activity", path="activity")
public interface ActivityRepository extends MongoRepository<Activity, String> 
{

}

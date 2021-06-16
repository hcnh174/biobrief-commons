package org.biobrief.users.repositories;

import org.biobrief.users.entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> 
{

}

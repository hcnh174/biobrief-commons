package org.biobrief.users.repositories;

import org.biobrief.users.entities.Route;
import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel="routes", path="routes")
public interface RouteRepository extends MongoRepository<Route, String>
{

}

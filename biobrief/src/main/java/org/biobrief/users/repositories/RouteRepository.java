package org.biobrief.users.repositories;

import org.biobrief.users.entities.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="routes", path="routes")
public interface RouteRepository extends MongoRepository<Route, String>
{

}

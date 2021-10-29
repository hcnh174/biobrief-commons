package org.biobrief.users.dao;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.Route;
import org.biobrief.users.repositories.RouteRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RouteDao extends AbstractMongoDao<Route, RouteRepository>
{
	public Route logUrl(Route route)
	{
		//System.out.println("logging url: "+route.toString());
		return repository.save(route);
	}
}
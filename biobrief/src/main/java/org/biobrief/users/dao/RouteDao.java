package org.biobrief.users.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.Route;
import org.biobrief.users.repositories.RouteRepository;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository
public class RouteDao extends AbstractMongoDao<Route, RouteRepository>
{
	@Autowired private RouteRepository repository;
	
	protected RouteRepository getRepository()
	{
		return repository;
	}

	public Route logUrl(Route route)
	{
		//System.out.println("logging url: "+route.toString());
		return repository.save(route);
	}
}
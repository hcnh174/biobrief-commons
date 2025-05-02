package org.biobrief.users.dao;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.AngularError;
import org.biobrief.users.repositories.AngularErrorRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AngularErrorDao extends AbstractMongoDao<AngularError, AngularErrorRepository>
{
	public AngularError logError(AngularError error)
	{
		//System.out.println("logging url: "+route.toString());
		return repository.save(error);
	}
}

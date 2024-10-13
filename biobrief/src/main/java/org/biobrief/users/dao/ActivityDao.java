package org.biobrief.users.dao;

import java.util.List;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.Activity;
import org.biobrief.users.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDao extends AbstractMongoDao<Activity, ActivityRepository>
{
	@Autowired private ActivityRepository repository;

	public Activity add(Activity activity)
	{
		return repository.save(activity);
	}
	
	public List<Activity> findAll()
	{
		return repository.findAll();
	}
	
	public Page<Activity> findAll(Pageable paging)
	{
		return repository.findAll(paging);
	}
}
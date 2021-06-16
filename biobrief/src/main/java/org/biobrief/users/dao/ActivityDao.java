package org.biobrief.users.dao;

import org.biobrief.users.entities.Activity;
import org.biobrief.users.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDao
{
	@Autowired private ActivityRepository repository;
	
	protected ActivityRepository getRepository()
	{
		return repository;
	}

	public Activity logActivity(String username, String url)
	{
		Activity activity=new Activity(username, url);
		//System.out.println("logging url: "+activity.toString());
		return repository.save(activity);
	}
}
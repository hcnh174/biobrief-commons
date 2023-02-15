package org.biobrief.pipelines.dao;

import java.util.List;

import org.biobrief.mongo.AbstractMongoEntityDao;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.entities.Task;
import org.biobrief.pipelines.repositories.TaskRepository;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TaskDao extends AbstractMongoEntityDao<Task, TaskRepository>
{
	public Task create(String id)
	{
		throw new CException("Not implement. Use a derived class create method");
	}
	
	public List<Task> findByProject(String project)
	{
		return getRepository().findByProject(project);
	}
	
	//https://stackoverflow.com/questions/15866175/spring-data-mongodb-criteria-api-oroperator-is-not-working-properly
	public List<Task> findByProject(String project, TaskType type)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("project").is(project).andOperator(Criteria.where("type").is(type)));
		return mongoTemplate.find(query, Task.class);
		//return getRepository().findByProjectAndType(project, type);
	}
	
	public Task findByResultId(String resultId)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("results.id").is(resultId));
		List<Task> batchjobs=mongoTemplate.find(query, Task.class);
		return StringHelper.getOne(batchjobs);
	}
}
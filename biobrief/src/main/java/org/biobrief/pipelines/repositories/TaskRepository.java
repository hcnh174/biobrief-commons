package org.biobrief.pipelines.repositories;

import java.util.List;

import org.biobrief.mongo.MongoEntityRepository;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.entities.Task;
import org.springframework.data.repository.query.Param;

//@RepositoryRestResource(collectionResourceRel="tasks", path="tasks")
public interface TaskRepository extends MongoEntityRepository<Task>
{
	List<Task> findByType(@Param("type") TaskType type);
	List<Task> findByProject(@Param("project") String project);
	List<Task> findByProjectAndType(@Param("project") String project, @Param("type") TaskType type);
}

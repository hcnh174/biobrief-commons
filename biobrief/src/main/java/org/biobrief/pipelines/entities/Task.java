package org.biobrief.pipelines.entities;

import java.util.List;

import org.biobrief.mongo.AbstractMongoEntity;
import org.biobrief.mongo.MongoHelper;
import org.biobrief.pipelines.PipelineConstants.FastqMode;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Command;
import org.biobrief.util.StringHelper;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection="tasks") @Data @EqualsAndHashCode(callSuper=true)
public abstract class Task extends AbstractMongoEntity
{
	protected TaskType type;
	protected String description;
	protected String title;

	public Task(){}

	public Task(TaskType type, AbstractTaskParams params)
	{
		super(params.getId());
		this.type=type;
		this.title=params.getTitle();
		this.description=params.getDescription();
	}
	
	public abstract <T extends AbstractTaskParams> T getParams();

	public Command getCommand(Task parent)
	{
		return getParams().getCommand(parent);
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
	
	@Data
	public static abstract class AbstractTaskParams
	{
		protected String id=MongoHelper.newId();
		protected String parentDir="";
		protected String dir="";
		protected String title="";
		protected String description="";
		protected FastqMode fastqMode=FastqMode.SE;
		protected List<String> samples=Lists.newArrayList();
		
		public void setSamples(String samples)
		{
			this.samples=StringHelper.split(samples, ",", true);
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public abstract Command getCommand(Task parent);
	}
}

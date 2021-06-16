package org.biobrief.mongo;

import org.biobrief.util.StringHelper;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class AbstractNamedMongoEntity extends AbstractMongoEntity
{
	@Indexed(unique=true) protected String name;
	
	public AbstractNamedMongoEntity(){}

	public AbstractNamedMongoEntity(String id, String name)
	{
		super(id);
		StringHelper.checkHasContent(id);
		StringHelper.checkHasContent(name);
		this.name=name;
	}
	
	public String getName(){return this.name;}
	public void setName(final String name){this.name=name;}
}

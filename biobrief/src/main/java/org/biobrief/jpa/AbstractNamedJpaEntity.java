package org.biobrief.jpa;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractNamedJpaEntity extends AbstractJpaEntity
{
	protected String name;
	
	public AbstractNamedJpaEntity(){}
	
	public AbstractNamedJpaEntity(String name)
	{
		this.name=name;
	}
	
	public String getName(){return this.name;}
	public void setName(final String name){this.name=name;}
}


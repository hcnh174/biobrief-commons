package org.biobrief.jpa;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.biobrief.util.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class AbstractJpaEntity extends AbstractEntity<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;
	
	public Integer getId(){return this.id;}//@JsonIgnore
	public void setId(final Integer id){this.id=id;}
	
	@JsonIgnore public boolean isNew()
	{
		return null == getId();
	}
}

package org.biobrief.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import org.biobrief.util.CException;
import org.biobrief.util.DataFrame;
import org.biobrief.util.MessageWriter;

@SuppressWarnings("rawtypes")
@Transactional
public abstract class AbstractNamedJpaDao<T extends AbstractNamedJpaEntity, R extends JpaRepository>
	extends AbstractJpaDao<T,R>
{
	public boolean load(String filename, MessageWriter writer)
	{
		DataFrame<String> dataframe=parseFile(filename);
		for (String name : dataframe.getRowNames())
		{
			T entity=findOrCreate(name);
			for (String property : dataframe.getColNames())
			{
				Object value=dataframe.getValue(property,name);
				setProperty(entity, property, value);	
			}
			save(entity); //hack
		}
		flush();
		return true;
	}
	
	protected DataFrame<String> parseFile(String filename)
	{
		return DataFrame.parseTabFile(filename);
	}
	
	public abstract T findByName(String name);
	
	public T getByName(String name)
	{
		T entity=findByName(name);
		if (entity==null)
			throw new CException("cannot find named entity: "+name);
		return entity;
	}
	
	public abstract T create(String name);
	
	public T findOrCreate(String name)
	{
		T entity=findByName(name);
		if (entity==null)
			entity=create(name);
		return entity;
	}
}
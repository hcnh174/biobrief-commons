package org.biobrief.mongo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.biobrief.util.CException;
import org.biobrief.util.DataFrame;
import org.biobrief.util.MessageWriter;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractNamedMongoEntityDao<T extends AbstractNamedMongoEntity, R extends NamedMongoEntityRepository<T>>
	extends AbstractMongoDao<T, R>
{
	public boolean load(String filename, MessageWriter writer)
	{
		DataFrame<String> dataframe=parseFile(filename);
		for (String rowname : dataframe.getRowNames())
		{
			String id=dataframe.getStringValue("id", rowname);
			String name=dataframe.getStringValue("name", rowname, id); // use id if no name column
			T entity=findOrCreate(id, name);
			for (String property : dataframe.getColNames())
			{
				Object value=dataframe.getValue(property, rowname);
				setProperty(entity, property, value);
			}
			save(entity);
		}
		return true;
	}

	protected DataFrame<String> parseFile(String filename)
	{
		return DataFrame.parseTabFile(filename);
	}
	
	public Optional<T> findByName(String name)
	{
		return repository.findByName(name);
	}
	
	public T getByName(String name)
	{
		Optional<T> entity=findByName(name);
		if (entity.isEmpty())
			throw new CException("cannot find named entity: "+name);
		return entity.get();
	}
	
	public abstract T create(String id, String name);
	
	public T findOrCreate(String id, String name)
	{
		Optional<T> entity=findById(id);
		if (entity.isPresent())
			return entity.get();
		else return create(id, name);
	}
	
	public Map<String, T> asNameMap(Collection<T> items)
	{
		return MongoHelper.asNameMap(items);
	}
	
//	public Map<String, T> asNameMap(Collection<T> items)
//	{
//		Map<String, T> map=Maps.newLinkedHashMap();
//		for (T item : items)
//		{
//			map.put(item.getName(), item);
//		}
//		return map;
//	}
}
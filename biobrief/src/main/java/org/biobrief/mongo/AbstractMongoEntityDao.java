package org.biobrief.mongo;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractMongoEntityDao<T extends AbstractMongoEntity, R extends MongoEntityRepository<T>>
	extends AbstractMongoDao<T, R>
{	
	public T findOrCreate(String id)
	{
		Optional<T> entity=findById(id);
		if (entity.isPresent())
			return entity.get();
		else return create(id);
	}
	
	public abstract T create(String id);
}
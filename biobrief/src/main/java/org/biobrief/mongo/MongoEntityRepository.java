package org.biobrief.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoEntityRepository<E extends AbstractMongoEntity>
	extends MongoRepository<E, String>
{

}

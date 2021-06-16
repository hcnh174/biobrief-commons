package org.biobrief.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface NamedMongoEntityRepository<E extends AbstractNamedMongoEntity>
	extends MongoRepository<E, String>
{
	Optional<E> findByName(@Param("name") String name);
}

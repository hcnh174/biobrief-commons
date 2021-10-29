package org.biobrief.mongo;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//import javax.transaction.Transactional;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.LogUtil;
import org.biobrief.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractMongoDao<T extends AbstractMongoEntity, R extends MongoRepository<T, String>>
{
	@Autowired protected MongoTemplate mongoTemplate;
	@Autowired protected MongoMappingContext mongoMappingContext;
	@Autowired protected R repository;
	protected Class<T> entityClass;
	protected List<String> ignore=Lists.newArrayList("id","createdBy","createdDate","lastModifiedBy","lastModifiedDate","patient_id");
	protected BeanHelper beanhelper=new BeanHelper();

	protected final R getRepository() {return repository;}
	
	public MongoTemplate getMongoTemplate() {return mongoTemplate;} // todo hack!
	
	@SuppressWarnings("unchecked")
	public AbstractMongoDao()
	{
		//https://stackoverflow.com/questions/4837190/java-generics-get-class
		//https://stackoverflow.com/questions/3437897/how-do-i-get-a-class-instance-of-generic-type-t
		this.entityClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//System.out.println("AbstractMongoDao() entityClass="+entityClass.getCanonicalName());
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void initIndicesAfterStartup()
	{
		//System.out.println("AbstractMongoDao.initIndicesAfterStartup: "+entityClass.getCanonicalName());
		IndexOperations indexOps = mongoTemplate.indexOps(entityClass);
		IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
		resolver.resolveIndexFor(entityClass).forEach(indexOps::ensureIndex);
	}
		
	public Optional<T> findById(String id)
	{
		return repository.findById(id);
	}
	
	public T getOne(String id)
	{
		Optional<T> entity=findById(id);
		if (!entity.isPresent())
			throw new CException("cannot find item with id "+id);
		return entity.get();
	}
	
	public List<T> findAll()
	{
		return repository.findAll();
	}
	
	public Page<T> findAll(Pageable paging)
	{
		return repository.findAll(paging);
	}
	
	//https://stackoverflow.com/questions/44172159/spring-data-mongodb-repository-how-can-i-search-by-a-list-of-ids
	//https://stackoverflow.com/questions/30123810/repository-query-with-a-list-parameter-in-spring-data-mongodb/47551966
	public List<T> findAllById(List<String> ids)
	{
		return asList(repository.findAllById(ids));
	}
	
	/*
	public Page<T> findAll(Predicate predicate, Pageable paging)
	{
		//return repository.findAll(predicate, paging);
		throw new CException("Page<T> findAll(String query, Pageable paging) not implemented for "+this.getClass().getName());
	}
	*/

	public List<T> findAll(String query)
	{
		throw new CException("Page<T> findAll(String query) not implemented for "+this.getClass().getName());
	}
	
	public Page<T> findAll(String query, Pageable paging)
	{
		throw new CException("Page<T> findAll(String query, Pageable paging) not implemented for "+this.getClass().getName());
	}
	
	public Page<T> findAll(Query query, Class<T> cls, Pageable paging)
	{
		LogUtil.log("query="+query.getQueryObject().toString());
		System.out.println("query="+query.getQueryObject().toString());
		long total=mongoTemplate.count(query, cls);
		System.out.println("total="+total);
		query.with(paging);
		List<T> list=mongoTemplate.find(query, cls);
		System.out.println("list="+StringHelper.toString(list));
		return new PageImpl<T>(list, paging, total);
	}
	
	public boolean save(T item)
	{
		//log.debug("saving entity: "+StringHelper.toString(item));
		repository.save(item);
		return true;
	}
	
	public <U extends T> List<U> saveAll(Collection<U> items)
	{
		return repository.saveAll(items);
	}
	
	public void delete(String id)
	{
		repository.deleteById(id);
	}
	
	public T addOrUpdate(T item)
	{
		if (item.getId()==null)
			return add(item);
		return update(item);
	}
	
	public T add(T item)
	{
		return (T)repository.save(item);
	}
		
	public T update(T item) // T item
	{
		T entity=getOne(item.getId());
		//log.debug("updating: "+item.toString());
		copyProperties(entity,item);
		return (T)repository.save(entity);
	}
	
	protected boolean setProperty(T entity, String property, Object value)
	{
		if (isIgnored(property))
			return false;
		return BeanHelper.setProperty(entity, property, value);
	}
	
	protected boolean setBooleanProperty(T entity, String property, Object value, Boolean dflt)
	{
		//System.out.println("setBooleanProperty: property="+property+" value="+value+" default="+dflt);
		if (!StringHelper.hasContent(value))
			return setProperty(entity, property, dflt);
		else return setProperty(entity, property, value);
	}
	
	// skip columns with leading underscore
	protected boolean isIgnored(String property)
	{
		return property.startsWith("_") || property.equals("id") || property.equals("name");
	}
	
	public List<T> updateAll(List<T> items)
	{
		for (T item : items)
		{
			update(item);
		}
		return items;
	}
	
	public void deleteAll(List<T> items)
	{
		for (T item : items)
		{
			deleteItem(item);
		}
	}
	
	public void deleteItem(T item)
	{
		repository.delete(item);
	}
	
	public void deleteById(String id)
	{
		repository.deleteById(id);
	}
	
	public void deleteAll()
	{
		repository.deleteAll();
	}
	
	////////////////////////////////////////////////////////
	
	protected boolean copyProperties(T dest, Object src)
	{
		return beanhelper.copyProperties(dest,src,ignore);
	}
	
	protected List<T> asList(Iterable<T> items)
	{
		List<T> list=Lists.newArrayList();
		for (T item : items)
		{
			list.add(item);
		}
		return list;
	}
	
	public Map<String, T> asIdMap(Collection<T> items)
	{
		Map<String, T> map=Maps.newLinkedHashMap();
		for (T item : items)
		{
			map.put(item.getId(), item);
		}
		return map;
	}
	
	protected static void doNothing(){}  
}
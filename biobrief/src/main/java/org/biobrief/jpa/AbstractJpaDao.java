package org.biobrief.jpa;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.google.common.collect.Lists;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractJpaDao<T extends AbstractJpaEntity, R extends JpaRepository>
{
	@Autowired protected DataSource dataSource;
	@Autowired protected R repository;
	protected List<String> ignore=Lists.newArrayList("id","createdBy","createdDate","lastModifiedBy","lastModifiedDate","patient_id");
	protected BeanHelper beanhelper=new BeanHelper();

	protected final R getRepository() {return repository;}
	
	public Optional<T> findById(Integer id)
	{
		return repository.findById(id);
	}
	
	public T findOne(Integer id, boolean check)
	{
		Optional<T> entity=findById(id);
		if (check && !entity.isPresent())
			throw new CException("cannot find item with id "+id);
		return entity.get();
	}
	
	public T getOne(Integer id)
	{
		return (T)repository.getOne(id);
	}
	
	public List<T> findAll()
	{
		return repository.findAll();
	}
	
	public Page<T> findAll(Pageable paging)
	{
		return repository.findAll(paging);
	}

	public List<T> findAll(String query)
	{
		throw new CException("Page<T> findAll(String query) not implemented for "+this.getClass().getName());
	}
	
	public Page<T> findAll(String query, Pageable paging)
	{
		throw new CException("Page<T> findAll(String query, Pageable paging) not implemented for "+this.getClass().getName());
	}
	
//	public Page<T> findAll(Optional<Predicate> filter, Pageable paging)
//	{
//		if (filter.isPresent())
//			return findAll(filter.get(), paging);
//		else return findAll(paging);
//	}
	/*
	public Page<T> findAll(Predicate filter, Pageable paging)
	{
		throw new CException("Page<T> findAll(Predicate query, Pageable paging) not implemented for "+this.getClass().getName());
	}
	*/
	
	public void save(T item)
	{
		//log.debug("saving entity: "+StringHelper.toString(item));
		repository.save(item);
	}
	
	public void saveAll(Iterable<T> items)
	{
		//log.debug("saving entity: "+StringHelper.toString(item));
		repository.saveAll(items);
	}
	
	// alias for saveAll but can be overriden to implement more efficient inserts
	public void insertAll(Iterable<T> items)
	{
		saveAll(items);
	}
	
	public void delete(Integer id)
	{
		repository.delete(id);
	}
	
	public void deleteAll()
	{
		repository.deleteAll();
	}
	
	public void flush()
	{
		repository.flush();
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
		T entity=findOne(item.getId(), true);
		//log.debug("updating: "+item.toString());
		copyProperties(entity,item);
		return (T)repository.save(entity);
	}
	
	protected boolean setProperty(T entity, String property, Object value)
	{
		return BeanHelper.setProperty(entity, property, value);
	}
	
	public List<T> update(List<T> items)
	{
		for (T item : items)
		{
			update(item);
		}
		return items;
	}
	
	public void delete(List<T> items)
	{
		for (T item : items)
		{
			delete(item);
		}
	}
	
	public void delete(T item)
	{
		//try
		//{
			repository.delete(item);
//		}
//		catch (Throwable e)
//		{
//			new CException(e.getMessage(),e);
//		}
	}
	
	////////////////////////////////////////////////////////
	
	protected boolean copyProperties(T dest, Object src)
	{
		return beanhelper.copyProperties(dest, src, ignore);
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
	
	protected static void doNothing(){}
}
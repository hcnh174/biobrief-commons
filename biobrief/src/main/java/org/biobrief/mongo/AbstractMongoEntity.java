package org.biobrief.mongo;

import java.util.Date;
import java.util.List;

import org.biobrief.util.AbstractEntity;
import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.Constants.FirstLast;
import org.biobrief.util.StringHelper;
//import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//https://stackoverflow.com/questions/35584271/spring-data-mongodb-annotation-createddate-isnt-working-when-id-is-assigned-m
//https://medium.com/@manika09singh/enable-auditing-using-spring-data-jpa-2f62587ccb23
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class AbstractMongoEntity extends AbstractEntity<String>
	implements MongoDocument//, Persistable<String>
{	
	@Id
	protected String id;
	
	//@Version
	//private Long version;
	
	//@CreatedBy //@JsonIgnore
	protected String createdBy;
	
	//@CreatedDate //@JsonIgnore
	protected Date createdDate;
	
	//@LastModifiedBy //@JsonIgnore
	protected String lastModifiedBy;
	
	//@LastModifiedDate //@JsonIgnore
	protected Date lastModifiedDate;
	
	public AbstractMongoEntity() {}
	
	public AbstractMongoEntity(String id)
	{
		this.id=id;
		this.createdDate=new Date();
		this.lastModifiedDate=new Date();
	}
	
	@Override public String getId(){return this.id;}
	
	public String getCreatedBy(){return this.createdBy;}
	public void setCreatedBy(final String createdBy){this.createdBy=createdBy;}

	public Date getCreatedDate(){return this.createdDate;}
	public void setCreatedDate(final Date createdDate){this.createdDate=createdDate;}

	public String getLastModifiedBy(){return this.lastModifiedBy;}
	public void setLastModifiedBy(final String lastModifiedBy){this.lastModifiedBy=lastModifiedBy;}

	public Date getLastModifiedDate(){return this.lastModifiedDate;}
	public void setLastModifiedDate(final Date lastModifiedDate){this.lastModifiedDate=lastModifiedDate;}
	
//	@Override
//	public boolean isNew()
//	{
//		return id==null;
//	}
	
	///////////////////////////////////////////////////////////////
	
	public NestedMongoDocument getNested(Class<?> cls)
	{
		throw new CException("no handler for nested type: "+cls);
	}
	
	public List<? extends MongoItem> getCollection(Class<?> cls)
	{
		throw new CException("no handler for collection type: "+cls);
	}
	
	////////////////////////////////////
	
	public MongoItem getFirstLast(Class<?> cls, FirstLast firstlast)//EntityDefinition entityType
	{
		return StringHelper.getFirstLast(getCollection(cls), firstlast);
	}
	
	public MongoItem getFirstLast(List<? extends MongoItem> collection, FirstLast firstlast)
	{
		return StringHelper.getFirstLast(collection, firstlast);
	}
	
	public MongoItem getFirst(List<? extends MongoItem> collection)
	{
		return StringHelper.getFirst(collection);
	}
	
	public MongoItem getLast(List<? extends MongoItem> collection)
	{
		return StringHelper.getLast(collection);
	}
	
	//////////////////////////////////////////////////
	
	public MongoItem getFirst(Class<?> cls)
	{
		return getFirst(getCollection(cls));
	}
	
	public MongoItem getLast(Class<?> cls)
	{
		return getLast(getCollection(cls));
	}
	
	//////////////////////////////////////////////////////////////////////
	
	public MongoItem findItem(Class<?> cls, String id, boolean strict)
	{
		List<? extends MongoItem> collection=getCollection(cls);
		return findItem(collection, id, strict);
	}

	protected MongoItem findItem(List<? extends MongoItem> collection, String id, boolean strict)
	{
		//System.out.println("findItem: id="+id);
		if (!StringHelper.hasContent(id))
		{
			if (strict)
				throw new CException("id for item is null");
			return null;
		}
		
		for (MongoItem item : collection)
		{
			//System.out.println("item: "+StringHelper.toString(item));
			if (item.getId().equals(id))
				return item;
		}
		if (strict)
			throw new CException("cannot find embedded item with id: "+id);
		return null;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void addItem(Class<?> cls, MongoItem item)
	{
		List collection=getCollection(cls);
		collection.add(item);
	}
	
	public void removeItem(Class<?> cls, String id)
	{
		if (!StringHelper.hasContent(id))
			return;
		List<? extends MongoItem> collection=getCollection(cls);
		MongoItem item=findItem(collection, id, false);
		collection.remove(item);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public MongoItem findOrCreateItem(Class<?> cls, String id)
	{
		List collection=getCollection(cls);
		MongoItem item=findItem(collection, id, false);
		if (item!=null)
			return item;
		return createItem(cls, id);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public MongoItem createItem(Class<?> cls, String id)
	{
		List collection=getCollection(cls);
		//MongoItem item=(MongoItem)BeanHelper.newInstance(cls);
		MongoItem item=(MongoItem)BeanHelper.instantiateClass(cls);
		item.init();
		collection.add(item);
		return item;
	}
}

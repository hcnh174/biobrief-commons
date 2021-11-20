package org.biobrief.dictionary;

import java.util.Collection;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.Constants.PersistenceType;

import com.google.common.collect.Maps;

public class GroupDefinition
{
	private final Dictionary dictionary;
	private final String name;
	private final String label;
	private final PersistenceType persistenceType;
	private final String identifier; // unique key of root element (dbno, nafldNo
	private final Boolean escape;
	private final String index;
	private final Map<String, EntityDefinition> entities=Maps.newLinkedHashMap();
	
	public GroupDefinition(Dictionary dictionary, Map<String, String> map)
	{
		this.dictionary=dictionary;
		this.name=map.get("name");
		this.label=DictUtil.dfltIfEmpty(map.get("label"), this.name);
		this.persistenceType=PersistenceType.valueOf(DictUtil.dfltIfEmpty(map.get("persistenceType"), "mongo"));
		this.identifier=DictUtil.dfltIfEmpty(map.get("identifier"), "id");
		this.index=DictUtil.nullIfEmpty(map.get("index"));
		this.escape=DictUtil.asBoolean(map.get("escape"), true);	
	}
	
	public void add(EntityDefinition type)
	{
		entities.put(type.getName(), type);
	}
	
	public EntityDefinition getEntity(String name)
	{
		if (!entities.containsKey(name))
			throw new CException("cannot find entity definition of type: "+name+" in group "+this.name);
		return entities.get(name);
	}

	public EntityDefinition createDynamicEntity(String name)
	{
		System.out.println("creating dynamic entity in group "+this.name+": "+name);
		return new DynamicEntityDefinition(this, name);
	}
	
	public Dictionary getDictionary(){return this.dictionary;}
	public String getName(){return this.name;}
	public String getlabel(){return this.label;}
	public PersistenceType getPersistenceType(){return this.persistenceType;}
	public String getIdentifier(){return this.identifier;}
	public String getIndex(){return this.index;}
	public Boolean getEscape(){return this.escape;}
	public Collection<EntityDefinition> getEntities(){return entities.values();}
}

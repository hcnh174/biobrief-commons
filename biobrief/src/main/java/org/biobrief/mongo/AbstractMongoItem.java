package org.biobrief.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class AbstractMongoItem implements MongoItem
{	
	public abstract String getId();
	
	public void init() {}
}

package org.biobrief.util;

public abstract class AbstractEntity<K>
{
	public abstract K getId();
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
}

package org.biobrief.util;

import org.biobrief.util.StringHelper;

public abstract class AbstractEntity<K>
{
	public abstract K getId();
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
}

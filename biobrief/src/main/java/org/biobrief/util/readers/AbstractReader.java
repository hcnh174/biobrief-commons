package org.biobrief.util.readers;

import java.util.List;

import org.biobrief.util.MessageWriter;

@SuppressWarnings("rawtypes")
public abstract class AbstractReader<T>
{
	protected final Class cls;
	protected final MessageWriter out;
	
	public AbstractReader(Class cls, MessageWriter out)
	{
		this.cls=cls;
		this.out=out;
	}
	
	public abstract List<T> readFile(String filename);
}
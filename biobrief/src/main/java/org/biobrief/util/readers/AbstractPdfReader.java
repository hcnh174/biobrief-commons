package org.biobrief.util.readers;

import org.biobrief.util.MessageWriter;

@SuppressWarnings("rawtypes")
public abstract class AbstractPdfReader<T> extends AbstractReader<T>
{
	public AbstractPdfReader(Class cls, MessageWriter out)
	{
		super(cls, out);
	}
}
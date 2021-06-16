package org.biobrief.util.readers;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.LogUtil;

@SuppressWarnings("rawtypes")
public abstract class AbstractTsvReader<T> extends AbstractReader<T>
{
	protected final String encoding;
	
	public AbstractTsvReader(Class cls, String encoding, MessageWriter out)
	{
		super(cls, out);
		this.encoding=encoding;
	}
	
	public abstract List<T> readFile(String filename);
	
	protected Reader createReader(String filename)
	{
		try
		{
			return new InputStreamReader(new FileInputStream(filename), encoding);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	protected void logReadTsvFile(String filename, Exception e)
	{
		String message=e.getMessage()+" (filename="+filename+")";
		LogUtil.logMessage("tsv-errors.txt", message);
	}
}
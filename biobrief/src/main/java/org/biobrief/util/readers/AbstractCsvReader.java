package org.biobrief.util.readers;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.LogUtil;

////https://code.google.com/archive/p/jcsv/wikis/Welcome.wiki
//https://commons.apache.org/proper/commons-csv/
//http://demeranville.com/how-not-to-parse-csv-using-java/
//https://github.com/FasterXML/jackson-dataformats-text
//https://github.com/uniVocity/csv-parsers-comparison
@SuppressWarnings("rawtypes")
public abstract class AbstractCsvReader<T> extends AbstractReader<T>
{
	protected final String encoding;
	
	public AbstractCsvReader(Class cls, String encoding, MessageWriter out)
	{
		super(cls, out);
		this.encoding=encoding;
	}
	
	public abstract List<T> readFile(String filename);
	
	protected Reader createReader(String filename)
	{
		try
		{
			return new InputStreamReader(new FileInputStream(filename), encoding);//StringHelper.SHIFT_JIS
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	protected void logReadCsvFile(String filename, Exception e)
	{
		String message=e.getMessage()+" (filename="+filename+")";
		LogUtil.logMessage("csv-errors.txt", message);
	}
}
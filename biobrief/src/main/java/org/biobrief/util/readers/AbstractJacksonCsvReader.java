package org.biobrief.util.readers;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Lists;

import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;

//https://github.com/FasterXML/jackson-dataformats-text/tree/master/csv
//http://demeranville.com/how-not-to-parse-csv-using-java/
//https://github.com/FasterXML/jackson-dataformats-text
//http://www.cowtowncoder.com/blog/archives/2012/03/entry_468.html
@SuppressWarnings("rawtypes")
public abstract class AbstractJacksonCsvReader<T> extends AbstractCsvReader<T>
{	
	public AbstractJacksonCsvReader(Class cls, MessageWriter out)
	{
		super(cls, StringHelper.SHIFT_JIS, out);
	}
	
	@Override
	public List<T> readFile(String filename)
	{
		if (!FileHelper.isCsv(filename))
			throw new CException("expected file ending in .csv or .csvj: "+filename);
		try
		{
			List<T> list=Lists.newArrayList();
			CsvSchema schema=createSchema();
			CsvMapper mapper = new CsvMapper();
			MappingIterator<Map<String,String>> it=mapper.readerFor(Map.class)
			   .with(schema).readValues(createReader(filename));
			while (it.hasNext())
			{
				Map<String,String> row=it.next();
				handleRow(row, list);
			}
			return list;
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	protected abstract void handleRow(Map<String,String> row, List<T> list);
	
	protected CsvSchema createSchema()
	{
		return CsvSchema.emptySchema().withHeader(); // use first row as header
	}
}
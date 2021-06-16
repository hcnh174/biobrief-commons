package org.biobrief.util.readers;

import java.util.List;
import java.util.Map;

import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Lists;

//https://github.com/FasterXML/jackson-dataformats-text/tree/master/csv
@SuppressWarnings("rawtypes")
public abstract class AbstractJacksonTsvReader<T> extends AbstractCsvReader<T>
{	
	public AbstractJacksonTsvReader(Class cls, String encoding, MessageWriter out)
	{
		super(cls, encoding, out);
	}
	
	@Override
	public List<T> readFile(String filename)
	{
		if (!FileHelper.isTsv(filename))
			throw new CException("expected file ending in .tsv: "+filename);
		out.println("reading file: "+filename);
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
		return CsvSchema.emptySchema().withHeader().withColumnSeparator('\t'); // use first row as header
	}
}
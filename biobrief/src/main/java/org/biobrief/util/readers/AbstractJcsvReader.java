package org.biobrief.util.readers;

import java.io.Reader;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.biobrief.util.DateHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.annotations.ValueProcessor;
import com.googlecode.jcsv.annotations.internal.ValueProcessorProvider;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.AnnotationEntryParser;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

////https://code.google.com/archive/p/jcsv/wikis/Welcome.wiki
//https://code.google.com/archive/p/jcsv/wikis/CSVStrategy.wiki
//https://github.com/va7map/jcsv/releases
//https://mydevgeek.com/csv-file-reading-writing-java-using-google-jcsv/
@SuppressWarnings("rawtypes")
public abstract class AbstractJcsvReader<T> extends AbstractCsvReader<T>
{
	private final CSVStrategy strategy;
	
	public AbstractJcsvReader(Class cls, CSVStrategy strategy, String encoding, MessageWriter out)
	{
		super(cls, encoding, out);
		this.strategy=strategy;
	}

	public AbstractJcsvReader(Class cls, MessageWriter out)
	{
		this(cls, new CSVStrategy(',', '"', '#', true, true), StringHelper.SHIFT_JIS, out);//CSVStrategy.UK_DEFAULT);
	}
	
	@Override
	public List<T> readFile(String filename)
	{
		//checkSuffix(filename);
		List<T> list=Lists.newArrayList();
		CSVReader<T> csvReader=createCsvReader(createReader(filename));
		Iterator<T> iter=csvReader.iterator();
		while (iter.hasNext())
		{
			try
			{
				T item=iter.next();
				list.add(item);
			}
			catch (Exception e)
			{
				logReadCsvFile(filename, e);
			}
		}
		return list;
	}
	
//	protected void checkSuffix(String filename)
//	{
//		if (!FileHelper.isCsv(filename))
//			throw new CException("expected file ending in .csv or .csvj: "+filename);
//	}
	
	@SuppressWarnings("unchecked")
	protected CSVReader<T> createCsvReader(Reader reader)
	{
		ValueProcessorProvider provider=createValueProcessorProvider();
		CSVEntryParser<T> entryParser=new AnnotationEntryParser<T>(cls, provider);
		CSVReader<T> csvReader = new CSVReaderBuilder<T>(reader).strategy(strategy).entryParser(entryParser).build();
		return csvReader;
	}
	
	protected ValueProcessorProvider createValueProcessorProvider()
	{
		ValueProcessorProvider provider=new ValueProcessorProvider();
		
		provider.removeValueProcessor(Date.class);
		provider.registerValueProcessor(Date.class, new CustomDateProcessor(DateHelper.YYYYMMDD_PATTERN));
		
		provider.removeValueProcessor(Float.class);
		provider.registerValueProcessor(Float.class, new CustomFloatProcessor());
		
		provider.removeValueProcessor(Integer.class);
		provider.registerValueProcessor(Integer.class, new CustomIntegerProcessor());
		
		return provider;
	}
	
	public static class CustomDateProcessor implements ValueProcessor<Date>
	{
		private final List<String> patterns;

		public CustomDateProcessor(List<String> patterns)
		{
			this.patterns=patterns;
		}
		
		public CustomDateProcessor(String pattern)
		{
			this(Lists.newArrayList(pattern));
		}

		@Override
		public Date processValue(String value)
		{
			return DateHelper.parse(value, patterns, false);
		}
	}
	
	public static class CustomFloatProcessor implements ValueProcessor<Float>
	{
		@Override
		public Float processValue(String value)
		{
			if (!StringHelper.hasContent(value))
				return null;
			return Float.parseFloat(value);
		}
	}
	
	public static class CustomIntegerProcessor implements ValueProcessor<Integer>
	{
		@Override
		public Integer processValue(String value)
		{
			if (!StringHelper.hasContent(value))
				return null;
			return Integer.parseInt(value);
		}
	}
}
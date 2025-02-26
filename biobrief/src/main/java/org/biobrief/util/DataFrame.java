package org.biobrief.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.io.LineReader;

//similar to a CTable but more geared to identifying cells using a double hash
// tables are row-based, dataframes are more column-based
public class DataFrame<T extends Object>
{
	private static final Logger logger=LoggerFactory.getLogger(DataFrame.class);
	
	protected Map<String,Column> columns=Maps.newLinkedHashMap();
	protected Set<T> rownames=Sets.newLinkedHashSet();
	//protected List<T> rownames=Lists.newArrayList();
	protected Options options=new Options();
	protected BeanHelper beanhelper=new BeanHelper();
	
	public DataFrame(){}
	
	public DataFrame(Options options)
	{
		this.options=options;
	}
	
	public DataFrame(boolean autoAddColumns)
	{
		options.autoAddColumns=autoAddColumns;
	}
	
	public Options getOptions(){return options;}
	
	public Column findOrCreateColumn(String colname)
	{
		Column column=getColumn(colname);
		if (column==null)
			column=addColumn(colname);
		return column;
	}
	
	public Column addColumn(String colname)
	{
		//logger.debug("adding column ["+colname+"]");
		if (this.columns.containsKey(colname))
			return this.columns.get(colname);
		Column column=new Column(this,colname);
		this.columns.put(column.getName(),column);
		return column;
	}

	public void addColumns(Collection<String> colnames)
	{
		for (String colname : colnames)
		{
			addColumn(colname);
		}
	}
	
	public Object getValue(String colname, T rowname)
	{
		Column column=this.columns.get(colname);
		if (column==null)
			throw new CException("column is null: "+colname);
		return column.getValue(rowname);
	}
	
	public Object getValue(String colname, T rowname, Object dflt)
	{
		Column column=this.columns.get(colname);
		if (column==null)
			return dflt;
		Object value=column.getValue(rowname);
		if (!StringHelper.hasContent(value))
			return dflt;
		return value;
	}
	
	public void setValue(String colname, T rowname, Object value)
	{
		//System.out.println("dataframe.setValue: colname="+colname+", rowname="+rowname+", value="+value);
		Column column=getColumn(colname);
		column.setValue(rowname, value);
	}
	
	public void appendValue(String colname, T rowname, Object value)
	{
		Column column=getColumn(colname);
		column.appendValue(rowname, value);
	}
	
	public void setValue(String colname, T rowname, char value)
	{
		Column column=getColumn(colname);
		column.setValue(rowname, value);
	}
	
	public boolean hasValue(String colname, T rowname)
	{
		Object value=getValue(colname, rowname);
		return (value!=null);
	}
	
	public String getStringValue(String colname, T rowname)
	{
		Object value=getValue(colname, rowname);
		if (value==null)
			return null;
		return value.toString();
	}
	
	// if the column does not exist or the value is null, return the default value 	
	public String getStringValue(String colname, T rowname, String dflt)
	{
		if (!hasColumn(colname))
			return dflt;
		return StringHelper.dflt(getStringValue(colname, rowname), dflt);
	}
	
	public Integer getIntValue(String colname, T rowname)
	{
		Object value=getValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return Integer.valueOf(value.toString());
	}
	
	public Long getLongValue(String colname, T rowname)
	{
		Object value=getValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return Long.valueOf(value.toString());
	}
	
	public Float getFloatValue(String colname, T rowname)
	{
		Object value=getValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return Float.valueOf(value.toString());
	}
	
	public Double getDoubleValue(String colname, T rowname)
	{
		Object value=getValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return Double.valueOf(value.toString());
	}
	
	public Boolean getBooleanValue(String colname, T rowname)
	{
		Object value=getValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return Boolean.parseBoolean(value.toString());
	}
	
	public Date getDateValue(String colname, T rowname, String pattern)
	{
		String value=getStringValue(colname,rowname);
		if (!StringHelper.hasContent(value))
			return null;
		return DateHelper.parse(value, pattern);
	}
	
	public Column getColumn(String colname)
	{
		if (!this.columns.containsKey(colname))
		{
			if (!options.autoAddColumns)
				throw new CException("can't find column named "+colname);
			else return addColumn(colname);
		}
		return this.columns.get(colname);
	}	
	
	public Collection<Column> getColumns()
	{
		return this.columns.values();
	}
	
	public int getNumRows()
	{
		return this.rownames.size();
	}
	
	public int getNumCols()
	{
		return this.columns.size();
	}
	
	public CTable getTable()
	{
		return getTable(false);
	}
	
	public CTable getTable(boolean rownames)
	{
		CTable table=new CTable();
		if (rownames)
			table.getHeader().add("id");
		for (String colname : getColNames())
		{
			table.getHeader().add(colname);
		}
		for (T rowname : getRowNames())
		{
			CTable.Row row=table.addRow();
			if (rownames)
				row.add(rowname);
			for (String colname : getColNames())
			{
				Column column=getColumn(colname);
				Object value=column.getValue(rowname);
				row.add(value);
			}
		}
		return table;
	}
	
	public Map<String,Object> getRow(T rowname)
	{
		Map<String,Object> map=Maps.newLinkedHashMap();
		for (String colname : getColNames())
		{
			Column column=getColumn(colname);
			Object value=column.getValue(rowname);
			map.put(colname, value);
		}
		return map;
	}
	
	public Map<String, String> getRowAsStrings(T rowname)
	{
		Map<String, String> map=Maps.newLinkedHashMap();
		for (String colname : getColNames())
		{
			map.put(colname, getStringValue(colname, rowname));
		}
		return map;
	}

	public String toString()
	{
		return getTable(true).toString();
	}
	
	public List<String> getColNames()
	{
		List<String> colnames=Lists.newArrayList();
		colnames.addAll(this.columns.keySet());
		return colnames;
	}
	
	public List<String> getPropertyNames(Object obj)
	{
		List<String> colnames=Lists.newArrayList();
		for (String colname : this.columns.keySet())
		{
			if (BeanHelper.isReadable(obj, colname))
				colnames.add(colname);
		}
		return colnames;
	}
	
	public List<String> getColNamesExcept(String...args)
	{
		List<String> skip=Lists.newArrayList(args);
		return getColNamesExcept(skip);
	}
	
	// gets all columns names unless specified
	public List<String> getColNamesExcept(List<String> skip)
	{
		List<String> colnames=Lists.newArrayList();
		for (String colname : this.columns.keySet())
		{
			if (!skip.contains(colname))
				colnames.add(colname);
		}
		return colnames;
	}
	
	public boolean hasRow(T rowname)
	{
		return this.rownames.contains(rowname);
	}
	
	public boolean hasColumn(String colname)
	{
		return this.columns.containsKey(colname);
	}
	
	public void registerRowName(T rowname)
	{
		addRow(rowname);
	}
	
	public void addRows(Collection<T> rownames)
	{
		for (T rowname : rownames)
		{
			addRow(rowname);
		}
	}
	
	public void addRow(T rowname)
	{
		if (!hasRow(rowname))
			this.rownames.add(rowname);
	}
	
	public void deleteRow(T rowname)
	{
		if (hasRow(rowname))
		{
			//System.out.println("deleting row: "+rowname);
			this.rownames.remove(rowname);
			for (Column column : this.columns.values())
			{
				column.deleteValue(rowname);
			}
		}
	}
	
	public List<T> getRowNames()
	{
		return Lists.newArrayList(rownames);
	}
	
	public Collection<String> getStringRowNames()
	{
		return Collections2.transform(getRowNames(), new Function<Object, String>()
		{
			@Override
			public String apply(Object obj)
			{
				return obj.toString();
			}
		});
	}
	
	public int size()
	{
		Column column=this.columns.values().iterator().next();
		return column.size();
	}
	
	public boolean isEmpty()
	{
		return this.columns.isEmpty();
	}

	public void appendColumns(DataFrame<T> other)
	{
		for (String colname : other.getColNames())
		{
			if (hasColumn(colname))
			{
				System.err.println("dataframe already has column "+colname+". skipping.");
				continue;
			}
			addColumn(colname);
			Column column=other.getColumn(colname);
			for (T rowname : column.getRowNames())
			{
				Object value=column.getValue(rowname);
				if (hasRow(rowname))
					setValue(colname,rowname,value);
			}
		}
	}
	
	public void appendForeignColumns(String fkey_colname, DataFrame<T> other)
	{
		for (String other_colname : other.getColNames())
		{
			String colname=fkey_colname+"_"+other_colname;
			if (hasColumn(colname))
			{
				System.err.println("dataframe already has column "+colname+". skipping.");
				continue;
			}
			addColumn(colname);
			Column column=other.getColumn(other_colname);
			for (T other_rowname : column.getRowNames())
			{
				Object value=column.getValue(other_rowname);
				for (T rowname : this.getRownamesByColValue(fkey_colname, other_rowname))
				{
					//logger.debug("trying to setValue(colname="+colname+", rowname="+rowname+", value="+value);
					setValue(colname,rowname,value);
				}
			}
		}
	}

	public Collection<Object> getUniqueValues(String colname)
	{
		Column column=getColumn(colname);
		return column.getUniqueValues();
	}
	
	public Collection<T> getRownamesByColValue(String colname, Object value)
	{
		Column column=getColumn(colname);
		return column.getRownamesByColValue(value); 
	}
	
	@JsonProperty
	public Integer getTotalCount()
	{
		return rownames.size();
	}
	
	@JsonProperty
	public List<Row> getRows()
	{
		return getRows(true);
	}	
	
	public List<Row> getRows(boolean addrowname)
	{
		List<Row> rows=Lists.newArrayList();
		for (T rowname : getRowNames())
		{
			Row row=new Row();
			if (addrowname)
				row.put("rowname", rowname);
			for (String colname : getColNames())
			{
				Object value=getValue(colname,rowname);
				row.put(colname, value);
			}
			rows.add(row);
		}
		return rows;
	}
	
	public void setProperties(Object obj, T rowname)
	{
		for (String property : getColNames())
		{
			if (options.isKeyField(property))
				continue;
			Object value=getValue(property,rowname);
			if (value==null)
				continue;
			beanhelper.setPropertyFromString(obj, property, value.toString());
			logger.debug("dataframe.setProperties["+rowname+"]: "+property+"="+value);
		}
	}
	
	@SuppressWarnings("serial")
	public class Row extends LinkedHashMap<String,Object>
	{
		
	}
	
	public class Column
	{
		protected DataFrame<T> dataframe;
		protected String colname;
		protected Map<T,Object> values=Maps.newLinkedHashMap();
		
		public Column(DataFrame<T> dataframe, String colname)
		{
			this.dataframe=dataframe;
			this.colname=colname;
		}
		
		public String getName(){return this.colname;}
		
		public Set<T> getKeys()
		{
			return this.values.keySet();
		}
		
		public Object getValue(T rowname)
		{
			return this.values.get(rowname);
		}
		
		public void setValue(T rowname, Object value)
		{
			dataframe.registerRowName(rowname);
			this.values.put(rowname,value);
		}
		
		public void appendValue(T rowname, Object value)
		{
			Object oldvalue=this.values.get(rowname);
			if (oldvalue==null)
				this.setValue(rowname, value);
			else this.setValue(rowname, oldvalue.toString()+"\n"+value);
		}
		
//		public void appendValue(T rowname, Object value)
//		{
//			Object oldvalue=StringHelper.dflt(this.values.get(rowname));
//			if (StringHelper.hasContent(oldvalue))
//				oldvalue=oldvalue.toString()+"\n";
//			Object newvalue=oldvalue.toString()+value;
//			this.setValue(rowname, newvalue);
//		}
		
		public void deleteValue(T rowname)
		{
			this.values.remove(rowname);
		}
		
		public Collection<T> getRowNames()
		{
			return this.values.keySet();
		}
		
		public Collection<Object> getValues()
		{
			return this.values.values();
		}
		
		public int size()
		{
			return this.values.size();
		}
		
		public Collection<Object> getUniqueValues()
		{
			Set<Object> uniquevalues=Sets.newLinkedHashSet();
			for (Object value : values.values())
			{
				if (!uniquevalues.contains(value))
					uniquevalues.add(value);
			}
			return uniquevalues;
		}

		public Collection<T> getRownamesByColValue(Object val)
		{
			Set<T> rownames=Sets.newLinkedHashSet();
			for (T rowname : values.keySet())
			{
				Object value=values.get(rowname);
				logger.debug("if "+value+".equals("+val+"): "+value.equals(val));
				if (value.equals(val))
					rownames.add(rowname);
			}
			return rownames;
		}
	}

	public interface IntervalListener
	{
		boolean onInterval(Parser parser, int rownum);
	}
	
	public abstract static class Parser
	{
		protected Options options;
		protected int interval=10;
		protected List<IntervalListener> listeners=Lists.newArrayList();
		protected BiMap<Integer,String> headerfields=HashBiMap.create();
		protected StringDataFrame dataframe;
		protected Map<Integer,StringDataFrame.Column> columns;
		
		public Parser(){}
		
		public Parser(Options options)
		{
			this.options=options;
		}
		
		public int getInterval(){return this.interval;}
		public void setInterval(final int interval){this.interval=interval;}
		
		public void addIntervalListener(IntervalListener listener)
		{
			listeners.add(listener);
		}
		
		public StringDataFrame getDataFrame(){return dataframe;}
		
		protected boolean readHeader(List<String> fields)
		{
			fields=preProcessHeader(fields);
			for (int index=0;index<fields.size();index++)
			{
				String colname=fields.get(index).trim();
				if (!StringHelper.hasContent(colname))
				{
					//LogUtil.log("colname "+index+" is blank: "+StringHelper.join(fields));
					//throw new CException("colname "+index+" is blank: "+StringHelper.join(fields));
					colname="col"+index;
				}
				if (headerfields.containsValue(colname))
					throw new CException("duplicate colname for column "+index+"="+colname+" : "+StringHelper.join(fields));
				headerfields.put(index, colname);
			}
			postProcessHeader(fields);
			setupRownames();
			//logger.debug("idcols="+options.idcols);
			resetDataFrame();
			return true;
		}
		
		protected void setupRownames()
		{
			//logger.debug("setupRownames");
			if (options.idcols!=null)
				return;
			if (options.idnames==null)
			{
				options.idcols=Lists.newArrayList(0);
				return;
			}
			options.idcols=Lists.newArrayList();
			for (String idname : options.idnames)
			{
				Integer idcol=headerfields.inverse().get(idname);
				options.idcols.add(idcol);
			}
			//logger.debug("idcols="+options.idcols);
		}
		
		public void resetDataFrame()
		{
			//dataframe=new DataFrame<String>(options);
			dataframe=new StringDataFrame(options);
			columns=Maps.newLinkedHashMap();
			for (Integer index : headerfields.keySet())
			{
				String colname=headerfields.get(index);
				StringDataFrame.Column column=this.dataframe.addColumn(colname);
				this.columns.put(index,column);
			}
		}
		
		protected boolean readLine(List<String> values, int rownum)
		{
			values=preProcessLine(values,rownum);
			if (values.size()<columns.size())
				throw new CException("numbers of fields in row is less than the number of colnums: values="+values.size()+", columns="+columns.size()+" in row "+rownum);
			String rowname=options.getRowname(values);
			//System.out.println("rowname="+rowname);
			this.dataframe.addRow(rowname);
			for (int index=0; index<this.columns.size(); index++)//values.size()
			{
				StringDataFrame.Column column=this.columns.get(index);
				String value=values.get(index).trim();
				if (options.stripQuotes)
					value=StringHelper.unquote(value);
				//System.out.println("setting col="+column.getName()+", row="+rownum+", value="+value);
				column.values.put(rowname, value);
			}
			postProcessLine(values, rownum);
			return true;
		}
		
		protected boolean notifyListeners(int rownum)
		{
			if (rownum%interval!=0)
				return true;
			boolean proceed=true;
			for (IntervalListener listener : listeners)
			{
				if (!listener.onInterval(this, rownum))
					proceed=false;
			}
			return proceed;
		}
		
		protected List<String> preProcessHeader(List<String> fields){return fields;}
		protected void postProcessHeader(List<String> fields){}
		
		protected List<String> preProcessLine(List<String> values, int rownum)
		{
			return values;
		}
	
		protected List<String> splitLine(String line)
		{
			return StringHelper.split(line, "\t");
		}
		
		protected void postProcessLine(List<String> values, int rownum)
		{
			if (rownum % options.ROW_STATUS_INTERVAL==0)
				logger.debug("reading line "+rownum);
		}
		
		protected boolean isComment(String line)
		{
			return line.trim().equals("") || line.startsWith(options.comment);
		}
	}
	
	public static class TabFileParser extends Parser
	{	
		public TabFileParser(){}
		
		public TabFileParser(Options options)
		{
			super(options);
		}
		
		public void parseFile(String filename)
		{
			try		
			{
				FileHelper.checkExists(filename);
				if (!StringHelper.hasContent(Files.asCharSource(new File(filename), this.options.encoding).readFirstLine()))
					throw new CException("file is empty: "+filename);
				Files.asCharSource(new File(filename), this.options.encoding).readLines(new LineProcessor<String>()
				{
					private int rownum=0;
					
					public boolean processLine(String line)
					{
						//System.out.println("line: "+line);
						if (rownum==0)
							readHeader(splitLine(line.trim()));
						else if (isComment(line))
						{
							//System.out.println("skipping comment line: "+line);
							return true;// check - was false
						}
						else readLine(splitLine(line), rownum);
						if (!notifyListeners(rownum))
							return true; // check - was false
						rownum++;
						return true;
					}
					
					public String getResult(){return null;}
				});	
			}
			catch (IOException e)
			{
				throw new CException(e);
			}
		}

		public void parse(String str)
		{
			try
			{
				if (!StringHelper.hasContent(str))
					throw new CException("cannot parse text: "+str);
				LineReader reader=new LineReader(new StringReader(str));
				String line;
				int rownum=0;
				while ((line=reader.readLine())!=null)
				{
					logger.debug("reading line: "+line);
					if (rownum==0)
						readHeader(splitLine(line.trim()));
					else if (isComment(line))
						continue;
					else readLine(splitLine(line),rownum);
					rownum++;
				}
			}
			catch (IOException e)
			{
				throw new CException(e);
			}
		}
	}
	
	public static StringDataFrame parseTabFile(String filename)
	{
		return parseTabFile(filename, new Options());
	}
	
	public static StringDataFrame parseTabFile(String filename, Options options)
	{
		try
		{
			TabFileParser parser=new TabFileParser(options);
			parser.parseFile(filename);
			return parser.getDataFrame();
		}
		catch(Exception e)
		{
			throw new CException("cannot parse file :"+filename, e);
		}
	}
	
	public static StringDataFrame parse(String str)
	{
		return parse(str,new Options());
	}
	
	public static StringDataFrame parse(String str, Options options)
	{
		TabFileParser parser=new TabFileParser(options);
		parser.parse(str);
		return parser.getDataFrame();
	}
	
	public static class Options
	{
		public enum IdType {ALL, LIST}
		
		public int ROW_STATUS_INTERVAL=100000;
		public IdType idType=IdType.LIST;
		public Charset encoding=StandardCharsets.UTF_8;
		public List<Integer> idcols;
		public List<String> idnames;
		public String keyDelimiter="_";
		public String comment="#";
		public boolean autoAddColumns=false;
		public boolean stripQuotes=true;
		
		public Options(){}
		
		public Options(IdType idType)
		{
			this.idType=idType;
		}
		
		public Options(Charset encoding)
		{
			this.encoding=encoding;
		}		
		
		public Options(String...colnames)
		{
			if (colnames.length==1)
				this.idnames=StringHelper.split(colnames[0]);
			else this.idnames=Lists.newArrayList(colnames);
		}
		
		public Options(Integer...colnums)
		{
			this.idcols=Lists.newArrayList(colnums);
		}
		
		public boolean isKeyField(String colname)
		{
			if (idnames==null)
				return false;
			return idnames.contains(colname);
		}
		
		public String getRowname(String... values)
		{
			return getRowname(Lists.newArrayList(values));
		}
		
		// if the idtype is ALL, just append all the values as the rowname		
		public String getRowname(List<String> values)
		{
			List<String> list=Lists.newArrayList();
			if (idType==Options.IdType.ALL)
				return StringHelper.join(values, keyDelimiter);
			for (Integer idcol : idcols)
			{
				list.add(values.get(idcol).trim());
			}
			return StringHelper.join(list, keyDelimiter);
		}
	}
	
	public static class StringDataFrame extends DataFrame<String>
	{
		public StringDataFrame(){}
		
		public StringDataFrame(Options options)
		{
			super(options);
		}
		
		public StringDataFrame(boolean autoAddColumns)
		{
			super(autoAddColumns);
		}
	}
	
	public static class IntegerDataFrame extends DataFrame<Integer>
	{
		public IntegerDataFrame(){}
		
		public IntegerDataFrame(Options options)
		{
			super(options);
		}
		
		public IntegerDataFrame(boolean autoAddColumns)
		{
			super(autoAddColumns);
		}
	}
}
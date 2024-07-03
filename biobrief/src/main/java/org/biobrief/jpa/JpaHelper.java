package org.biobrief.jpa;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.DataFrame.IntegerDataFrame;
import org.biobrief.util.DataType;
import org.biobrief.util.FileHelper;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.LocalDateHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class JpaHelper
{	
	private static final Logger logger=LoggerFactory.getLogger(JpaHelper.class);
	
	private static final String SQL_DIR="src/main/sql/";
	
	public static final String NULL="NULL";
	
	private JpaHelper(){}

	public static String stripSqlComments(String sql)
	{
		return StringHelper.stripSqlComments(sql);
	}
	
	public static DataSource createTestDataSource()
	{
		//https://stackoverflow.com/questions/1336885/how-do-i-manually-configure-a-datasource-in-java
		//https://examples.javacodegeeks.com/enterprise-java/spring/jdbc/create-data-source-for-jdbctemplate/
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/biobrief");
		dataSource.setUsername("biobrief_admin");
		dataSource.setPassword("biobrief_admin");
		return dataSource;
	}

	public static void createSetupFile(String folder, String setupfile, boolean overwrite)
	{
		//String setupfile=folder+"/setup.sql";
		if (FileHelper.exists(setupfile) && !overwrite)
			return;
		//logger.debug("creating database setup file: "+setupfile);
		List<String> filenames=getSqlFilenames(folder);
		String str=concatenateScripts(filenames);
		FileHelper.writeFile(setupfile,str);
	}
	
	public static List<String> getSqlFilenames(String folder)
	{
		FileHelper.checkExists(folder+"/files.txt");
		List<String> filenames=Lists.newArrayList();
		appendFilenames(folder,filenames);
		for (String filename : filenames)
		{
			logger.debug("filename="+filename);
		}		
		return filenames;
	}

	private static void appendFilenames(String folder, List<String> filenames)
	{
		logger.debug("checking for files.txt in folder: "+folder);
		String filelist=folder+"/files.txt";
		if (!FileHelper.exists(folder))
			return;
		if (!FileHelper.exists(filelist))
			return;
		String str=FileHelper.readFile(filelist);
		List<String> subfolders=Lists.newArrayList();
		for (String filename : StringHelper.splitLines(str))
		{
			if (filename.indexOf('#')!=-1)
				filename=filename.substring(0,filename.indexOf('#'));
			filename=filename.trim();
			if (!StringHelper.hasContent(filename))
				continue;
			//logger.debug("adding filename: "+filename);
			if (filename.endsWith("/") || !filename.endsWith(".sql"))
				subfolders.add(filename);
			else filenames.add(FileHelper.join(folder,filename));
		}
		for (String subfolder : subfolders)
		{
			logger.debug("subfolder="+subfolder);
			appendFilenames(FileHelper.join(folder,subfolder), filenames);
		}
//		for (String subfolder : FileHelper.listDirectories(folder,true))
//		{
//			//logger.debug("subfolder="+subfolder);
//			appendFilenames(subfolder, filenames);
//		}
	}

	public static String concatenateScripts(List<String> filenames)
	{
		StringBuilder buffer=new StringBuilder();
		for (String filename : filenames)
		{
			FileHelper.checkExists(filename);
			String sql=FileHelper.readFile(filename);
			buffer.append(sql);
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	// some or all may be empty
	// remove empty ones, surround the rest with parentheses, and join with " AND "
	public static String joinSubqueries(String...subqueries)
	{
		List<String> items=StringHelper.clean(subqueries);
		items=StringHelper.wrap(items,"(",")");
		return StringHelper.join(items," AND ");
	}

	public static void reloadViews(DataSource dataSource, MessageWriter out)
	{
		List<String> filenames=getSqlFilesFromPom("pom.xml", "vw_");
		executeFiles(dataSource, SQL_DIR, filenames, out);
	}
	
	public static List<String> getSqlFilesFromPom(String pattern)
	{
		return getSqlFilesFromPom("pom.xml", pattern);
	}
	
	// assumes only one srcFiles tag in the pom file
	// <srcFile>src/main/sql/sequence.sql</srcFile>
	public static List<String> getSqlFilesFromPom(String pomfile, String pattern)
	{
		String pom=FileHelper.readFile(pomfile);
		List<String> filenames=Lists.newArrayList();
		String str=StringHelper.extractBetween(pom, "<srcFiles>", "</srcFiles>");
		str=StringHelper.stripXmlComments(str);
		List<String> lines=StringHelper.split(str, "\n", true);
		for (String line : lines)
		{
			//logger.debug("line="+line);
			if (!line.contains("<srcFile>"))
				continue;
			String path=StringHelper.extractBetween(line, "<srcFile>", "</srcFile>");
			String filename=StringHelper.replace(path, SQL_DIR, "");
			if (!StringHelper.hasContent(pattern) || filename.contains(pattern))
				filenames.add(filename);
		}
		return filenames;
	}

	public static String column2field(String column)
	{
		return StringHelper.toCamelCase(column);
	}
	
	public static String field2column(String field)
	{
		return StringHelper.toUnderscore(field);
	}

	public static List<String> field2column(Collection<String> fields)
	{
		List<String> columns=Lists.newArrayList();
		for (String field : fields)
		{
			columns.add(field2column(field));
		}
		return columns;
	}
	
	public static TableMetaData getTableMetaData(DataSource dataSource, String table)
	{
		TableMetaData tableMetaData=new TableMetaData(table);
		try
		{
			DatabaseMetaData metadata=dataSource.getConnection().getMetaData();
			ResultSet resultSet=metadata.getColumns(null, null, table, null);
			while (resultSet.next())
			{
				String name=resultSet.getString("COLUMN_NAME");
				String type=resultSet.getString("TYPE_NAME");
				tableMetaData.add(new ColumnMetaData(name, type));
			}
			logger.debug("coldata="+tableMetaData.toString());
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
		return tableMetaData;
	}
	
	public static class TableMetaData
	{
		private String table;
		private List<ColumnMetaData> columns=Lists.newArrayList();
		
		public TableMetaData(String table)
		{
			this.table=table;
		}
		
		public void add(ColumnMetaData column)
		{
			columns.add(column);
		}
		
		public boolean hasColumn(String name)
		{
			ColumnMetaData column=findColumn(name);
			return column!=null;
		}
		
		////JpaHelper.field2column(field.getName())
		public ColumnMetaData findColumn(String name)
		{
			name=JpaHelper.field2column(name);
			for (ColumnMetaData column : columns)
			{
				if (StringHelper.compare(column.getName(), name))
					return column;
			}
			return null;
		}
		
		public List<String> getColnames(Collection<ColumnMetaData> columns)
		{
			List<String> colnames=Lists.newArrayList();
			for (ColumnMetaData column : columns)
			{
				colnames.add(column.getName());
			}
			return colnames;
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public String getTable(){return this.table;}
		public List<ColumnMetaData> getColumns(){return this.columns;}
	}
	
	public static class ColumnMetaData
	{
		private final String name;
		private final DataType type;
		
		public ColumnMetaData(String name, String type)
		{
			//System.out.println("column="+name+", type="+type);;
			this.name=name;
			this.type=getDataType(type);
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public Object format(Object value)
		{
			if (value==null)
				return NULL;
			if (value instanceof LocalDate)
				value=LocalDateHelper.format((LocalDate)value, LocalDateHelper.POSTGRES_YYYYMMDD_PATTERN);
			switch(type)
			{
			case INTEGER:
				return formatInteger(value);
			case FLOAT:
				return formatFloat(value);
			case STRING:
			case DATE:
				return formatString(value);
			default:
				System.out.println("no handler for DataType: "+type);
				return formatString(value);
			}
		}
		
		public static DataType getDataType(String type)
		{
			if (type.equalsIgnoreCase("int4") || type.equalsIgnoreCase("serial"))
				return DataType.INTEGER;
			if (type.equalsIgnoreCase("float4"))
				return DataType.FLOAT; 
			if (type.equalsIgnoreCase("timestamp"))
				return DataType.DATE; 
			if (type.equalsIgnoreCase("text"))
				return DataType.STRING;
			System.out.println("no mapping for SQL type "+type);
			return DataType.STRING;
		}

		private Object formatString(Object value)
		{
			if (value==null)
				return NULL;
			return StringHelper.singleQuote(StringHelper.escapeSql(value.toString()));
		}
		
		private String formatInteger(Object value)
		{
			if (value==null)
				return NULL;
			Integer intvalue=Integer.valueOf(value.toString());
			if (intvalue==null)
				return NULL;
			return intvalue.toString();
		}
		
		private String formatFloat(Object value)
		{
			if (value==null)
				return NULL;
			Float fvalue=Float.valueOf(value.toString());
			if (fvalue==null)
				return NULL;
			return fvalue.toString();
		}
		
		public String getName(){return this.name;}
		public DataType getType(){return this.type;}
	}
	
	//////////////////////////////////////////////////////
	
	public enum FieldOperator
	{
		EQUAL("="),
		LIKE("like"),
		NOT_EQUAL("!="),
		GREATER_THAN(">"),
		GREATER_THAN_OR_EQUAL(">="),
		LESS_THAN("<"),
		LESS_THAN_OR_EQUAL("<="),
		IN("IN");
		
		private final String sql;
		
		FieldOperator(String sql)
		{
			this.sql=sql;
		}
		
		public String getSql() {return sql;}
	}
	
	public enum LogicalOperator
	{
		AND,
		OR
	}
	
	public static void executeFolder(DataSource dataSource, String dir, MessageWriter out)
	{
		for (String filename : JpaHelper.getSqlFilenames(dir))
		{
			executeFile(dataSource, filename, out);
		}
	}
	
	public static void executeFiles(DataSource dataSource, String dir, List<String> filenames, MessageWriter out)
	{
		for (String filename : filenames)
		{
			executeFile(dataSource,FileHelper.join(dir,filename), out);
		}
	}
	
	public static void executeFile(DataSource dataSource, String filename)
	{
		executeFile(dataSource, filename, new MessageWriter());
	}
	
	public static void executeFile(DataSource dataSource, String filename, MessageWriter out)
	{
		out.println("executing SQL file: "+filename);
		String sql=FileHelper.readFile(filename);
		execute(dataSource,sql);
	}

	//http://stackoverflow.com/questions/20452831/how-to-run-a-sql-script-from-file-in-java-and-return-a-resultset-using-spring
//	public static void executeFile(DataSource dataSource, String filename)
//	{
//		try
//		{
//			logger.debug("executing SQL file: "+filename);
//			Resource resource = new FileSystemResource(filename);
//			ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
//		}
//		catch (SQLException e)
//		{
//			throw new CException(e);
//		}
//	}
//	
	
	public static void executeFileIf(DataSource dataSource, String filename, MessageWriter out)
	{
		if (FileHelper.exists(filename))
			executeFile(dataSource, filename, out);
	}
	
	public static void update(DataSource dataSource, String sql)
	{
		JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);
	}
	
	public static void truncateTable(DataSource dataSource, String table)
	{
		execute(dataSource, "TRUNCATE TABLE "+table);
	}
	
	public static void execute(DataSource dataSource, String sql)
	{
		JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
		jdbcTemplate.execute(sql);
	}
	
	public static void execute(NamedParameterJdbcTemplate jdbcTemplate, String sql)
	{
		System.out.println(sql);
		jdbcTemplate.getJdbcOperations().execute(sql);
	}

	public static IntegerDataFrame executeQuery(DataSource dataSource, String sql)
	{
		//logger.debug(sql);
		DataFrameRowMapper mapper=new DataFrameRowMapper();
		executeQuery(dataSource, sql, mapper);
		return mapper.getDataFrame();
	}
	
	public static void executeQuery(DataSource dataSource, String sql, RowMapper<?> mapper)
	{
		JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
		executeQuery(jdbcTemplate, sql, mapper);
	}
	
	public static void executeQuery(JdbcTemplate jdbcTemplate, String sql, RowMapper<?> mapper)
	{
		jdbcTemplate.query(sql, mapper);
	}
	
	//http://stackoverflow.com/questions/6514876/converting-resultset-to-json-faster-or-better-way
	public static Object getValue(ResultSet rs, String column, int type)
	{
		try
		{
			if (type==java.sql.Types.ARRAY)
				return rs.getArray(column);
			else if (type==java.sql.Types.BIGINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.BOOLEAN)
				return rs.getBoolean(column);
			else if (type==java.sql.Types.BLOB)
				return rs.getBlob(column);
			else if (type==java.sql.Types.DOUBLE)
				return rs.getDouble(column);
			else if (type==java.sql.Types.FLOAT)
				return rs.getFloat(column);
			else if (type==java.sql.Types.INTEGER)
				return rs.getInt(column);
			else if (type==java.sql.Types.NVARCHAR)
				return rs.getNString(column);
			else if (type==java.sql.Types.VARCHAR)
				return rs.getString(column);
			else if (type==java.sql.Types.TINYINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.SMALLINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.DATE)
				return rs.getDate(column);
			else if (type==java.sql.Types.TIMESTAMP)
			   return rs.getTimestamp(column);
			else
			{
				//System.err.println("no handler for SQL type: "+type);
				return rs.getObject(column);
			}
			//else throw new CException("no handler for SQL type: "+type);
		}
		catch (SQLException e)
		{
			throw new CException("column="+column+", type="+type, e);
		}
	}
	
	public static Object getValue(ResultSet rs, Integer column, int type)
	{
		try
		{
			if (type==java.sql.Types.ARRAY)
				return rs.getArray(column);
			else if (type==java.sql.Types.BIGINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.BOOLEAN)
				return rs.getBoolean(column);
			else if (type==java.sql.Types.BLOB)
				return rs.getBlob(column);
			else if (type==java.sql.Types.DOUBLE)
				return rs.getDouble(column);
			else if (type==java.sql.Types.FLOAT)
				return rs.getFloat(column);
			else if (type==java.sql.Types.INTEGER)
				return rs.getInt(column);
			else if (type==java.sql.Types.NVARCHAR)
				return rs.getNString(column);
			else if (type==java.sql.Types.VARCHAR)
				return rs.getString(column);
			else if (type==java.sql.Types.TINYINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.SMALLINT)
				return rs.getInt(column);
			else if (type==java.sql.Types.DATE)
				return rs.getDate(column);
			else if (type==java.sql.Types.TIMESTAMP)
			   return rs.getTimestamp(column);
			else
			{
				//System.err.println("no handler for SQL type: "+type);
				return rs.getObject(column);
			}
			//else throw new CException("no handler for SQL type: "+type);
		}
		catch (SQLException e)
		{
			throw new CException("column="+column+", type="+type,e);
		}
	}

	public static String exportAsTable(Class<?> cls, Collection<?> items, String tablename, List<String> skip)
	{
		List<String> lines=Lists.newArrayList();		
		for (Object item : items)
		{
			List<String> values=getValues(cls, item, skip);
			values=StringHelper.escapeSql(values);
			values=StringHelper.wrap(values,"'");
			lines.add("(DEFAULT, NULL, "+StringHelper.join(values,",")+")");
		}
		List<String> columns=StringHelper.wrapCollection(getFieldNames(cls, skip), "\"");
		String sql="DROP TABLE IF EXISTS "+tablename+" CASCADE;\n\n";
		sql+="CREATE TABLE "+tablename+"\n";
		sql+="(\n\trowid SERIAL PRIMARY KEY,\n\tdbno INTEGER NULL,\n"+StringHelper.join(StringHelper.wrap(columns,"\t"," TEXT"),",\n")+"\n);\n\n";
		sql+="INSERT INTO "+tablename+"\nVALUES\n";
		sql+=StringHelper.join(lines,",\n");
		return sql;
	}
	
	private static List<Field> getFields(Class<?> cls, List<String> skip)
	{
		List<Field> fields=Lists.newArrayList();
		for (Field field : cls.getDeclaredFields())
		{
			//logger.debug("field: "+StringHelper.toString(field));
			if (field.getType().isInterface() || field.getName().startsWith("this") || skip.contains(field.getName()))
				continue;
			fields.add(field);
		}
		return fields;
	}
	
	private static List<String> getFieldNames(Class<?> cls, List<String> skip)
	{
		List<String> fields=Lists.newArrayList();
		for (Field field : getFields(cls, skip))
		{
			//logger.debug("field: "+StringHelper.toString(field));
			fields.add(field.getName());
		}
		return fields;
	}
	
	private static List<String> getValues(Class<?> cls, Object obj, List<String> skip)
	{
		List<String> values=Lists.newArrayList();
		for (Field field : getFields(cls, skip))
		{
			try
			{
				field.setAccessible(true);
				Object value=field.get(obj);
				if (value==null)
					value="";
				//logger.debug("value "+value);
				values.add(value.toString());
			}
			catch (Exception e)
			{
				System.err.println(e);
			}
		}
		return values;
	}

	public static List<String> getColumns(DataSource dataSource, String table)
	{
		// don't return any rows - just list columns
		String sql="select * from "+table+" limit 1";
		MetadataRowMapper<Object> mapper=new MetadataRowMapper<Object>()
		{
			protected void mapRow(ResultSet rs, int rowNum, Object obj){}
		};
		JpaHelper.executeQuery(dataSource,sql,mapper);
		return mapper.getColumns();
	}

	public abstract static class MetadataRowMapper<T> implements RowMapper<T>
	{
		protected List<String> columns;
		protected List<String> fields;
		protected List<Integer> types;
		private int reportThreshold=10000;
		private MessageWriter out=new MessageWriter();
		
		public T mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			getColumnNames(rs);
			T obj=createEntity(rs,rowNum);
			notify(rowNum);
			mapRow(rs, rowNum, obj);
			return obj;
		}
		
		protected void notify(int rowNum)
		{
			if (rowNum % reportThreshold==0)
				out.println("mapping row "+rowNum);
		}
		
		protected abstract void mapRow(ResultSet rs, int rowNum, T obj);
		
		protected T createEntity(ResultSet rs, int rowNum)
		{
			return null;
		}
		
		public List<String> getColumns()
		{
			return columns;
		}
		
		public void setMessageWriter(MessageWriter out)
		{
			this.out=out;
		}
		
		public void setReportThreshold(int reportThreshold)
		{
			assert(reportThreshold>0);
			this.reportThreshold=reportThreshold;
		}
		
		protected void getColumnNames(ResultSet rs) throws SQLException
		{
			if (columns==null)
			{
				columns=Lists.newArrayList();
				types=Lists.newArrayList();	
				ResultSetMetaData metadata=rs.getMetaData();
				for (int column=1;column<=metadata.getColumnCount();column++)
				{
					columns.add(metadata.getColumnName(column));
					types.add(metadata.getColumnType(column));
				}
				//logger.debug("columns="+StringHelper.join(columns));
				//logger.debug("types="+StringHelper.join(types));
				this.fields=getFields(columns);
				postProcessMetadata();
			}
		}
		
		protected List<String> getFields(List<String> columns)
		{
			List<String> fields=Lists.newArrayList();
			for (String column : columns)
			{
				fields.add(JpaHelper.column2field(column));
			}
			//logger.debug("fields="+StringHelper.join(fields,","));
			return fields;
		}
		
		protected Map<String,Object> getAsMap(ResultSet rs)//, int rowNum)
		{
			Map<String,Object> map=Maps.newLinkedHashMap();
			for (int index=0; index<columns.size(); index++)
			{
				String column=columns.get(index);
				Integer type=types.get(index);
				String field=fields.get(index);
				Object value=JpaHelper.getValue(rs, column, type);
				if (value!=null)
					map.put(field, value);
			}
			return map;
		}		
		
		protected void postProcessMetadata(){}
	
		protected boolean hasColumn(String name)
		{
			return columns.contains(name);
		}
		
		protected Object getValue(ResultSet rs, int colnum)
		{
			String column=columns.get(colnum);
			int type=types.get(colnum);
			return JpaHelper.getValue(rs, column, type);
		}
		
		protected Object getValue(ResultSet rs, String column)
		{
			Integer index=findColumn(column);
			if (index==-1)
				throw new CException("cannot find expected column: "+column);
			int type=types.get(index);
			return JpaHelper.getValue(rs, column, type);
		}
		
		protected String nullIfEmpty(String value)
		{
			return StringHelper.hasContent(value) ? value : null;
		}
		
		protected String getStringValue(ResultSet rs, String column)
		{
			return (String)getValue(rs, column);
		}
		
		protected String getTrimmedStringValue(ResultSet rs, String column)
		{
			String value=getStringValue(rs, "description");
			if (value==null)
				throw new CException(getClass().getName()+": value of ["+column+"] column is null. rs="+JsonHelper.toJson(getAsMap(rs)));
			return value.trim();
		}
		
		protected Integer getIntValue(ResultSet rs, String column)
		{
			return (Integer)getValue(rs, column);
		}
		
		protected Float getFloatValue(ResultSet rs, String column)
		{
			return (Float)getValue(rs, column);
		}
		
		protected Integer findColumn(String name)
		{
			return columns.indexOf(name);
		}
	}
	
	public static class DataFrameRowMapper extends MetadataRowMapper<Object>
	{
		protected IntegerDataFrame dataFrame=new IntegerDataFrame(true);

		@Override
		protected void mapRow(ResultSet rs, int rowNum, Object obj)
		{
			for (int index=0;index<columns.size();index++)
			{
				String column=columns.get(index);
				int type=types.get(index);
				Object value=JpaHelper.getValue(rs, column, type);
				//LogUtil.log("trying to set column "+column+"="+value+" type="+type);
				//logger.debug("trying to set column "+column+"="+value);
				dataFrame.setValue(column, rowNum, value);
			}
		}
		
		@Override
		protected Object createEntity(ResultSet rs, int rowNum)
		{
			return rowNum;
		}
		
		public IntegerDataFrame getDataFrame()
		{
			return dataFrame;
		}
	}
	
	public abstract static class BeanRowMapper<T> extends MetadataRowMapper<T>
	{
		protected BeanHelper beanhelper=new BeanHelper();

		@Override
		protected void mapRow(ResultSet rs, int rowNum, T obj)
		{
			for (int index=0;index<columns.size();index++)
			{
				String column=columns.get(index);
				int type=types.get(index);
				Object value=JpaHelper.getValue(rs, column, type);
				setProperty(obj,column,value);
			}
		}

		protected void setProperty(T obj, String property, Object value)
		{
			//logger.debug("trying to set property "+property+"="+value);
			BeanHelper.setProperty(obj, property, value);
		}
	}
}

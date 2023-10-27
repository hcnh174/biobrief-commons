package org.biobrief.jpa;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.biobrief.util.CException;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.LocalDateHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

//http://jackcess.sf.net/
//http://jackcess.sourceforge.net/
//https://github.com/jahlborn/jackcess
//http://javadox.com/com.healthmarketscience.jackcess/jackcess/1.2.10/com/healthmarketscience/jackcess/Database.html
//https://www.codeproject.com/tips/883036/java-and-microsoft-access-via-jackcess-how-to-conn
//https://www.programcreek.com/java-api-examples/?api=com.healthmarketscience.jackcess.Database
//https://gist.github.com/cofearabi/8d6d37a69f883f04e430
//view-source:http://webcache.googleusercontent.com/search?q=cache:pvnw6HQJvl8J:jackcess.sourceforge.net/cookbook.html+&cd=2&hl=en&ct=clnk&gl=jp
public final class JackcessHelper
{	
	public static Database openDatabase(String filename)
	{
		try
		{
			File file = new File(filename);
			if(!file.exists() || file.isDirectory())
				throw new CException("cannot find Access file: "+filename);
			Database db = DatabaseBuilder.open(file);
			Set<String> tables = db.getTableNames();
			for (String table : tables)
			{
				System.out.println("table="+table);
			}
			return db;
		}
		catch (IOException e)
		{
			throw new CException("Can't create or open file: "+filename, e);
		}
	}
	
	public static Table getTable(Database database, String name)
	{
		try
		{
			Table table = database.getTable(name);//"Table 1"
			for (Column col : table.getColumns())
			{
				System.out.println("column: "+col.getName()+" (" + col.getType() + ")");
			}
			return table;
		}
		catch (IOException e)
		{
		   throw new CException("Can't create or open file testDB.accdb", e);
		}
	}
	
	public static StringDataFrame extract(Table table)
	{
		StringDataFrame dataframe=new StringDataFrame();
		for (Column col : table.getColumns())
		{
			dataframe.addColumn(col.getName());
		}
		for (Row row : table)
		{
			String rowname=row.getId().toString();
			dataframe.addRow(rowname);
			for (Column col : table.getColumns())
			{
				dataframe.setValue(col.getName(), rowname, getValue(row, col));//row.get(colname)
			}
		}
		return dataframe;
	}
	
	//https://github.com/jahlborn/jackcess/blob/master/src/main/java/com/healthmarketscience/jackcess/Row.java
	private static Object getValue(Row row, Column col)
	{
		String colname=col.getName();
		switch(col.getType())
		{
		case TEXT:
		case MEMO:
		case GUID:
			return row.getString(colname);
		case BOOLEAN:
			return row.getBoolean(colname);
		case BYTE:
			return row.getByte(colname);
		case INT:
			return row.getShort(colname);
		case LONG:
			return row.getInt(colname);
		case MONEY:
		case NUMERIC:
			return row.getBigDecimal(colname);
		case FLOAT:
			return row.getFloat(colname);
		case DOUBLE:
			return row.getDouble(colname);
		case SHORT_DATE_TIME:
			//return row.getDate(colname);
			//return formatDate(row.getDate(colname));
			//return row.getDate(colname);
			return LocalDateHelper.asDate(row.getLocalDateTime(colname));
		case BINARY:
		case OLE:
			return row.getBytes(colname);
		case COMPLEX_TYPE:
			return row.getForeignKey(colname);
		default:
			throw new CException("no handler for Jackcess type: "+col.getType()+" colname="+colname);
		}
	}
	
//	private static String formatDate(Date date)
//	{
//		return DateHelper.format(date, DateHelper.DATE_PATTERN);
//	}
	
	public static String getInsertSql(Table table)
	{
		return getInsertSql(table, table.getName());
	}
	
	public static String getInsertSql(Table table, String tablename)
	{
		List<String> colnames=getColnames(table);	
		StringBuilder buffer=new StringBuilder();
		buffer.append("INSERT INTO "+tablename+" ");
		buffer.append("(").append(StringHelper.join(StringHelper.doubleQuote(colnames), ", ")).append(") ");
		buffer.append("VALUES ");
		buffer.append("(").append(StringHelper.join(StringHelper.duplicateString("?", colnames.size()), ", ")).append(") ");
		return buffer.toString();
	}
	
	public static List<String> getColnames(Table table)
	{
		List<String> colnames=Lists.newArrayList();
		for (Column col : table.getColumns())
		{
			colnames.add(col.getName());
		}
		return colnames;
	}
	
//	public static IntegerDataFrame extract(Table table)
//	{
//		IntegerDataFrame dataframe=new IntegerDataFrame();
//		List<String> columns=Lists.newArrayList();
//		for (Column col : table.getColumns())
//		{
//			columns.add(col.getName());
//			dataframe.addColumn(col.getName());
//		}
//		int rowname=0;
//		for (Row row : table)
//		{
//			System.out.println("rowid="+row.getId());
//			dataframe.addRow(rowname);
//			int colnum=0;
//			for (Object value : row.values().toArray())
//			{
//				String colname=columns.get(colnum);
//				dataframe.setValue(colname, rowname, value);
//				colnum++;
//			}
//			rowname++;
//		}
//		return dataframe;
//	}
	
//	public static CTable extract(Table table)
//	{
//		//DataFrame dataframe=new DataFrame();
//		CTable dataframe=new CTable();
//		for (Column col : table.getColumns())
//		{
//			//System.out.println("column: "+col.getName());
//			//dataframe.addColumn(col.getName());
//			dataframe.addHeader(col.getName());
//		}
//		for (Row row : table)
//		{
//			CTable.Row trow=dataframe.addRow();
//			//System.out.println("row="+row.values().toArray());
//			for (Object value : row.values().toArray())
//			{
//				//System.out.println("value="+value);
//				trow.add(value);
//			}
//		}
//		return dataframe;
//	}
	
//	
//	public static void test()
//	{
//		try
//		{
//			String filename="h:/sync/dataroom/肝臓疾患File.mdb";
//			Database db=openDatabase(filename);//".temp/testDB.accdb"
//			Table table= db.getTable("まとめ");//"Table 1"
//			// Add all columns from table
//			for (Column col : table.getColumns())
//			{
//				System.out.println("column: "+col.getName());
//			}
//			
//			// Add all rows from table
//			for (Row row : table)
//			{
//				System.out.println("row="+row.values().toArray());
//			}
//		}
//		catch (IOException e)
//		{
//		   throw new CException("Can't create or open file testDB.accdb", e);
//		}
//	}
	
//	public static Database openDatabase(String filename)
//	{
//		Connection connect = null;
//		Statement statement = null;
//		Database db = null;
//		
//		try
//		{
//			File dbFile = new File(filename);
//			if(dbFile.exists() && !dbFile.isDirectory())
//			{
//				db = DatabaseBuilder.open(dbFile);
//			}
//			else
//			{
//				db = DatabaseBuilder.create(Database.FileFormat.V2007, dbFile);
//				createTable(db);
//			}
//			Set<String> tables = db.getTableNames();
//			for (String table : tables)
//			{
//				System.out.println("table="+table);
//			}
//			return db;
//		}
//		catch (IOException e)
//		{
//			throw new CException("Can't create or open file testDB.accdb", e);
//		}
//	}
	
//	public static void createTable(Database db)
//	{
//		try
//		{
//			if (db.getTableNames().contains("Table 1"))
//				return;
//			Table tblNew = new TableBuilder("Table 1")
//				.addColumn(new ColumnBuilder("id", DataType.LONG).setAutoNumber(true))
//				.addColumn(new ColumnBuilder("name", DataType.TEXT))
//				.addColumn(new ColumnBuilder("age", DataType.INT))
//				.addIndex(new IndexBuilder(IndexBuilder.PRIMARY_KEY_NAME)
//				.addColumns("id").setPrimaryKey())
//				.toTable(db);
//
//			// Fill table by data
//			tblNew.addRow(Column.AUTO_NUMBER, "John", 27);
//			tblNew.addRow(Column.AUTO_NUMBER, "Peter", 43);
//		}
//		catch (IOException e)
//		{
//		   throw new CException("Can't create or open file testDB.accdb", e);
//		}
//	}
	
}
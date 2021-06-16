package org.biobrief.jpa;

import java.util.List;
import java.util.Optional;

import org.biobrief.util.CException;
import org.biobrief.util.DataType;
import org.biobrief.util.StringHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class BatchSqlInsertGenerator
{
	private boolean create=false;
	private String table;
	private boolean ignoreConflicts=true;
	private final List<String> columns=Lists.newArrayList();
	private final List<DataType> types=Lists.newArrayList();
	private final Table<Integer,String, Optional<Object>> data=HashBasedTable.create();
	private Integer rownum=0;
	
	public BatchSqlInsertGenerator(String table)
	{
		table(table);
	}
	
	public BatchSqlInsertGenerator(String table, boolean create)
	{
		this(table);
		this.create=create;
	}
	
	public BatchSqlInsertGenerator table(String table)
	{
		this.table=table;
		return this;
	}
	
	public BatchSqlInsertGenerator ignoreConflicts(boolean ignoreConflicts)
	{
		this.ignoreConflicts=ignoreConflicts;
		return this;
	}
	
	public BatchSqlInsertGenerator setColumns(List<String> columns)
	{
		return setColumns(columns, DataType.STRING);
	}
	
	public BatchSqlInsertGenerator setColumns(List<String> columns, DataType type)
	{
		for (String column : columns)
		{
			addColumn(column, type);
		}
		return this;
	}
	
	public BatchSqlInsertGenerator setColumns(List<String> columns, List<DataType> types)
	{
		for (int index=0; index<types.size(); index++)
		{
			String column=columns.get(index);
			DataType type=types.get(index);
			addColumn(column, type);
		}
		return this;
	}
	
	public BatchSqlInsertGenerator addColumn(String column)
	{
		return addColumn(column, DataType.STRING);
	}
	
	public BatchSqlInsertGenerator addColumn(String column, DataType type)
	{
		this.columns.add(column);
		this.types.add(type);
		return this;
	}
	
	// assumes column already exists
	public BatchSqlInsertGenerator setType(String column, DataType type)
	{
		if (!columns.contains(column))
			throw new CException("cannot find column named: "+column);
		int index=columns.indexOf(column);
		types.set(index, type);
		return this;
	}
	
	private void nextRow(){this.rownum++;}
	
	public BatchSqlInsertGenerator addRow(List<Object> values)
	{
		if (values.size()!=columns.size())
			throw new CException("row does not have the expected number of columns: "+values.size()+" vs "+columns.size()+": "+StringHelper.join(values));
		nextRow();
		//System.out.println("addRow: values="+StringHelper.join(values));
		for (int index=0; index<values.size(); index++)
		{
			String column=columns.get(index);
			Object value=values.get(index);
			put(column, value);
		}
		return this;
	}
	
	public void put(String column, Object value)
	{
		//System.out.println("put("+column+", "+value+")");
		data.put(rownum, column, Optional.ofNullable(value));
	}
	
	public String getSql()
	{
		StringBuilder buffer=new StringBuilder();
		if (create)
			buffer.append(getCreateSql());
		buffer.append("INSERT INTO "+table+"("+formatColumns()+")\n");
		buffer.append("VALUES\n");
		buffer.append(formatRows());
		if (ignoreConflicts)
			buffer.append("\nON CONFLICT DO NOTHING");
		buffer.append(";\n");
		return buffer.toString();
	}
	
	private String getCreateSql()
	{
		List<String> cols=Lists.newArrayList();
		for (int index=0; index<columns.size(); index++)
		{
			String column=columns.get(index);
			DataType type=getType(index);
			cols.add("\t"+column+" "+type.getSql()+" NULL");
		}	
		StringBuilder buffer=new StringBuilder();
		buffer.append("DROP TABLE IF EXISTS "+table+" CASCADE;\n\n");
		buffer.append("CREATE TABLE "+table+"\n");
		buffer.append("(\n");
		buffer.append(StringHelper.join(cols, ",\n"));
		buffer.append(");\n\n");
		return buffer.toString();
	}
	
	private String formatColumns()
	{
		return StringHelper.join(StringHelper.wrap(columns,"\""));
	}
	
	private String formatRows()
	{
		List<String> rows=Lists.newArrayList();
		for (Integer num : data.rowKeySet())
		{
			rows.add(formatRow(num));
		}
		return StringHelper.join(rows, ",\n");
	}
	
	private String formatRow(Integer rowindex)
	{
		List<String> values=Lists.newArrayList();
		for (int colindex=0; colindex<types.size(); colindex++)
		{
			String column=columns.get(colindex);
			Optional<Object> value=data.get(rowindex, column);
			DataType type=types.get(colindex);
			values.add(type.formatSql(value));
		}
		return StringHelper.parenthesize(StringHelper.join(values, ","));
	}
	
	public DataType getType(String column)
	{
		int index=this.columns.indexOf(column);
		return types.get(index);
	}
	
	public DataType getType(int index)
	{
		return this.types.get(index);
	}
}

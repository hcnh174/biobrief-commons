package org.biobrief.util;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ExcelDataLoaderTemplate
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(ExcelDataLoaderTemplate.class);
	
	private final String source;
	private Integer rownum=null;
	private Integer counter=0;
	private final List<Field> fields=Lists.newArrayList();
	private final ExcelHelper helper=new ExcelHelper();
	private final XSSFColor SELECTED=new XSSFColor(IndexedColors.YELLOW, new DefaultIndexedColorMap());
		
	public ExcelDataLoaderTemplate(String source, Sheet sheet)
	{
		this.source=source;
		for (Row row : sheet)
		{
			for (Cell cell : row)
			{
				if (ExcelHelper.hasFillColor(cell, SELECTED))
				{
					String name=helper.getStringCellValue(cell);
					String label=helper.getStringCellValue(helper.getCell(sheet, cell.getColumnIndex(), row.getRowNum()-1));
					add(row.getRowNum(), cell.getColumnIndex(), name, label);
				}
			}
		}
	}

	public void add(Integer row, Integer column, String name, String label)
	{
		add(new Field(row, column, name, label));
	}
	
	public void add(Field field)
	{
		if (rownum==null)
			rownum=field.row;
		else if (this.rownum!=field.row)
			throw new CException("all selected columns should be on the same row: "+rownum+"!="+field.row);
		this.fields.add(field);
	}
	
	public Integer getFirstRow()
	{
		return rownum+1;
	}
	
	public StringDataFrame extract(Sheet sheet)
	{
		if (sheet==null)
			throw new CException("sheet is null");
		StringDataFrame dataframe=createDataFrame();
		checkLabels(sheet);
		for (Row row : sheet)
		{
			extract(row, dataframe);
		}
		return dataframe;
	}
	
	private void checkLabels(Sheet sheet)
	{
		int headerrow=rownum-1;
		for (Field field : fields)
		{
			Cell cell=helper.getCell(sheet, field.column, headerrow);
			String label=helper.getStringCellValue(cell);
			if (!field.label.equals(label))
				throw new CException("label in worksheet "+sheet.getSheetName()+" ["+label+"] does not match label in template ["+field.label+"]");
		}
	}
	
	private StringDataFrame createDataFrame()
	{
		StringDataFrame dataframe=new StringDataFrame();
		for (Field field : fields)
		{
			dataframe.addColumn(field.name);
		}
		return dataframe;
	}
	
	public void extract(Row row, StringDataFrame dataframe)
	{
		System.out.println("extracting row: "+row.getRowNum());
		if (row.getRowNum()<this.rownum)
			return;
		for (Field field : fields)
		{
			Cell cell=row.getCell(field.column);
			Object value=helper.getCellValue(cell);
			value=adjustValue(value);
			dataframe.setValue(field.name, counter.toString(), value);
		}
		counter++;
	}
		
	private Object adjustValue(Object value)
	{
		if (value==null)
			return null;
		if (value instanceof Date)
			return adjustDateValue(value);
		return value;
	}
	
	private String adjustDateValue(Object value)
	{
		Date date=(Date)value;
		return DateHelper.format(date, DateHelper.DATE_PATTERN);
	}
	
	public String getInsertSql()
	{
		List<String> colnames=getColnames();	
		StringBuilder buffer=new StringBuilder();
		buffer.append("INSERT INTO "+this.source+" ");
		buffer.append("(").append(StringHelper.join(StringHelper.doubleQuote(colnames), ", ")).append(") ");
		buffer.append("VALUES ");
		buffer.append("(").append(StringHelper.join(StringHelper.duplicateString("?", colnames.size()), ", ")).append(") ");
		return buffer.toString();
	}
	
	private List<String> getColnames()
	{
		List<String> colnames=Lists.newArrayList();
		for (Field field : fields)
		{
			colnames.add(field.name);
		}
		return colnames;
	}
	
	public String createDictionaryFile()
	{
		CTable table=new CTable();
		table.addHeader("name");
		table.addHeader("type");
		table.addHeader("label");
		for (Field field : fields)
		{
			CTable.Row row=table.addRow();
			row.add(field.name);
			row.add("String");
			row.add(StringHelper.replace(field.label, "\r",""));
		}
		return table.toString();
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
	
	public static class Field
	{
		private final Integer row;
		private final Integer column;
		private final String name;
		private final String label;
		
		public Field(Integer row, Integer column, String name, String label)
		{
			this.row=row;
			this.column=column;
			this.name=name;
			this.label=label;
		}
	}
}
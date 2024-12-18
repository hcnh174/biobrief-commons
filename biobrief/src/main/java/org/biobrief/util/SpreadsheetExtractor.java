package org.biobrief.util;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SpreadsheetExtractor
{
	public static final String FILE_COLUMN="file";
	public static final String SHEET_COLUMN="sheet";
	public static final String SETTINGS_SHEET="settings";
	
	private MessageWriter writer=new MessageWriter();
	private ExcelHelper excelhelper=new ExcelHelper(writer);
	private Params params;
	private Config config;

	//////////////////////////////////////////////////////////////////////////////////
	
	public static SpreadsheetExtractor create(String template)
	{
		Params params=new Params(template);
		return new SpreadsheetExtractor(params);
	}
	
	public SpreadsheetExtractor(Params params)
	{
		this.params=params;
		try
		{
			//log.debug("importing data from directory "+params.baseDir);
			config=new Config(params.template);
			//log.debug(config.toString());
		}
		catch (Exception e)
		{
			logExtractor(e.toString());
			e.printStackTrace();
		}
	}

	public void writeSpreadsheet(String filename, DataFrame<String> dataframe)
	{
		//log.debug("Writing output to "+filename);
		Workbook workbook=excelhelper.createWorkbook(filename);
		excelhelper.createWorksheet(workbook, dataframe, "patients");
		excelhelper.writeWorkbook(workbook, filename);
		FileHelper.writeFile(filename+".txt",dataframe.toString(), false);
	}
	
	public DataFrame<String> loadFolder(String dir)
	{
		List<String> filenames=listFiles(dir);
		return loadFiles(filenames);
	}
	
	public DataFrame<String> loadFiles(List<String> filenames)
	{
		DataFrame<String> dataframe=createDataFrame();
		//log.debug("loading files");
		for (String filename : filenames)
		{
			loadFile(filename,dataframe);
		}
		return dataframe;
	}

	public DataFrame<String> loadFile(String filename)
	{
		DataFrame<String> dataframe=createDataFrame();
		loadFile(filename,dataframe);
		return dataframe;
	}
	
	private void loadFile(String filename, DataFrame<String> dataframe)
	{
		logExtractor("loading spreadsheet="+filename);
		Workbook workbook=null;
		try
		{
			workbook=excelhelper.openWorkbook(filename);
		}
		catch (Exception e)
		{
			logExtractor("Cannot open file "+filename+": "+e);
			return;
		}
		for (int sheetnum=0;sheetnum<workbook.getNumberOfSheets();sheetnum++)
		{
			Sheet sheet=workbook.getSheetAt(sheetnum);
			if (!sheetMatches(sheet))
				continue;
			loadSheet(filename,sheet,dataframe);
		}
		excelhelper.closeWorkbook(workbook);
	}
	
	private void loadSheet(String filename, Sheet sheet, DataFrame<String> dataframe)
	{
		if (config.skipSheet(sheet))
			return;
//		try
//		{
//			config.checkSheet(sheet);
//		}
//		catch(CException e)
//		{
//			error(filename+": sheet "+sheet.getSheetName()+": "+e.getMessage());
//			return;
//		}
		String rowname=getRowname(filename,sheet);
		logExtractor("sheet name: "+sheet.getSheetName());
		//dataframe.setValue(FILE_COLUMN, rowname, params.stripBaseDir(filename));
		dataframe.setValue(FILE_COLUMN, rowname, filename);
		dataframe.setValue(SHEET_COLUMN, rowname, sheet.getSheetName());
		for (Field field : config.getFields())
		{
			String colname=field.getName();
			Object value=getValue(sheet,field);
			dataframe.setValue(colname, rowname, value);
		}
	}
	
	private DataFrame<String> createDataFrame()
	{	
		DataFrame<String> dataframe=new DataFrame<String>(true);
		dataframe.addColumn(FILE_COLUMN);
		dataframe.addColumn(SHEET_COLUMN);
		for (Field field : config.getFields())
		{
			dataframe.addColumn(field.getName());
		}
		//log.debug(dataframe.toString());
		return dataframe;
	}
	
	public List<String> listFiles(String dir)
	{
		List<String> filenames=Lists.newArrayList();
		List<String> list=Lists.newArrayList();
		list.addAll(FileHelper.listFilesRecursively(dir, ".xls", config.recursive));
		list.addAll(FileHelper.listFilesRecursively(dir, ".xlsx", config.recursive));
		for (String filename : list)
		{
			String name=FileHelper.stripPath(filename);
			if (name.equals(params.template)) // skip template file
				continue;
			if (name.startsWith(ExcelHelper.OPEN_FILE_PREFIX))
				continue;
			if (name.startsWith("selected-")) // skip output files
				continue;
			if (name.contains("ひな形")) // skip sample files
				continue;
			if (name.charAt(0)=='.' || name.startsWith("【"))
				continue;
			if (!filenameMatches(filename))
				continue;
			//log.debug("filename="+filename);
			filenames.add(filename);
		}
		return filenames;
	}
	
	private String getRowname(String filename, Sheet sheet)
	{
		return filename+"!"+sheet.getSheetName();
		//return FileHelper.stripPath(filename)+"!"+sheet.getSheetName();//todo - fix sheetId
		//return params.stripBaseDir(filename)+"!"+sheet.getSheetName();
	}
	
	private Object getValue(Sheet sheet, Field field)
	{
		Object value=excelhelper.getCellValue(sheet, field.getRow(), field.getCol());
		//Object value=getCellValue(sheet, field.getRow(), field.getCol());
		//if (value==null)
		//	log.debug("	value="+null);
		//else log.debug("	value="+value+" "+value.getClass().getCanonicalName());
		value=adjustValue(value);
		return value;
	}
	
	private Object adjustValue(Object value)
	{
		if (value==null)
			return null;
		if (value instanceof LocalDate)
			return adjustLocalDateValue(value);
		if (value instanceof Date)
			return adjustDateValue(value);
		return value;
	}
	
	private String adjustLocalDateValue(Object value)
	{
		LocalDate date=(LocalDate)value;
		if (date.getYear()<1902)
		{
			System.err.println("trying to parse value as date but year is less than 1902: "+value);
			return null;
		}
		return LocalDateHelper.format(date, LocalDateHelper.DATE_PATTERN);
	}
	
	private String adjustDateValue(Object value)
	{
		Date date=(Date)value;
		if (DateHelper.getYear(date)<1902)
		{
			System.err.println("trying to parse value as date but year is less than 1902: "+value);
			return null;
		}
		return DateHelper.format(date, DateHelper.DATE_PATTERN);
	}
	
	private boolean filenameMatches(String path)
	{
		String filename=FileHelper.stripPath(path);
		boolean matches=filename.matches(config.filepattern);
		if (!matches)
			LogUtil.debug("filename "+filename+" does not match pattern "+config.filepattern+". skipping. ("+path+")");
		return matches;
	}
	
	private boolean sheetMatches(Sheet sheet)
	{
		boolean matches=sheet.getSheetName().matches(config.sheetpattern);
		if (!matches)
			LogUtil.debug("sheet "+sheet.getSheetName()+" does not match pattern "+config.sheetpattern+". skipping.");
		return matches;
	}
	
	private static void logExtractor(String message)
	{
		String logfile="extractor.txt";
		LogUtil.logMessage(logfile, message);
	}
	public class Field
	{
		protected String address;
		protected String name;
		protected Integer col;
		protected Integer row;
		
		public String getAddress(){return this.address;}
		public void setAddress(final String address){this.address=address;}

		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public Integer getCol(){return this.col;}
		public void setCol(final Integer col){this.col=col;}

		public Integer getRow(){return this.row;}
		public void setRow(final Integer row){this.row=row;}
		
		public Field(Cell cell)
		{
			this.col=cell.getColumnIndex();
			this.row=cell.getRowIndex();
			this.address=excelhelper.getAddress(cell);
			this.name=getName(cell);			
		}
		
		private String getName(Cell cell)
		{
			Object value=excelhelper.getCellValue(cell);
			if (value==null)
				throw new CException("field cell value is null: "+address);
			return value.toString();
		}
		
		@Override
		public String toString()
		{
			return "["+address+"]="+name;
		}
	}
	
	public class Config
	{
		protected String filepattern=".+\\.xlsx?";//".*[0-9]+-[0-9]+\\.xls";
		protected String sheetpattern=".+";//"[0-9]+";
		protected String templatesheet="template";
		protected boolean recursive=false;
		protected List<String> skipsheets=Lists.newArrayList("ひな形");
		protected XSSFColor checkColor=new XSSFColor(IndexedColors.ORANGE, new DefaultIndexedColorMap());//Color.ORANGE);
		protected XSSFColor selectColor=new XSSFColor(IndexedColors.YELLOW, new DefaultIndexedColorMap());
		protected Map<String,Field> fields=Maps.newLinkedHashMap();
		protected Map<String,String> checkFields=Maps.newLinkedHashMap();
		protected ExcelHelper excelhelper=new ExcelHelper();

		public Config(String template)
		{
			Workbook workbook=excelhelper.openWorkbook(template);
			loadSettings(workbook);
			loadTemplate(workbook);
			excelhelper.closeWorkbook(workbook);
		}
		
		private void loadSettings(Workbook workbook)
		{
			Sheet sheet=workbook.getSheet(SETTINGS_SHEET);
			if (sheet==null)
				throw new CException("no sheet named "+SETTINGS_SHEET+" found");
			CTable table=excelhelper.extractTable(sheet);
			for (CTable.Row row : table.getRows())
			{
				String name=row.getValue(0);
				String value=row.getValue(1);
				setProperty(name,value);
			}
		}
		private void setProperty(String name, String value)
		{
			//log.debug("setting "+name+"="+value);
			if (name.equals("filepattern"))
				filepattern=value;
			else if (name.equals("sheetpattern"))
				sheetpattern=value;
			else if (name.equals("templatesheet"))
				templatesheet=value;
			else if (name.equals("recursive"))
				recursive=Boolean.valueOf(value);
//			else if (name.equals("check"))
//				this.check=value;
			else if (name.equals("skip"))
				this.skipsheets.addAll(StringHelper.split(value));
		}
		
		private void loadTemplate(Workbook workbook)
		{
			Sheet sheet=workbook.getSheet(templatesheet);
			if (sheet==null)
				throw new CException("no sheet named "+templatesheet+" found");
			for (int rownum = sheet.getFirstRowNum(); rownum <= sheet.getLastRowNum(); rownum++)
			{
				Row row = sheet.getRow(rownum);
				if (row==null)
					continue;
				for (int colnum=row.getFirstCellNum(); colnum<row.getLastCellNum();colnum++)
				{
					Cell cell=row.getCell(colnum);
					if (isSelected(cell))
						addField(cell);
					if (isChecked(cell))
						addCheckField(cell);
				}
			}
		}
		
		private boolean isChecked(Cell cell)
		{
			return ExcelHelper.hasFillColor(cell, checkColor);
		}
		
		private boolean isSelected(Cell cell)
		{
			return ExcelHelper.hasFillColor(cell, selectColor);
		}
		
//		private boolean hasFillColor(Cell cell, XSSFColor color)
//		{
//			if (cell==null || cell.getCellStyle()==null || cell.getCellStyle().getFillForegroundColorColor()==null)
//				return false;
//			XSSFColor fgcolor=(XSSFColor)cell.getCellStyle().getFillForegroundColorColor();
//			//log.debug("fgcolor="+fgcolor.getARGBHex()+" color="+color.getARGBHex());//Color.YELLOW);
//			return (fgcolor.getARGBHex().equals(color.getARGBHex()));
//		}
		
		private void addField(Cell cell)
		{
			Object value=excelhelper.getCellValue(cell);
			if (value==null || value.toString().equals(""))// || value.toString().equals("1"))
				return;			
			Field field=new Field(cell);
			fields.put(field.getName(),field);
		}
		
		private void addCheckField(Cell cell)
		{
			String address=excelhelper.getAddress(cell);
			Object value=excelhelper.getCellValue(cell);
			if (value==null || value.toString().equals(""))// || value.toString().equals("1"))
				return;//throw new CException("use a non-empty field as a check value: "+address);
			//log.debug("adding check field: "+address+"="+value.toString());
			checkFields.put(address,value.toString());
		}
		
		public Collection<Field> getFields()
		{
			return fields.values();
		}
		
		public void checkSheet(Sheet sheet)
		{
			for (String address : checkFields.keySet())
			{
				String checkvalue=checkFields.get(address);
				checkCell(sheet,address,checkvalue);
			}
		}
		
		private void checkCell(Sheet sheet, String address, String checkvalue)
		{
			Object value=excelhelper.getCellValue(sheet, address);
			if (value==null)
				throw new CException("Expected ["+checkvalue+"] at cell "+address+" but found null");
			if (value.toString().equals(checkvalue))
				return;
			else throw new CException("Expected ["+checkvalue+"] at cell "+address+" but found ["+value+"]");
		}
		
		public boolean skipSheet(Sheet sheet)
		{
			String sheetname=sheet.getSheetName();
			for (String name : skipsheets)
			{
				if (name.equals(sheetname))
					return true;
			}
			return false;
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
	}
	
	public static class Params
	{
		protected String template;

		public String getTemplate(){return this.template;}
		public void setTemplate(final String template){this.template=template;}
		
		public Params(String template)
		{
			this.template=FileHelper.normalize(template);
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
	}	
}

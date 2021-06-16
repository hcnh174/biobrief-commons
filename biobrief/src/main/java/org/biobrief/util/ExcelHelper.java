package org.biobrief.util;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

//https://github.com/vaadin/spreadsheet/blob/master/vaadin-spreadsheet/src/main/java/com/vaadin/addon/spreadsheet/XSSFColorConverter.java
public class ExcelHelper
{	
	private static final Logger logger=LoggerFactory.getLogger(ExcelHelper.class);
	
	public static final String XLSX=".xlsx";
	public static final String OPEN_FILE_PREFIX="~$";
	public static final Integer DEFAULT_FONT_HEIGHT=11;
	public static final Integer DEFAULT_ROW_HEIGHT=15;
	
	private MessageWriter writer=new MessageWriter();
	private List<CellStyle> styleList=Lists.newArrayList();
	private DataFormatter formatter=new DataFormatter();
	//private FileInputStream stream; 
	
	public ExcelHelper(){}
	
	public ExcelHelper(MessageWriter writer)
	{
		this.writer=writer;
	}
	
	public MessageWriter getWriter(){return this.writer;}
	public void setWriter(final MessageWriter writer){this.writer=writer;}
	
	public static List<String> listFilesRecursively(String dir)
	{
		List<String> filenames=Lists.newArrayList();
		for (String filename : FileHelper.listFilesRecursively(dir, ".xlsx"))
		{
			if (!filename.contains(ExcelHelper.OPEN_FILE_PREFIX))
				filenames.add(filename);
		}
		for (String filename : FileHelper.listFilesRecursively(dir, ".xls"))
		{
			if (!filename.contains(ExcelHelper.OPEN_FILE_PREFIX))
				filenames.add(filename);
		}
		return filenames;
	}
	
	//////////////////////////////////////////////////////////
	
	// try to create a table based on a cells in the first sheet of a spreadsheet
	// stop columns when first row cell is blank
	// stop rows when first column cell is blank (other cells may be empty)
	public CTable extractTable(String filename)
	{
		Workbook workbook=openWorkbook(filename);
		Sheet sheet = workbook.getSheetAt(0);
		CTable table=extractTable(sheet);
		table.setIdentifier(FileHelper.getIdentifierFromFilename(filename));
		closeWorkbook(workbook);
		return table;
	}
	
	public CTable extractTable(Sheet sheet)
	{
		return extractTable(sheet, 0);
	}
	
	public CTable extractTable(Sheet sheet, int startrow)
	{
		CTable table=new CTable();
		boolean isHeader=true;
		for (Row row : sheet)
		{
			if (row.getRowNum()<startrow)
				continue;
			if (isHeader)
			{
				extractHeaderRow(row, table);
				isHeader=false;
			}
			else extractRow(row, table);
		}
		return table;
	}
	
	private void extractHeaderRow(Row row, CTable table)
	{
		CTable.Row trow=table.getHeader();
		for (Cell cell : row)
		{
			String value=cell.getStringCellValue();
			//logger.debug("header value=["+value+"]");
			if (!hasContent(value))
				return;
			trow.add(value);
		}
	}
	
	private void extractRow(Row row, CTable table)
	{
		// if the first column is empty, skip and return
		if (!hasContent(getCellValue(row.getCell(0))))
		{
			writer.message("first column is empty - skipping");
			return;
		}
		CTable.Row trow=table.addRow();
		// only read as many columns as there are header fields
		for (int colnum=0; colnum<table.getHeader().size();colnum++)
		{
			Cell cell=row.getCell(colnum);
			if (colnum==0)
				trow.add(getIdentifierCellValue(cell));
			else trow.add(getCellValue(cell));
		}
	}
	
	// copied here to remove a dependency on SpringUtils
	private boolean hasContent(Object obj)
	{
		if (obj==null)
			return false;
		String value=obj.toString();
		if (value.length()==0)
			return false;
		value=value.trim();
		if (value.length()==0)
			return false;
		return !"".equals(value);
	}
	
	// gets last column in entire sheet
	public int getLastColNum(Sheet sheet)
	{
		int max=0;
		for (int rownum=sheet.getFirstRowNum(); rownum<=sheet.getLastRowNum(); rownum++)
		{
			Row row=sheet.getRow(rownum);
			if (row==null)
				continue;
			int num=row.getLastCellNum();
			if (num>max)
				max=num;
		}
		return max;
	}
	
	// gets last column in specified row
	public int getLastColNum(Sheet sheet, int rownum)
	{
		Row row=sheet.getRow(rownum);
		return row.getLastCellNum();
	}
	
	/////////////////////////////////////////////////
	
//	@SuppressWarnings("rawtypes")
//	public DataFrame extractDataFrame(Sheet sheet)
//	{
//		DataFrame<String> dataframe=new DataFrame<String>();
//		boolean isHeader=true;
//		for (Row row : sheet)
//		{
//			if (isHeader)
//			{
//				extractHeaderRow(row, dataframe);
//				isHeader=false;
//			}
//			else extractRow(row, dataframe);
//		}
//		return dataframe;
//	}
	
	public StringDataFrame extractDataFrame(Sheet sheet)
	{
		return extractDataFrame(sheet, 0);
	}
	
	public StringDataFrame extractDataFrame(Sheet sheet, int headerrow)
	{
		StringDataFrame dataframe=new StringDataFrame();
		for (Row row : sheet)
		{
			if (row.getRowNum()<headerrow)
				continue;
			else if (row.getRowNum()==headerrow)
				extractHeaderRow(row, dataframe);
			else extractRow(row, dataframe);
		}
		return dataframe;
	}
	
	private void extractHeaderRow(Row row, DataFrame<?> dataframe)
	{
		//CTable.Row trow=table.getHeader();
		for (Cell cell : row)
		{
			String colname=cell.getStringCellValue();
			if (!StringHelper.hasContent(colname))
				return;
			dataframe.addColumn(colname);
		}
	}
	
	private void extractRow(Row row, DataFrame<String> dataframe)
	{
		// if the first column is empty, skip and return
		if (!StringHelper.hasContent(getCellValue(row.getCell(0))))
		{
			writer.message("first column is empty - skipping");
			return;
		}
		//CTable.Row trow=table.addRow();
		// only read as many columns as there are header fields
		//for (int colnum=0; colnum<table.getHeader().size();colnum++)
		String rowname=getIdentifierCellValue(row.getCell(0));		
		for (int colnum=1; colnum<dataframe.getNumCols(); colnum++)
		{
			Cell cell=row.getCell(colnum);
			String colname=dataframe.getColNames().get(colnum);
			dataframe.setValue(colname, rowname, getCellValue(cell));
		}
	}
	
	//////////////////////////////////////////////////////

	private String getIdentifierCellValue(Cell cell)
	{
		Object value=getCellValue(cell);
		if (value instanceof Double)
			return StringHelper.formatDecimal((Double)value, 0);
		else return value.toString();
	}
	
	public Object getCellValue(Cell cell)
	{
		if (cell==null)
			return null;
		if (cell.getCellType()==CellType.BLANK)
			return null;
		switch(cell.getCellType())
		{
			case BLANK:
				return null;//"(blank)"; // hack! temporary
			case STRING:
				return cell.getRichStringCellValue().getString();
			case NUMERIC:
				return getNumericValue(cell);
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case FORMULA:
				return getCachedFormulaValue(cell);
			case ERROR:
				return formatter.formatCellValue(cell);//String.valueOf(cell.getErrorCellValue());//cell.getErrorCellValue();
			default:
				writer.message("unhandled cell type: "+cell.getCellType());
				return null;
		}
		//return new DataFormatter().formatCellValue(cell);
	}
	
// https://stackoverflow.com/questions/19711603/apache-poi-xlsx-read-cell-with-value-error-unexpected-cell-type5
//	private Object getErrorValue(Cell cell)
//	{
//		byte errorValue = cell.getErrorCellValue();
//		switch(errorValue) {
//		case ERROR_DIV_0:
//			return "#DIV/0!";
//		case ERROR_NA:
//			return "#N/A";
//		case ERROR_NAME:
//			return "#NAME?";
//		case ERROR_NULL:
//			return "#NULL!";
//		case ERROR_NUM:
//			return "#NUM!";
//		case ERROR_REF:
//			return "#REF!";
//		case ERROR_VALUE:
//			return "#VALUE!";
//		default:
//			return "Unknown error value: " + errorValue + "!";
//		}
//	}
	
	//http://stackoverflow.com/questions/7608511/java-poi-how-to-read-excel-cell-value-and-not-the-formula-computing-it
	private Object getCachedFormulaValue(Cell cell)
	{
		try
		{
			if (cell.getCellType()!=CellType.FORMULA)
				throw new CException("expected formula cell type: "+cell.getCellType());
			//logger.debug("Formula is " + cell.getCellFormula());
			switch(cell.getCachedFormulaResultType())
			{
				case NUMERIC:
					return cell.getNumericCellValue();
				case STRING:
					return cell.getRichStringCellValue();
				case ERROR:
					return null;// hack - todo check this
				default:
					writer.message("unhandled cellValue type: "+cell.getCachedFormulaResultType());
					return null;
			}
		}
		catch (NotImplementedException e)
		{
			writer.message("NotImplementedException: ["+"="+cell.getCellFormula().toString()+"]");
			throw e;
			//return null;
		}
		catch (Exception e)
		{
			writer.message(e.toString());
			writer.message("="+cell.getCellFormula().toString());
			throw e;
			//return null;
		}
	}
	
	public static Date getDateCellValue(Cell cell)
	{
		//if (!DateUtil.isCellDateFormatted(cell))
		//	return null;
		if (cell.getCellType()==CellType.NUMERIC)
			return cell.getDateCellValue();
		return null;
	}
	
	
//	public Object getCellValue(Cell cell)
//	{
//		if (cell==null)
//			return null;
//		if (cell.getCellType()==Cell.CELL_TYPE_BLANK)
//			return null;
//		//logger.debug("	cell type: "+cell.getCellType());
//		//cell.setCellType(Cell.CELL_TYPE_STRING); //hack?
//		switch(cell.getCellType())
//		{
//			case Cell.CELL_TYPE_BLANK:
//				return null;//"(blank)"; // hack! temporary
//			case Cell.CELL_TYPE_STRING:
//				return cell.getRichStringCellValue().getString();
//			case Cell.CELL_TYPE_NUMERIC:
//				return getNumericValue(cell);
//			case Cell.CELL_TYPE_BOOLEAN:
//				return cell.getBooleanCellValue();
//			case Cell.CELL_TYPE_FORMULA:
//				return getCachedFormulaValue(cell);
//			case Cell.CELL_TYPE_ERROR:
//				return cell.getErrorCellValue();
//			default:
//				//logger.debug("unhandled cell type: "+cell.getCellType());
//				writer.message("unhandled cell type: "+cell.getCellType());
//				return null;
//		}
//	}
//	
//	//http://stackoverflow.com/questions/7608511/java-poi-how-to-read-excel-cell-value-and-not-the-formula-computing-it
//	private Object getCachedFormulaValue(Cell cell)
//	{
//		try
//		{
//			if (cell.getCellType()!=Cell.CELL_TYPE_FORMULA)
//				throw new CException("expected formula cell type: "+cell.getCellType());
//			//logger.debug("Formula is " + cell.getCellFormula());
//			switch(cell.getCachedFormulaResultType())
//			{
//				case Cell.CELL_TYPE_NUMERIC:
//					return cell.getNumericCellValue();
//				case Cell.CELL_TYPE_STRING:
//					return cell.getRichStringCellValue();
//				case Cell.CELL_TYPE_ERROR:
//					return null;// hack - todo check this
//				default:
//					writer.message("unhandled cellValue type: "+cell.getCachedFormulaResultType());
//					return null;
//			}
//		}
//		catch (NotImplementedException e)
//		{
//			writer.message("NotImplementedException: ["+"="+cell.getCellFormula().toString()+"]");
//			throw e;
//			//return null;
//		}
//		catch (Exception e)
//		{
//			writer.message(e.toString());
//			writer.message("="+cell.getCellFormula().toString());
//			throw e;
//			//return null;
//		}
//	}
		
//	private Object getFormulaValue(Cell cell)
//	{
//		try
//		{
//			if (cell.getCellType()==Cell.CELL_TYPE_BLANK)
//				return null;
//			FormulaEvaluator evaluator = getFormulaEvaluator(cell);
//			CellValue cellValue = evaluator.evaluate(cell);
//			switch(cellValue.getCellType())
//			{
//				case Cell.CELL_TYPE_BLANK:
//					return null;//"(blank)"; // hack! temporary
//				case Cell.CELL_TYPE_STRING:
//					return cellValue.getStringValue();
//				case Cell.CELL_TYPE_NUMERIC:
//					return getNumericValue(cell, cellValue);
//				case Cell.CELL_TYPE_BOOLEAN:
//					return cellValue.getBooleanValue();				
//				case Cell.CELL_TYPE_ERROR:
//					return cellValue.formatAsString();
//				default:
//					writer.message("unhandled cellValue type: "+cellValue.getCellType());
//					return null;
//			}
//		}
//		catch (NotImplementedException e)
//		{
//			writer.message("NotImplementedException: ["+"="+cell.getCellFormula().toString()+"]");
//			return null;
//		}
//		catch (Exception e)
//		{
//			writer.message(e.toString());
//			writer.message("="+cell.getCellFormula().toString());
//			return null;
//		}
//	}
	
//	private Object getNumericValue(Cell cell)
//	{
//		if (DateUtil.isCellDateFormatted(cell))
//			return cell.getDateCellValue();
//		else return getNumericValueAsString(cell);
//		//else return cell.getNumericCellValue();
//	}
	
	private Object getNumericValue(Cell cell)
	{
		if (DateUtil.isCellDateFormatted(cell))
		{
			Date date=cell.getDateCellValue();
			if (date==null)
			{
				logger.debug("&*&*&*&*&*&*&*&*&*&*&*&*&*&");
				logger.debug("date cell is null: return as string: "+cell.toString()+" --> "+getNumericValueAsString(cell));
				logger.debug("&*&*&*&*&*&*&*&*&*&*&*&*&*&");
				return getNumericValueAsString(cell);
			}
			//return DateHelper.format(date, DateHelper.YYYYMMDD_PATTERN);// @TODO hack - make this configurable
			return date;
		}
		else return getNumericValueAsString(cell);
		//else return cell.getNumericCellValue();
	}
	
	private String getNumericValueAsString(Cell cell)
	{
		//DataFormatter formatter=new DataFormatter();
		return formatter.formatCellValue(cell);
	}
	
//	private Object getNumericValue(Cell cell, CellValue cellValue)
//	{
//		if (DateUtil.isCellDateFormatted(cell))
//			return DateUtil.getJavaDate(cellValue.getNumberValue());
//		else return cellValue.getNumberValue();
//	}

	public Workbook openWorkbook(String filename)
	{
		try
		{
			logger.debug("trying to open file: "+filename);
			FileHelper.checkExists(filename);
			//this.stream=new FileInputStream(new File(filename));
			//return WorkbookFactory.create(stream);
			return WorkbookFactory.create(new File(filename));
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}

	public Workbook openWorkbook(String filename, String password)
	{
		try
		{
			logger.debug("trying to open password-protected file: "+filename);
			FileHelper.checkExists(filename);
			return WorkbookFactory.create(new File(filename), password);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public void closeWorkbook(Workbook workbook)
	{
		try
		{
			if (workbook!=null)
				workbook.close();
//			if (this.stream!=null)
//			{
//				this.stream.close();
//				this.stream=null;
//			}
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
//	public Cell getCell(Sheet sheet, int rownum, int colnum)
//	{
//		Row row=sheet.getRow(rownum);
//		if (row==null)
//			return null;//throw new CException("row is null for rownum "+rownum+" in sheet "+sheet.getSheetName());
//		return row.getCell(colnum);
//	}	
	
	public Object getCellValue(Sheet sheet, int rownum, int colnum)
	{
		Row row=sheet.getRow(rownum);
		if (row==null)
			return null;//throw new CException("row is null for rownum "+rownum+" in sheet "+sheet.getSheetName());
		Cell cell=row.getCell(colnum);
		//if (cell==null)
			//throw new CException("cell is null for rownum "+rownum+" and colnum "+colnum+" in sheet "+sheet.getSheetName());
		return getCellValue(cell);
	}
	
	public Cell getCell(Sheet sheet, String address)
	{
		try
		{
			CellReference cellReference=new CellReference(address);
			Row row=sheet.getRow(cellReference.getRow());
			if (row==null)
				return null;
			Cell cell=row.getCell(cellReference.getCol());
			if (cell==null)
				return null;
			return cell;
		}
		catch (Exception e)
		{
			System.err.println("problem getting cell value for "+address+" in sheet "+sheet.getSheetName()+": "+e.getMessage());
			return null;
		}
	}
	
	public Object getCellValue(Sheet sheet, String address)
	{
		Cell cell=getCell(sheet, address);
		return getCellValue(cell);
	}
	
//	public Object getCellValue(Sheet sheet, String address)
//	{
//		try
//		{
//			CellReference cellReference=new CellReference(address);
//			Row row=sheet.getRow(cellReference.getRow());
//			if (row==null)
//				return null;
//			Cell cell=row.getCell(cellReference.getCol());
//			if (cell==null)
//				return null;
//			return getCellValue(cell);
//		}
//		catch (Exception e)
//		{
//			System.err.println("problem getting cell value for "+address+" in sheet "+sheet.getSheetName()+": "+e.getMessage());
//			return null;
//		}
//	}
	
	public String getStringCellValue(Sheet sheet, String address)
	{
		Object value=getCellValue(sheet, address);
		if (value==null)
			return "";
		return value.toString();
	}
	
	public String getStringCellValue(Cell cell)
	{
		Object value=getCellValue(cell);
		if (value==null)
			return "";
		return value.toString();
	}
	
	public static String getComment(Cell cell)
	{
		Comment comment=cell.getCellComment();
		if (comment==null)
			return null;
		return comment.getString().getString();
	}
	
	////////////////////////////////////////////////////////////////
	
	public CellReference getCellReference(Cell cell)
	{
		return new CellReference(cell.getRowIndex(), cell.getColumnIndex());
	}
	
	public CellReference getFirstCellReference(CellRangeAddress address)
	{
		return new CellReference(address.getFirstRow(), address.getFirstColumn());
	}

	public CellReference getLastCellReference(CellRangeAddress address)
	{
		return new CellReference(address.getLastRow(), address.getLastColumn());
	}
	
	/////////////////////////////////////////////////////////////
	
//	private FormulaEvaluator getFormulaEvaluator(Cell cell)
//	{
//		Sheet sheet=cell.getSheet();
//		Workbook workbook=sheet.getWorkbook();
//		return workbook.getCreationHelper().createFormulaEvaluator();
//	}
	
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	private CellStyle headerStyle=null;
	
	public Workbook createXlsWorkbook()
	{
		return new HSSFWorkbook();
	}
	
	public Workbook createXlsxWorkbook()
	{
		return new XSSFWorkbook();
	}
	
	public Workbook createWorkbook(String filename)
	{
		Workbook workbook=null;
		if (filename.endsWith(".xls"))
			workbook=createXlsWorkbook();
		else if (filename.endsWith(".xlsx"))
			workbook=createXlsxWorkbook();
		else throw new CException("unhandled Excel file extension");
		//writeWorkbook(workbook, filename);
		return workbook;
	}
	
	public void writeWorkbook(Workbook workbook, String filename)
	{
		writeWorkbook(workbook, filename, new MessageWriter());
	}
	
	public void writeWorkbook(Workbook workbook, String filename, MessageWriter messages)
	{
		FileOutputStream out=null;
		try
		{
			messages.println("generating excel file: "+filename);
			out = new FileOutputStream(filename);
			workbook.write(out);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
		finally
		{
			FileHelper.closeStream(out);
		}
	}
	
	public Sheet createWorksheet(Workbook workbook, DataFrame<?> dataframe, String sheetname)
	{
		CTable table=dataframe.getTable();
		return createWorksheet(workbook, table, sheetname);
	}
	
	public Sheet createWorksheet(Workbook workbook, CTable table, String sheetname)
	{		
		Sheet sheet=workbook.createSheet(sheetname);
		int row=0, col=0;
		appendTable(sheet, table, row, col);
		return sheet;
	}
	
	public void appendTable(Sheet sheet, CTable table, int r, int c)
	{
		for (CTable.Cell cell : table.getHeader().getCells())
		{
			setHeaderCell(sheet, c++, r, cell.getStringValue());
		}

		for (CTable.Row tablerow : table.getRows())
		{
			c=0;
			r++;
			for (CTable.Cell cell : tablerow.getCells())
			{
				setCell(sheet, c++, r, cell.getValue());
			}			
		}
	}
	
	public void setHeaderCell(Sheet sheet, int c, int r, String value)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
		cell.setCellStyle(getHeaderCellStyle(sheet.getWorkbook()));
	}
	
	public void setCell(Sheet sheet, int c, int r, String value)
	{
		if (value==null)
			return;
		//logger.debug("adding cell at c="+c+", r="+r+", value="+value);
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
	}
	
	public void setCell(Sheet sheet, int c, int r, Integer value)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
	}
	
	public void setCell(Sheet sheet, int c, int r, Double value)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
	}
	
	public void setCell(Sheet sheet, int c, int r, Double value, CellStyle style)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}
	
	public void setCell(Sheet sheet, int c, int r, Float value)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
	}
	
	public void setCell(Sheet sheet, int c, int r, Float value, CellStyle style)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}
	
//	http://stackoverflow.com/questions/5794659/poi-how-do-i-set-cell-value-to-date-and-apply-default-excel-date-format
//	CellStyle cellStyle = wb.createCellStyle();
//	cellStyle.setDataFormat(
//		createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
//	cell = row.createCell(1);
//	cell.setCellValue(new LocalDate());
//	cell.setCellStyle(cellStyle);
	
	public void setCell(Sheet sheet, int c, int r, LocalDate value, String format)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(LocalDateHelper.format(value, format));
	}
	
	public void setCell(Sheet sheet, int c, int r, LocalDate value, String format, CellStyle style)
	{
		if (value==null)
			return;
		Cell cell=getCell(sheet, c, r);
		cell.setCellValue(LocalDateHelper.format(value, format));
		cell.setCellStyle(style);
	}
	
	public Cell setCell(Sheet sheet, String address, Object value, CellStyle style)
	{
		Cell cell=setCell(sheet, address, value);
		cell.setCellStyle(style);
		return cell;
	}
	
	public Cell setCell(Sheet sheet, String address, Object value)
	{
		if (address.contains(":"))
			return addRange(sheet, address, value);
		CellReference cellref = new CellReference(address);
		return setCell(sheet, cellref.getCol(), cellref.getRow(), value);
	}
	
	public Cell addRange(Sheet sheet, String range, Object value)
	{
		String cell1=range.split(":")[0];
		String cell2=range.split(":")[1];
		CellReference cellref1 = new CellReference(cell1);
		//logger.debug("range="+range+", cellref1="+cellref1.formatAsString());
		Cell cell=getCell(sheet, cellref1.getCol(), cellref1.getRow());
		//Row row = sheet.getRow(cellref1.getRow());
		//Cell cell = row.getCell(cellref1.getCol());
		setCellValue(cell, value);

		CellReference cellref2 = new CellReference(cell2);
		sheet.addMergedRegion(new CellRangeAddress(
				cellref1.getRow(), //first row (0-based)
				cellref2.getRow(), //last row  (0-based)
				cellref1.getCol(), //first column (0-based)
				cellref2.getCol()  //last column  (0-based)
		));
		return cell;
	}
	
	public Cell setCell(Sheet sheet, int c, int r, Object value, CellStyle style)
	{
		Cell cell=setCell(sheet, c, r, value);
		cell.setCellStyle(style);
		return cell;
	}
	
	public Cell setCell(Sheet sheet, int c, int r, Object value)
	{
		if (value==null)
			return null;
		Cell cell=getCell(sheet, c, r);
		setCellValue(cell, value);
		return cell;
	}
	
	public void setCellValue(Cell cell, Object value)
	{
		DataType type=DataType.guessDataTypeByClass(value);
		switch(type)
		{
		case BOOLEAN:
			cell.setCellValue((Boolean)value);
			return;
		case DATE:
			cell.setCellValue(LocalDateHelper.format((LocalDate)value, LocalDateHelper.DATE_PATTERN));
			return;
		case INTEGER:
			cell.setCellValue((Integer)value);
			return;
		case FLOAT:
			if (value instanceof Float)
				cell.setCellValue((Float)value);
			else cell.setCellValue((Double)value);
			return;
		default:
			setStringCellValue(cell, value);
			return;
		}
	}

	public void setStringCellValue(Cell cell, Object value)
	{
		DataType type=DataType.guessDataType(value);
		switch(type)
		{
		case BOOLEAN:
			cell.setCellValue(Boolean.valueOf(value.toString()));
			return;
		case DATE:
			cell.setCellValue(DateHelper.parse(value.toString(), LocalDateHelper.DATE_PATTERN));
			return;
		case INTEGER:
			cell.setCellValue(Integer.valueOf(value.toString().trim()));
			return;
		case FLOAT:
			cell.setCellValue(Float.valueOf(value.toString().trim()));
			return;
		default:
			cell.setCellValue(value.toString());
			return;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	public Cell getCell(Sheet sheet, int c, int r)
	{
		//logger.debug("creating cell at c="+c+", r="+r);
		Row row=sheet.getRow(r);
		if (row==null)
			row=sheet.createRow(r);
		Cell cell=row.getCell(c);
		if (cell==null)
			cell=row.createCell(c);
		return cell;
	}
	
	/*
	@SuppressWarnings("unused")
	private CellStyle createDecimalFormat(int dps)
	{
		String pattern="#."+StringHelper.repeatString("#", dps);
		return createNumberFormat(pattern);
	}
	
	private CellStyle createNumberFormat(String pattern)
	{
		return new WritableCellFormat(new NumberFormat(pattern));
	}
	*/
	
	private CellStyle getHeaderCellStyle(Workbook workbook)
	{
		if (headerStyle==null)
		{
			headerStyle=workbook.createCellStyle();
			int fontSize=10;
			Font font=workbook.createFont();
			font.setFontHeightInPoints((short)fontSize); //NOPMD
			font.setBold(true);
			//font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			headerStyle.setFont(font);
			headerStyle.setBorderBottom(BorderStyle.MEDIUM);
		}
		return headerStyle;
	}
	
	public CellStyle createBorderedCellStyle(Workbook workbook)
	{
		CellStyle style=workbook.createCellStyle();
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
//		style.setBorderTop(CellStyle.BORDER_MEDIUM);
//		style.setBorderBottom(CellStyle.BORDER_MEDIUM);
//		style.setBorderLeft(CellStyle.BORDER_MEDIUM);
//		style.setBorderRight(CellStyle.BORDER_MEDIUM);
		return style;
	}
	
	public void setCellComment(Cell cell, String text)
	{
		CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();		
		Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
		ClientAnchor anchor = factory.createClientAnchor();
		Comment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString(text);
		comment.setString(str);
		comment.setAuthor("Chayama");
		//assign the comment to the cell
		cell.setCellComment(comment);
	}
	
	public String getAddress(Cell cell)
	{
		CellReference cellref = new CellReference(cell);
		return cellref.formatAsString();
	}
	
	public boolean hasContent(Cell cell)
	{
		Object value=getCellValue(cell);
		//if (value!=null && value.toString().equals("1"))
		//	logger.debug("has content ("+value+"): "+cell.getCellType());
		return (value!=null && !value.toString().trim().equals(""));
	}
	
//	//http://poi.apache.org/spreadsheet/quick-guide.html#NamedRanges
//	public void getNamedRanges(Workbook workbook)
//	{
//		//logger.debug("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
//		for (int index=0; index<workbook.getNumberOfNames(); index++)
//		{
//			getNamedRange(workbook, index);
//		}
//		//logger.debug("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//	}
//	
//	public void getNamedRange(Workbook workbook, String name)
//	{
//		int index=workbook.getNameIndex(name);
//		getNamedRange(workbook, index);
//	}
//	
//	public void getNamedRange(Workbook workbook, Integer index)
//	{
//		Name namedRange=workbook.getNameAt(index);
//		if (namedRange.isDeleted())
//			return;		
////		logger.debug("named range: "+StringHelper.toString(namedRange));
////		AreaReference[] arefs = AreaReference.generateContiguous(namedRange.getRefersToFormula());
////		for (int i=0; i<arefs.length; i++)
////		{
////			//Only get the corners of the Area
////			// (use arefs[i].getAllReferencedCells() to get all cells)
////			AreaReference aref=arefs[i];
////			logger.debug("arefs["+i+"]: "+StringHelper.toString(aref));
////			CellReference[] crefs = aref.getAllReferencedCells();
////			for (int j=0; j<crefs.length; j++)
////			{
////				CellReference cref=crefs[j];
////				logger.debug("crefs["+j+"]: "+StringHelper.toString(cref));
////				Sheet sheet = workbook.getSheet(cref.getSheetName());
////				Row row = sheet.getRow(cref.getRow());
////				if (row==null)
////					continue;
////				Cell cell = row.getCell(cref.getCol());
////				if (cell==null)
////					continue;
////				logger.debug(getStringCellValue(cell));
////				// Do something with this corner cell
////			}
////		}
//	}
//	
//	///////////////////////////////////////////////////////////////////////
//	
//	public void getShapes(XSSFSheet sheet)
//	{
////		List(XSSFShape shape : sheet.getDrawingPatriarch().getShapes())
////		{
////			
////		}
//	}
	
	//////////////////////////////////////////////////////////////////////
	
	// copy sheets
	//http://www.coderanch.com/t/420958/open-source/Copying-sheet-excel-file-another
	
	public void passwordProtect(Sheet sheet, String envvar)
	{
		String password=System.getenv(envvar);
		if (StringHelper.hasContent(password))
			sheet.protectSheet(password);
	}
	
	//http://www.coderanch.com/t/420958/open-source/Copying-sheet-excel-file-another
	/** 
	* @param newSheet the sheet to create from the copy. 
	* @param sheet the sheet to copy. 
	*/  
	public void copySheet(Sheet newSheet, Sheet sheet)
	{	
		int maxColumnNum = 0;
		//logger.debug("getFirstRowNum="+sheet.getFirstRowNum()+", getLastRowNum="+sheet.getLastRowNum());
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++)
		{
			//logger.debug("i="+i);
			Row srcRow = sheet.getRow(i);
			Row destRow = newSheet.createRow(i);
			if (srcRow != null)
			{
				copyRow(sheet, newSheet, srcRow, destRow);//styleMap
				if (srcRow.getLastCellNum() > maxColumnNum)	
					maxColumnNum = srcRow.getLastCellNum();
			}
		}
		for (int i = 0; i <= maxColumnNum; i++)
		{
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}
	
	/** 
	* @param srcSheet the sheet to copy. 
	* @param destSheet the sheet to create. 
	* @param srcRow the row to copy. 
	* @param destRow the row to create. 
	* @param styleMap - 
	*/  
	public void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow)//Map<Integer, CellStyle> styleMap)
	{	
		if (srcRow.getFirstCellNum()<0 || srcRow.getFirstCellNum()<0)
			return;
		// manage a list of merged zone in order to not insert two times a merged zone  
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		// pour chaque row
		//logger.debug("getFirstCellNum="+srcRow.getFirstCellNum()+", getLastCellNum="+srcRow.getLastCellNum());
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++)
		{	
			//logger.debug("j="+j);
			Cell oldCell = srcRow.getCell(j); // ancienne cell  
			Cell newCell = destRow.getCell(j); // new cell   
			if (oldCell != null)
			{	
				if (newCell == null)
					newCell = destRow.createCell(j);
				// copy chaque cell  
				copyCell(oldCell, newCell);
				// copy les informations de fusion entre les cellules  
				//logger.debug("row num: " + srcRow.getRowNum() + " , col: " + (short)oldCell.getColumnIndex());  
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), oldCell.getColumnIndex());
				if (mergedRegion != null)
				{
					//logger.debug("Selected merged region: " + mergedRegion.toString());  
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(), mergedRegion.getLastRow(), mergedRegion.getFirstColumn(),  mergedRegion.getLastColumn());  
					//logger.debug("New merged region: " + newMergedRegion.toString());  
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions))
					{
						mergedRegions.add(wrapper);
						destSheet.addMergedRegion(wrapper.range);
					}
				}	
			}	
		}
	}	

	@SuppressWarnings("deprecation")
	public void copyCell(Cell oldCell, Cell newCell)
	{
		if (styleList != null)
		{
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook())
			{
				newCell.setCellStyle(oldCell.getCellStyle());
			}
			else
			{
				DataFormat newDataFormat = newCell.getSheet().getWorkbook().createDataFormat();		
				CellStyle newCellStyle = getSameCellStyle(oldCell, newCell);
				if (newCellStyle == null)
				{
					//Create a new cell style
					Font oldFont = oldCell.getSheet().getWorkbook().getFontAt(oldCell.getCellStyle().getFontIndexAsInt());
					//Find a existing font corresponding to avoid to create a new one 
					//Font newFont = newCell.getSheet().getWorkbook().findFont(oldFont.getBoldweight(), oldFont.getColor(), oldFont.getFontHeight(), oldFont.getFontName(), oldFont.getItalic(), oldFont.getStrikeout(), oldFont.getTypeOffset(), oldFont.getUnderline());
					Font newFont = newCell.getSheet().getWorkbook().findFont(oldFont.getBold(), oldFont.getColor(), oldFont.getFontHeight(), oldFont.getFontName(), oldFont.getItalic(), oldFont.getStrikeout(), oldFont.getTypeOffset(), oldFont.getUnderline());
					if (newFont == null)
					{
						newFont = newCell.getSheet().getWorkbook().createFont();
						//newFont.setBoldweight(oldFont.getBoldweight());
						newFont.setBold(true);
						newFont.setColor(oldFont.getColor());
						newFont.setFontHeight(oldFont.getFontHeight());
						newFont.setFontName(oldFont.getFontName());
						newFont.setItalic(oldFont.getItalic());
						newFont.setStrikeout(oldFont.getStrikeout());
						newFont.setTypeOffset(oldFont.getTypeOffset());
						newFont.setUnderline(oldFont.getUnderline());
						newFont.setCharSet(oldFont.getCharSet());
					}
		
					short newFormat = newDataFormat.getFormat(oldCell.getCellStyle().getDataFormatString());//NOPMD
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.setFont(newFont);
					newCellStyle.setDataFormat(newFormat);
					
					newCellStyle.setAlignment(oldCell.getCellStyle().getAlignment());
					newCellStyle.setHidden(oldCell.getCellStyle().getHidden());
					newCellStyle.setLocked(oldCell.getCellStyle().getLocked());
					newCellStyle.setWrapText(oldCell.getCellStyle().getWrapText());
					newCellStyle.setBorderBottom(oldCell.getCellStyle().getBorderBottom());
					newCellStyle.setBorderLeft(oldCell.getCellStyle().getBorderLeft());
					newCellStyle.setBorderRight(oldCell.getCellStyle().getBorderRight());
					newCellStyle.setBorderTop(oldCell.getCellStyle().getBorderTop());
					newCellStyle.setBottomBorderColor(oldCell.getCellStyle().getBottomBorderColor());
					newCellStyle.setFillBackgroundColor(oldCell.getCellStyle().getFillBackgroundColor());
					newCellStyle.setFillForegroundColor(oldCell.getCellStyle().getFillForegroundColor());
					newCellStyle.setFillPattern(oldCell.getCellStyle().getFillPattern());
					newCellStyle.setIndention(oldCell.getCellStyle().getIndention());
					newCellStyle.setLeftBorderColor(oldCell.getCellStyle().getLeftBorderColor());
					newCellStyle.setRightBorderColor(oldCell.getCellStyle().getRightBorderColor());
					newCellStyle.setRotation(oldCell.getCellStyle().getRotation());
					newCellStyle.setTopBorderColor(oldCell.getCellStyle().getTopBorderColor());
					newCellStyle.setVerticalAlignment(oldCell.getCellStyle().getVerticalAlignment());
	
					styleList.add(newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch (oldCell.getCellType())
		{
			case STRING:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case NUMERIC:
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case BLANK:
				newCell.setCellType(CellType.BLANK);
				break;
			case BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case ERROR:
				newCell.setCellErrorValue(oldCell.getErrorCellValue());
				break;
			case FORMULA:
				newCell.setCellFormula(oldCell.getCellFormula());
				break;
			default:
				break;
		}
	}
	
	//@SuppressWarnings("deprecation")
	private CellStyle getSameCellStyle(Cell oldCell, Cell newCell)
	{
		CellStyle styleToFind = oldCell.getCellStyle();
		CellStyle currentCellStyle=null;
		CellStyle returnCellStyle=null;
		Iterator<CellStyle> iterator = styleList.iterator();
		Font oldFont = null;
		Font newFont = null;
		while (iterator.hasNext() && returnCellStyle == null)
		{
			currentCellStyle = iterator.next();	
			if (currentCellStyle.getAlignment() != styleToFind.getAlignment())
				continue;
			if (currentCellStyle.getHidden() != styleToFind.getHidden())
				continue;
			if (currentCellStyle.getLocked() != styleToFind.getLocked())
				continue;
			if (currentCellStyle.getWrapText() != styleToFind.getWrapText())
				continue;
			if (currentCellStyle.getBorderBottom() != styleToFind.getBorderBottom())
				continue;
			if (currentCellStyle.getBorderLeft() != styleToFind.getBorderLeft())
				continue;
			if (currentCellStyle.getBorderRight() != styleToFind.getBorderRight())
				continue;
			if (currentCellStyle.getBorderTop() != styleToFind.getBorderTop())
				continue;
			if (currentCellStyle.getBottomBorderColor() != styleToFind.getBottomBorderColor())
				continue;
			if (currentCellStyle.getFillBackgroundColor() != styleToFind.getFillBackgroundColor())
				continue;
			if (currentCellStyle.getFillForegroundColor() != styleToFind.getFillForegroundColor())
				continue;
			if (currentCellStyle.getFillPattern() != styleToFind.getFillPattern())
				continue;
			if (currentCellStyle.getIndention() != styleToFind.getIndention())
				continue;
			if (currentCellStyle.getLeftBorderColor() != styleToFind.getLeftBorderColor())
				continue;
			if (currentCellStyle.getRightBorderColor() != styleToFind.getRightBorderColor())
				continue;
			if (currentCellStyle.getRotation() != styleToFind.getRotation())
				continue;
			if (currentCellStyle.getTopBorderColor() != styleToFind.getTopBorderColor())
				continue;
			if (currentCellStyle.getVerticalAlignment() != styleToFind.getVerticalAlignment())
				continue;
	
			oldFont = oldCell.getSheet().getWorkbook().getFontAt(oldCell.getCellStyle().getFontIndexAsInt());
			newFont = newCell.getSheet().getWorkbook().getFontAt(currentCellStyle.getFontIndexAsInt());
	
			if (newFont.getBold() == oldFont.getBold())
				continue;
			if (newFont.getColor() == oldFont.getColor())
				continue;
			if (newFont.getFontHeight() == oldFont.getFontHeight())
				continue;
			if (newFont.getFontName() == oldFont.getFontName())
				continue;
			if (newFont.getItalic() == oldFont.getItalic())
				continue;
			if (newFont.getStrikeout() == oldFont.getStrikeout())
				continue;
			if (newFont.getTypeOffset() == oldFont.getTypeOffset())
				continue;
			if (newFont.getUnderline() == oldFont.getUnderline())
				continue;
			if (newFont.getCharSet() == oldFont.getCharSet())
				continue;
			if (oldCell.getCellStyle().getDataFormatString().equals(currentCellStyle.getDataFormatString()))
				continue;
			returnCellStyle = currentCellStyle;
		}
		return returnCellStyle;
	}

	////////////////////////////////////////////////////
	
	public static Float getRowHeightInPixels(Row row)
	{
		if (row==null)
			return null;
		return points2pixels(row.getHeightInPoints());
	}
	
	//http://reeddesign.co.uk/test/points-pixels.html
	//http://www.endmemo.com/sconvert/pixelpoint.php
	//1 px = 0.75 point; 1 point = 1.333333 px
	public static Float points2pixels(Float points)
	{
		return points*0.75f;
	}
	
	public static XSSFCellStyle getStyle(Cell cell)
	{
		return (XSSFCellStyle)cell.getCellStyle();
	}
	
	public static XSSFFont getFont(Cell cell)
	{
		XSSFCellStyle style=getStyle(cell);
		return style.getFont();
	}
	
//	public static String getFontColor(XSSFFont font)
//	{
//		short fontColor=font.getColor();
//		if (fontColor==0 || fontColor==IndexedColors.BLACK.index)
//			return null;
//		else return ExcelHelper.getIndexedColor(fontColor);		
//	}
	
	public static String getFontColor(XSSFFont font)
	{
		XSSFColor color=font.getXSSFColor();
		return getRgbColor(color);
	}
	
//	public static String getFontColor(Cell cell)
//	{
//		XSSFFont font=getFont(cell);
//		if (font==null)
//			return null;
//		return getFontColor(font);
//	}
	
	public String getFontColor(Cell cell)
	{
		XSSFFont font=getFont(cell);
		return getFontColor(font);
	}
	
//	public String getFontColor(Cell cell)
//	{
//		XSSFCellStyle style=getStyle(cell);
//		XSSFFont font=style.getFont();
//		XSSFColor color=font.getXSSFColor();
//		return getRgbColor(color);
//	}
	
	public static HorizontalAlignment getHorizontalAlignment(Cell cell)
	{
		XSSFCellStyle style=getStyle(cell);
		return style.getAlignment();
	}
	
	public static VerticalAlignment getVerticalAlignment(Cell cell)
	{
		XSSFCellStyle style=getStyle(cell);
		return style.getVerticalAlignment();
	}
	
//	public static String getBgColor(Cell cell)
//	{
//		XSSFCellStyle style=getStyle(cell);
//		XSSFColor fgcolor=style.getFillForegroundXSSFColor();
//		if (fgcolor==null)
//			return null;
//		return ExcelHelper.getRgbColor(fgcolor);
//	}
	
//	public static String getBgColor(Cell cell)
//	{
//		XSSFColorConverter converter=new XSSFColorConverter((XSSFWorkbook)cell.getSheet().getWorkbook());
//		XSSFCellStyle style=ExcelHelper.getStyle(cell);
//		XSSFColor fgcolor=style.getFillForegroundXSSFColor();
//		String color=converter.styleColor(fgcolor);
//		if (StringHelper.hasContent(color) && color.endsWith(";"))
//			color=StringHelper.chomp(color);
//		return color;
//	}
	
	//http://www.w3schools.com/colors/colors_converter.asp
	//http://www.w3schools.com/css/css3_colors.asp
	public static String getRgbColor(XSSFColor color)
	{
		if (color==null || color.getARGBHex()==null)
			return "black";
		//String rgb="#"+color.getARGBHex().substring(2);//0, 6
		//logger.debug("color="+StringHelper.toString(color)+" rgb="+rgb);
		//return rgb;
		//System.out.println("color="+StringHelper.toString(color)+"  indexed="+color.getIndexed()
		//		+" tint="+color.getTint()+" rgbhex="+color.getARGBHex()+", rgbwithTint"+color.getRGBWithTint());
		byte[] rgb=color.getRGBWithTint();
		if (rgb==null)
			return "black";
		List<Integer> parts=Lists.newArrayList();
		for (int index=0; index<rgb.length; index++)
		{
			int part=rgb[index];
			if (part<0)
				part=255+part;
			parts.add(part);
			//System.out.println("rgb["+index+"]="+rgb[index]+" part="+part);
		}
		String rgba="rgba("+StringHelper.join(parts, ",")+","+MathHelper.format((float)color.getTint(), 2)+")";
		//System.out.println("rgb with tint="+rgba);
		return rgba;
	}
	
	//EEECE1 rgb(238, 236, 225)
//	rgbh[1]=-18 
//	rgbh[2]=-20
//	rgbh[3]=-31
	public static String getIndexedColor(short index)
	{
		//IndexedColors color=findIndexedColor(index);
		PaletteRecord palette=new PaletteRecord();
		byte[] b=palette.getColor(index);
		//logger.debug(b);
		//logger.debug("color="+color.name()+" index="+index+", r="+b[0]+" g="+b[1]+" b="+b[2]);
		String rgb="rgb("+Math.abs(b[0])+", "+Math.abs(b[1])+", "+Math.abs(b[2])+")";
		//logger.debug("rgb="+rgb);
		return rgb;
	}
	
//	public static IndexedColors findIndexedColor(short index)
//	{
//		if (index==0)
//			return IndexedColors.BLACK;
//		for (IndexedColors color : IndexedColors.values())
//		{
//			if (color.index==index)
//				return color;
//		}
//		throw new CException("cannot find indexcolor: "+index);
//	}
	
	public static String getBorderStyle(Cell cell, BorderSide side)
	{
		XSSFCellStyle style=(XSSFCellStyle)cell.getCellStyle();
		XSSFColor color=style.getBorderColor(side);
		//logger.debug("style="+style.name());
		switch(getBorder(cell, side))
		{
		case NONE:
			//return getBorderStyle("none", 1, color);
			//return "1px solid red";
			return null;
		case THIN:
			return getBorderStyle("solid", 1, color);
		case MEDIUM:
			return getBorderStyle("solid", 2, color);
		case DASHED:
			return getBorderStyle("dashed", 1, color);
		case DOTTED:
			return getBorderStyle("dotted", 1, color);
		case THICK:
			return getBorderStyle("solid", 3, color);
		case DOUBLE:
			return getBorderStyle("double", 1, color);
		case HAIR:
			return getBorderStyle("solid", 1, color);
		case MEDIUM_DASHED:
			return getBorderStyle("dashed", 2, color);
		case DASH_DOT:
			return getBorderStyle("dashed", 1, color);
		case MEDIUM_DASH_DOT:
			return getBorderStyle("dashed", 2, color);
		case DASH_DOT_DOT:
			return getBorderStyle("dashed", 1, color);
		case MEDIUM_DASH_DOT_DOT:
			return getBorderStyle("dashed", 2, color);
		//case MEDIUM_DASH_DOT_DOTC:
		//	return getBorderStyle("dashed", 2, color);
		case SLANTED_DASH_DOT:
			return getBorderStyle("dashed", 1, color);		
		default:
			throw new CException("no handle for border style: "+style); 
		}
	}
	
	/////////////////////////////////////
	
	public static BorderStyle getBorder(Cell cell, BorderSide side)
	{
		XSSFCellStyle style=(XSSFCellStyle)cell.getCellStyle();
		switch(side)
		{
		case BOTTOM:
			return style.getBorderBottom();
		case TOP:
			return style.getBorderTop();
		case LEFT:
			return style.getBorderLeft();
		case RIGHT:
			return style.getBorderRight();
		}
		throw new CException("unhandled border side: "+side);
	}
	
//	public static BorderStyle getBorder(Cell cell, BorderSide side)
//	{
//		XSSFCellStyle style=(XSSFCellStyle)cell.getCellStyle();
//		switch(side)
//		{
//		case BOTTOM:
//			return style.getBorderBottom();
//		case TOP:
//			return style.getBorderTop();
//		case LEFT:
//			return style.getBorderLeft();
//		case RIGHT:
//			return style.getBorderRight();
//		}
//		throw new CException("unhandled border side: "+side);
//	}
	
	public static Integer getBorderWidth(BorderStyle border)
	{
		switch(border)
		{
		case NONE:
			return 0;
		case THIN:
		case HAIR:
		case DASHED:
		case DOTTED:
		case DOUBLE:
		case DASH_DOT:
		case DASH_DOT_DOT:
		case SLANTED_DASH_DOT:
			return 1;
		case MEDIUM:
		case MEDIUM_DASHED:
		case MEDIUM_DASH_DOT:
		case MEDIUM_DASH_DOT_DOT:
		//case MEDIUM_DASH_DOT_DOTC:
			return 2;
		case THICK:
			return 3;
		default:
			throw new CException("no handle for border style: "+border.name());
		}
	}
	
	public static String getBorderColor(Cell cell, BorderSide side)
	{
		XSSFCellStyle style=(XSSFCellStyle)cell.getCellStyle();
		XSSFColor color=style.getBorderColor(side);
		return getRgbColor(color);
	}
	
	public static String getBorderStyle(BorderStyle border)
	{
		switch(border)
		{
		case NONE:
			return null;
		case THIN:
		case MEDIUM:
		case THICK:
		case HAIR:	
			return "solid";
		case DASHED:
		case MEDIUM_DASHED:
		case DASH_DOT:
		case MEDIUM_DASH_DOT:
		case DASH_DOT_DOT:
		case MEDIUM_DASH_DOT_DOT:
		//case MEDIUM_DASH_DOT_DOTC:
		case SLANTED_DASH_DOT:
			return "dashed";
		case DOTTED:
			return "dotted";
		case DOUBLE:
			return "double";
		default:
			throw new CException("no handle for border style: "+border); 
		}
	}
	
	public static String getBorderStyle(String style, int width, XSSFColor color)
	{
		return width+"px "+style+" "+getRgbColor(color);
	}
	
	public boolean hasBorder(Cell cell, BorderSide side)
	{
		BorderStyle border=getBorder(cell, side);
		return border != BorderStyle.NONE;
	}
	
	public boolean hasBorder(Cell cell)
	{
		return hasBorder(cell, BorderSide.TOP);
	}
	
	//https://mail-archives.apache.org/mod_mbox/poi-user/201508.mbox/%3CCABdJj55RZTOP+gQgzNwU_HXKmuKCvUk01qEQ+y_7ntnsyvJHrw@mail.gmail.com%3E
	//http://apache-poi.1045710.n5.nabble.com/Diagonal-border-td5720338.html
	//https://stackoverflow.com/questions/39529042/apache-poi-how-to-add-diagonal-border
	public boolean hasDiagonalBorder(Cell cell)
	{
		StylesTable stylesTable = getStylesTable(cell.getSheet().getWorkbook());
		XSSFCellStyle style = (XSSFCellStyle) (cell.getCellStyle());
		CTXf cxf = style.getCoreXf();
		long borderIndex = cxf.getBorderId();
		CTBorder ctBorder = stylesTable.getBorderAt((int)borderIndex).getCTBorder();
		boolean up = ctBorder.getDiagonalUp();
		boolean down = ctBorder.getDiagonalDown();
		return (up || down);
	}
	
	private StylesTable stylesTable = null;
	
	private StylesTable getStylesTable(Workbook workbook)
	{
		if (stylesTable!=null)
			return stylesTable;
		for (POIXMLDocumentPart part : ((XSSFWorkbook)workbook).getRelations())
		{ 
			if (part instanceof StylesTable)
			{
				stylesTable = (StylesTable) part;
				break;
			 }
		}  
		 if (stylesTable == null) 
			 throw new RuntimeException("Could not find styles table");
		 return stylesTable;
	}
	
	public static boolean hasFillColor(Cell cell, XSSFColor color)
	{
		if (cell==null || cell.getCellStyle()==null || cell.getCellStyle().getFillForegroundColorColor()==null)
			return false;
		XSSFColor fgcolor=(XSSFColor)cell.getCellStyle().getFillForegroundColorColor();
		return (fgcolor.getARGBHex().equals(color.getARGBHex()));
	}
	
	////////////////////////////////////////////////////
	
	/** 
	* Récupère les informations de fusion des cellules dans la sheet source pour les appliquer 
	* à la sheet destination... 
	* Récupère toutes les zones merged dans la sheet source et regarde pour chacune d'elle si 
	* elle se trouve dans la current row que nous traitons. 
	* Si oui, retourne l'objet CellRangeAddress. 
	*  
	* @param sheet the sheet containing the data. 
	* @param rowNum the num of the row to copy. 
	* @param cellNum the num of the cell to copy. 
	* @return the CellRangeAddress created. 
	*/  
	public static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, int cellNum)
	{	
		for (int i = 0; i < sheet.getNumMergedRegions(); i++)
		{
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum))
				return merged;
		}	
		return null;
	}	
	
	/** 
	* Check that the merged region has been created in the destination sheet. 
	* @param newMergedRegion the merged region to copy or not in the destination sheet. 
	* @param mergedRegions the list containing all the merged region. 
	* @return true if the merged region is already in the list or not. 
	*/  
	private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion, Set<CellRangeAddressWrapper> mergedRegions)
	{  
		return !mergedRegions.contains(newMergedRegion);
	}
	
	public static class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {
	
		public CellRangeAddress range;
		
		/**
		* @param theRange the CellRangeAddress object to wrap.
		*/
		public CellRangeAddressWrapper(CellRangeAddress theRange)
		{
				this.range = theRange;
		}
		
		/**
		* @param o the object to compare.
		* @return -1 the current instance is prior to the object in parameter, 0: equal, 1: after...
		*/
		public int compareTo(CellRangeAddressWrapper o)
		{
			if (range.getFirstColumn() < o.range.getFirstColumn() && range.getFirstRow() < o.range.getFirstRow())
				return -1;
			else if (range.getFirstColumn() == o.range.getFirstColumn() && range.getFirstRow() == o.range.getFirstRow())
				return 0;
			else return 1;
		}	
	}
	
	//http://alandix.com/code/apache-poi-detect-1904-date-option/
//	public static class SsCheck1904
//	{
//		public static boolean isDate1904(Workbook wb)
//		{
//			Sheet sheet = wb.createSheet();
//			int sheetIndex = wb.getSheetIndex(sheet);
//			Row row = sheet.createRow(0);
//			Cell cell = row.createCell(0);
//			cell.setCellValue(0.0);
//			boolean is1994 = isDate1904(cell);
//			wb.removeSheetAt(sheetIndex);
//			return is1994;
//		}
//		 
//		/**
//		 * throws an exception for non-numeric cells
//		 */
//		public static boolean isDate1904(Cell cell)
//		{
//			double value = cell.getNumericCellValue();
//			Date date = cell.getDateCellValue();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(date);
//			long year1900 = cal.get(Calendar.YEAR)-1900;
//			long yearEst1900 = Math.round(value/(365.25));
//			return year1900 > yearEst1900;
//		}
//		
//		public static boolean isDate1904(Sheet sheet)
//		{
//			try
//			{
//				Row row = sheet.createRow(sheet.getLastRowNum() + 1);
//				Cell cell = row.createCell(0);
//				cell.setCellValue(0.0);
//				boolean is1904StartDate = isDate1904(cell);
//				sheet.removeRow(row);
//				return is1904StartDate;
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				return false;
//			}
//		}
//	}
	
	public static LocalDate parseExcelDate(double value)
	{
		//logger.debug("trying to parse excel date: "+value+" --> "+LocalDateUtil.getJavaLocalDate(value));
		return LocalDateHelper.asLocalDate(DateUtil.getJavaDate(value));
	}
}

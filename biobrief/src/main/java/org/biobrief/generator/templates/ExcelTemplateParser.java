package org.biobrief.generator.templates;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.biobrief.util.ExcelHelper;

import com.google.common.collect.Lists;

public class ExcelTemplateParser
{
	private final ExcelHelper excel=new ExcelHelper();

	private final Sheet sheet;
	private final int lastcolumn;
	private final int lastrow;
	private final ExcelTemplate template;
	
	public static ExcelTemplate parse(Sheet sheet)
	{
		ExcelTemplateParser parser=new ExcelTemplateParser(sheet);
		parser.parse();
		return parser.getTemplate();
	}
	
	////////////////////////////////////////////////////////
	
	private ExcelTemplateParser(Sheet sheet)
	{
		System.out.println("loading template "+sheet.getSheetName());
		this.sheet=sheet;
		lastcolumn=excel.getLastColNum(sheet);
		lastrow=sheet.getLastRowNum();
		template=new ExcelTemplate(sheet.getSheetName(), lastcolumn, lastrow+1);//+1
	}
	
	private ExcelTemplate getTemplate(){return template;}
	
	private void parse()
	{
		template.setColWidths(getColWidths());
		template.setRowHeights(getRowHeights());

		for (int rownum=0; rownum<=lastrow; rownum++)
		{
			Row row=sheet.getRow(rownum);
			if (row==null)
				continue;
			for (int colnum=0; colnum<=lastcolumn; colnum++)
			{
				//Cell cell=row.getCell(colnum, Row.CREATE_NULL_AS_BLANK);
				Cell cell=row.getCell(colnum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				template.add(cell);
			}
		}
		getMergedCells();
	}
	
	private List<Integer> getColWidths()
	{
		List<Integer> colWidths=Lists.newArrayList();
		for (int colnum=0; colnum<=lastcolumn; colnum++)
		{
			colWidths.add(Math.round(sheet.getColumnWidthInPixels(colnum)));
		}
		return colWidths;
	}

	private List<Integer> getRowHeights()
	{
		List<Integer> rowHeights=Lists.newArrayList();
		for (int rownum=0; rownum<=lastrow; rownum++)
		{
			Row row=sheet.getRow(rownum);
			float points=(row==null) ? sheet.getDefaultRowHeight() : row.getHeightInPoints();
			rowHeights.add(Math.round(ExcelHelper.points2pixels(points)));
		}
		return rowHeights;
	}
	
	private void getMergedCells()
	{
		for (int mergenum=0; mergenum<sheet.getNumMergedRegions(); mergenum++)
		{
			CellRangeAddress range=sheet.getMergedRegion(mergenum);
			//System.out.println("merged cell="+range);
			template.add(range);
		}
	}
}
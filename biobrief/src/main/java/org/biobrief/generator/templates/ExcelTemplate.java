package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.biobrief.util.CException;
import org.biobrief.util.ExcelHelper;
import org.biobrief.util.StringHelper;

public class ExcelTemplate
{
	private ExcelHelper excel=new ExcelHelper();
	protected final String name;
	protected final Integer numcolumns;
	protected final Integer numrows;
	protected List<Integer> colWidths=Lists.newArrayList();
	protected List<Integer> rowHeights=Lists.newArrayList();
	protected List<CellData> cells=Lists.newArrayList();
	
	public ExcelTemplate(String name, int numcolumns, int numrows)
	{
		this.name=name;
		this.numcolumns=numcolumns;
		this.numrows=numrows;
	}
	
	public CellData add(Cell cell)
	{
		CellData celldata=new CellData(cell);
		add(celldata);
		return celldata;
	}
	
	public void add(CellData cell)
	{
		cells.add(cell);
	}
	
	public void add(CellRangeAddress range)
	{
		CellData cell=findCell(range.getFirstRow(), range.getFirstColumn());
		cell.setRange(range);
		// remove other cells in the range
		for (int row=range.getFirstRow(); row<=range.getLastRow(); row++)
		{
			for (int col=range.getFirstColumn(); col<=range.getLastColumn(); col++)
			{
				if (row==range.getFirstRow() && col==range.getFirstColumn())
					continue;
				cell=findCell(row, col);
				//System.out.println("removing cell: row="+row+", col="+col);
				cells.remove(cell);
			}
		}
	}
	
	public void setColWidths(List<Integer> colWidths)
	{
		this.colWidths=colWidths;
	}
	
	public void setRowHeights(List<Integer> rowHeights)
	{
		this.rowHeights=rowHeights;
	}
	
	public Integer getColumnWidth(Integer col)
	{
		return this.colWidths.get(col);
	}
	
	public Integer getRowHeight(Integer row)
	{
		return this.rowHeights.get(row);
	}
	
//	public String getTemplateName()
//	{
//		return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, name);
//	}

	public CellData findCell(int row, int col)
	{
		for (CellData cell : cells)
		{
			if (cell.matches(row, col))
				return cell;
		}
		return null;
	}
	
	public CellData getCell(int row, int col)
	{
		CellData cell=findCell(row, col);
		if (cell==null)
			throw new CException("cannot find cell: row="+row+" col="+col+" in template="+name);
		return cell;
	}
	
	public String getName(){return name;}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
	
	public class CellData
	{		
		protected Integer row;
		protected Integer col;
		protected Integer rowspan=1;
		protected Integer colspan=1;
		protected Object value;
		protected String comment;
		protected Boolean readOnly;
		protected HorizontalAlignment hAlign;
		protected VerticalAlignment vAlign;
		protected String bgColor;
		
		protected Integer fontHeight;
		protected String fontColor;
		protected boolean bold=false;
		protected boolean italic=false;
		protected boolean underline=false;

		protected Borders borders=new Borders();
				
		public CellData(Cell cell)
		{
			row=cell.getRowIndex();
			col=cell.getColumnIndex();
			setValue(cell);
			setComment(cell);
			setStyle(cell);
			borders.set(cell);
		}
		
		public void setRange(CellRangeAddress range)
		{
			this.rowspan=range.getLastRow()-range.getFirstRow()+1;
			this.colspan=range.getLastColumn()-range.getFirstColumn()+1;
			//System.out.println("setting merge cell range: rowspan="+rowspan+" colspan="+colspan);
		}
		
		public boolean isMerged()
		{
			return rowspan>1 || colspan>1;
		}
		
		public boolean matches(int row, int col)
		{
			return this.row==row && this.col==col;
		}
		
		private void setValue(Cell cell)
		{		
			this.value=excel.getCellValue(cell);
		}
		
		private void setComment(Cell cell)
		{
			this.comment=ExcelHelper.getComment(cell);
		}

		private void setStyle(Cell cell)
		{
			this.hAlign=ExcelHelper.getHorizontalAlignment(cell);
			this.vAlign=ExcelHelper.getVerticalAlignment(cell);
			this.bgColor=ExcelHelper.getBgColor(cell);
			
			XSSFFont font=ExcelHelper.getFont(cell);
			fontHeight=(int)font.getFontHeightInPoints();
			fontColor=ExcelHelper.getFontColor(font);
			bold=font.getBold();
			italic=font.getItalic();
			underline=font.getUnderline()!=0;
		}
		
		public Style getStyle()
		{
			Style style=new Style();
			style.setDefaultValue("vertical-align","bottom");
			getStyle(style);
			return style;
		}
		
		public void getStyle(Style style)
		{
			borders.getStyle(style);
			if (bgColor!=null)
				style.backgroundColor(bgColor);
			if (fontColor!=null)
				style.color(fontColor);
			if (fontHeight!=null && fontHeight!=ExcelHelper.DEFAULT_FONT_HEIGHT)
				style.fontSize(fontHeight);
			if (bold)
				style.bold();
			if (underline)
				style.underline();
			if (italic)
				style.italics();
			style.align(TemplateUtils.getHorizontalAlignment(this.hAlign));
			if (vAlign!=VerticalAlignment.BOTTOM)
				style.valign(TemplateUtils.getVerticalAlignment(this.vAlign));
		}

		//http://www.w3schools.com/jsref/dom_obj_style.asp
		public Map<String, Object> getStyleMap()
		{
			Map<String, Object> map=Maps.newLinkedHashMap();
			if (bgColor!=null)
				map.put("backgroundColor", StringHelper.singleQuote(bgColor));
			if (fontColor!=null)
				map.put("color", StringHelper.singleQuote(fontColor));
			if (fontHeight!=null && fontHeight!=ExcelHelper.DEFAULT_FONT_HEIGHT)
				map.put("fontSize", StringHelper.singleQuote(fontHeight+"pt"));
			if (bold)
				map.put("fontWeight", StringHelper.singleQuote("bold"));
			if (underline)
				map.put("textDecoration", StringHelper.singleQuote("underline"));
			if (italic)
				map.put("fontStyle", StringHelper.singleQuote("italic"));
			return map;
		}
		
		public String getStringValue()
		{
			return StringHelper.dflt(value, "");
		}
		
		public Object getValue(){return value;}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
	}
	
	public class Borders
	{
		protected BorderData top=new BorderData(BorderSide.TOP);
		protected BorderData left=new BorderData(BorderSide.LEFT);
		protected BorderData bottom=new BorderData(BorderSide.BOTTOM);
		protected BorderData right=new BorderData(BorderSide.RIGHT);
			
		public void set(Cell cell)
		{
			left.set(cell);
			right.set(cell);
			top.set(cell);
			bottom.set(cell);
		}
		
		public void getStyle(Style style)
		{
			if (identical())
			{
				top.getStyle(style, null);
				return;
			}
			
			top.getStyle(style);
			left.getStyle(style);
			bottom.getStyle(style);
			right.getStyle(style);
		}
		
		public boolean identical()
		{
			return (top.match(left) && top.match(bottom) && top.match(right));
		}
	}
	
	public class BorderData
	{
		final protected BorderSide side;
		protected String style;
		protected Integer width;
		protected String color;
		
		public BorderData(BorderSide side)
		{
			this.side=side;
		}
		
		public void set(Cell cell)
		{
			BorderStyle border=ExcelHelper.getBorder(cell, side);
			this.style=ExcelHelper.getBorderStyle(border);
			this.width=ExcelHelper.getBorderWidth(border);
			this.color=ExcelHelper.getBorderColor(cell, side);
		}
		
		public void getStyle(Style style)
		{
			getStyle(style, side);
		}
		
		public void getStyle(Style style, BorderSide side)
		{
			String name="border";
			if (side!=null)
				name+="-"+side.name().toLowerCase();
			if (width==null || width==0 || style==null)
				return;
			style.put(name, width+"px "+this.style+" "+color);
			
		}
		
		public boolean match(BorderData data)
		{
			return matches(style, data.style) && matches(width, data.width) && matches(color, data.color); 
		}
		
		private boolean matches(Object obj1, Object obj2)
		{
			if (obj1==null && obj2==null)
				return true;
			if ((obj1!=null && obj2==null) || obj1==null && obj2!=null)
				return false;
			return obj1.equals(obj2);
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
	}
}

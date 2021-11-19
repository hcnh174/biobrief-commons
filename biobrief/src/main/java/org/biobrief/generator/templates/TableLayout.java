package org.biobrief.generator.templates;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class TableLayout extends AbstractTemplate
{	
	private final Table<Integer, Integer, Cell> table=HashBasedTable.create();//R,C
	protected final Integer numcols;
	protected final Integer numrows;
	protected final List<Integer> colWidths;
	protected final List<Integer> rowHeights;
	
	public TableLayout(ExcelTemplate template)
	{
		super(template.getName());
		this.numcols=template.findCell(0,0).colspan;
		this.numrows=template.numrows;
		//System.out.println("numcols="+numcols);
		this.colWidths=Lists.newArrayList(template.colWidths.subList(0, this.numcols));
		this.rowHeights=Lists.newArrayList(template.rowHeights);
	}
	
	protected void init()
	{
		for (int row=0; row<numrows; row++)
		{
			for (int col=0; col<numcols; col++)
			{
				TableLayout.Cell cell=new TableLayout.Cell();
				put(row, col, cell);
			}
		}
	}
	
	public Cell put(Integer row, Integer col, Cell cell)
	{
		if (row>numrows-1) throw new CException("row index "+row+" is out of bounds ("+numrows+")");
		if (col>numcols-1) throw new CException("col index "+col+" is out of bounds ("+numcols+")");
		cell.row=row;
		cell.col=col;
		return table.put(row, col, cell);
	}
	
	public Cell get(Integer row, Integer col)
	{
		return table.get(row, col);
	}
	
	public void hide(Integer row, Integer col)
	{
		Cell cell=get(row, col);
		if (cell==null)
		{
			//System.err.println("cell ["+row+", "+col+"] is null");
			return;
		}
		cell.hide();
	}
	
	public Integer getNumRows()
	{
		if (table.isEmpty())
			return 0;
		return Collections.max(table.rowKeySet())+1;
	}
	
	public Integer getNumCols()
	{
		if (table.isEmpty())
			return 0;
		//return Collections.max(table.columnKeySet())+1;
		return numcols;
	}
	
	public Integer getWidth()
	{
		//return colWidths.stream().reduce(0, Integer::sum);
		return colWidths.stream().mapToInt(i -> i).sum();
	}
	
	public Collection<Cell> getRow(Integer rownum)
	{
		return table.row(rownum).values();
	}
	
	public List<Cell> getVisibleCells(Integer rownum)
	{
		List<Cell> list=Lists.newArrayList();
		for (Cell cell : getRow(rownum))
		{
			if (!cell.isHidden())
				list.add(cell);
		}
		return list;
	}
	
	public List<Cell> getVisibleCells()
	{
		List<Cell> list=Lists.newArrayList();
		for (Cell cell : getCells())
		{
			if (!cell.isRendered())
				continue;
			list.add(cell);
		}
		return list;
	}
	
	public Collection<Cell> getCells()
	{
		return table.values();
	}
	
	public void mergeEmptyCells()
	{
		for (Integer row : table.rowKeySet())
		{
			mergeEmptyCells(row);
		}
	}
	
	private void mergeEmptyCells(int row)
	{
		if (table.row(row).values().size()==1)
			return;
		//System.out.println("trying to merge row: "+row);
		List<List<Cell>> ranges=findEmptyCellRanges(table.row(row).values());
		//System.out.println("found "+ranges.size()+" ranges");
		for (List<Cell> range : ranges)
		{
			mergeCells(range);
		}
	}
	
	private static List<List<Cell>> findEmptyCellRanges(Collection<Cell> cells)
	{
		List<List<Cell>> ranges=Lists.newArrayList();
		List<Cell> range=Lists.newArrayList();
		ranges.add(range);
		for (Cell cell : cells)
		{
			if (cell.isHidden())//hidden cells should start a new range but not add itself
			{
				range=Lists.newArrayList();
				ranges.add(range);
				continue;
			}
			if (!cell.isEmpty())
			{
				range=Lists.newArrayList();
				ranges.add(range);
			}
			range.add(cell);
		}
		//System.out.println("ranges=: "+StringHelper.toString(ranges));
		return ranges;
	}
	
	private void mergeCells(List<Cell> cells)
	{
		if (cells.size()<2)
			return;
		//System.out.println("merging "+cells.size()+" cells");
		Cell cell=cells.get(0);
		Integer colspan=cell.getColspan();//-1; // todo??
		for (int index=1; index<cells.size(); index++)
		{
			Cell emptycell=cells.get(index);
			colspan+=emptycell.getColspan();
			hide(emptycell.row, emptycell.col);
		}
		cell.colspan=colspan;
	}

	public String getLayout()
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("<table border=\"1\">\n");
		for (int row=0; row<getNumRows(); row++)
		{
			buffer.append("<tr>\n");
			for (int col=0; col<getNumCols(); col++)
			{
				buffer.append("<td>");
				Cell cell=table.get(row, col);	
				if (cell.isHidden())
					buffer.append("-");
				else if (cell.isEmpty())
					buffer.append("&nbsp;");
				else buffer.append("X");
				buffer.append("</td>\n");
			}
			buffer.append("</tr>\n");
		}
		buffer.append("</table>\n");
		return buffer.toString();
	}
	
	public String getCssGrid()
	{
		List<String> list=Lists.newArrayList();
		for (Cell cell : table.values())
		{
			if (cell.isHidden() || cell.isEmpty())
				continue;
			String area=""+(cell.row+1)+" / "+(cell.col+1)+" / span "+cell.rowspan+" / span "+cell.colspan; 
			StringBuilder buffer=new StringBuilder();
			buffer.append("\t<div");
			buffer.append(" style=\"grid-area: "+area+"; border: 1px solid black\"");
			buffer.append(">");
			for (Item item : cell.getItems())
			{
				if (item instanceof Text)
					buffer.append(((Text)item).getValue());
				else buffer.append(item.getClass().getSimpleName());
			}			
			buffer.append("</div>");
			list.add(buffer.toString());
		}		
		
		String colwidths=StringHelper.join(StringHelper.suffix(colWidths, "px"), " ");
		//String rowheights=StringHelper.join(StringHelper.suffix(rowHeights, "px"), " ");
		
		StringBuilder buffer=new StringBuilder();
		buffer.append("<div");
		buffer.append(" style=\"");
			buffer.append("display: inline-grid;");
			buffer.append("grid-gap: 0px;");
			buffer.append("border: 1px solid black;");
			buffer.append("grid-template-columns: "+colwidths+";");
			//buffer.append("grid-template-rows: "+rowheights+";");
			buffer.append("\"");
		buffer.append(">\n");
		buffer.append(StringHelper.join(list, "\n")).append("\n");
		buffer.append("</div>\n");
		return buffer.toString();
	}
	
	public Integer getRowHeight(int rownum)
	{
		return rowHeights.get(rownum);
	}
	
	public List<Integer> getColWidths(){return colWidths;}
	public List<Integer> getRowHeights(){return rowHeights;}
	
	public interface Item
	{
		
	}
	
	public static class Text implements Item
	{
		protected String value;
		
		public Text()
		{
			this("");
		}
		
		public Text(String value)
		{
			this.value=value;
		}
		
		public String getValue(){return value;}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
	}
	
	public static class Cell
	{
		protected List<Item> items=Lists.newArrayList();
		protected Integer rowspan=1;
		protected Integer colspan=1;
		protected Style style=new Style();
		private boolean hidden=false;
		private Integer row;
		private Integer col;
		
		public Cell(){}
		
		public Cell(String value)
		{
			this(new Text(value));
		}
		
		public Cell(Item item)
		{
			this.items.add(item);
		}
		
		public Cell(Collection<Item> items)
		{
			this.items.addAll(items);
		}
		
		public void hide()
		{
			this.hidden=true;
		}
		
		public boolean isHidden()
		{
			return hidden;
		}
		
		public void setStyle(CellData cell)
		{
			cell.getStyle(style);
		}
		
		public boolean isEmpty()
		{
			return items.isEmpty();
		}
		
		public boolean isRendered()
		{
			return !isHidden() && !isEmpty();
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public List<Item> getItems(){return items;}
		public Integer getRow(){return row;}
		public Integer getCol(){return col;}
		public Integer getRowspan(){return rowspan;}
		public Integer getColspan(){return colspan;}
		public Style getStyle(){return style;}
	}
}
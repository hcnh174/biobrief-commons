package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.ExcelHelper;
import org.biobrief.util.FileHelper;
import org.biobrief.util.JsHelper.Function;
import org.biobrief.util.StringHelper;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class HandsontableHelper
{
	private static final String BOLD_CLASS="bold";
	private static final String ITALIC_CLASS="italic";
	private static final String UNDERLINE_CLASS="underline";
	
	public static List<String> getClasses(CellData celldata)
	{
		List<String> classes=Lists.newArrayList();
		String hAlign=getHAlign(celldata.hAlign);
		if (StringHelper.hasContent(hAlign) && !hAlign.equals("htCenter"))
			classes.add(hAlign);
		String vAlign=getVAlign(celldata.vAlign);
		if (StringHelper.hasContent(vAlign) && !vAlign.equals("vtCenter"))
			classes.add(vAlign);
		classes.addAll(getStyleClasses(celldata));
		return classes;
	}
	
	private static List<String> getStyleClasses(CellData celldata)
	{
		List<String> classes=Lists.newArrayList();
		if (celldata.bold)
			classes.add(BOLD_CLASS);
		if (celldata.italic)
			classes.add(ITALIC_CLASS);
		if (celldata.underline)
			classes.add(UNDERLINE_CLASS);
		if (celldata.fontHeight!=null && celldata.fontHeight!=ExcelHelper.DEFAULT_FONT_HEIGHT)
			classes.add(getFontClass(celldata.fontHeight));
		if (celldata.bgColor!=null)
			classes.add(getColorClass(celldata.bgColor));
		if (celldata.fontColor!=null)
			classes.add(getColorClass(celldata.fontColor));
		return classes;
	}
	
	private static String getFontClass(Integer fontHeight)
	{
		return "font"+fontHeight;
	}
	
	private static String getColorClass(String color)
	{
		//rgb(128, 128, 128) to rgb128-128-128
		String cls=color;
		cls=StringHelper.replace(cls, "(", "-");
		cls=StringHelper.replace(cls, ")", "");
		cls=StringHelper.replace(cls, ", ", "-");
		return cls;
	}
	
	public static String getHAlign(HorizontalAlignment hAlign)
	{
		//System.out.println("halign="+hAlign.name());
		switch(hAlign)
		{
		case CENTER:
		case CENTER_SELECTION:
			return "htCenter";
		case LEFT:
			return "htLeft";
		case RIGHT:
			return "htRight";
		case GENERAL:
		case DISTRIBUTED:
		case FILL:
		case JUSTIFY:
			return null;
		default:
			throw new CException("no handler for horizontal alignnment type: "+hAlign);	
		}
	}
	
	public static String getVAlign(VerticalAlignment vAlign)
	{
		//System.out.println("valign="+vAlign.name());
		switch(vAlign)
		{
		case CENTER:
			return "vtCenter";
		case TOP:
			return "vtTop";
		case BOTTOM:
			return "vtBottom";
		case DISTRIBUTED:
		case JUSTIFY:
			return null;
		default:
			throw new CException("no handler for vertical alignnment type: "+vAlign);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	public static Function createRenderer(CellData celldata)
	{
		Map<String, Object> map=celldata.getStyleMap();
		Function function=new Function("renderer",  Lists.newArrayList("instance", "td", "row", "col", "prop", "value", "cellProperties"));
		//function.addCommand("console.log('renderer')");
		function.addCommand("Handsontable.renderers.TextRenderer.apply(this, arguments)");
		for (String key : map.keySet())
		{
			function.addCommand("td.style."+key+"="+map.get(key));
		}
		return function;
	}

	////////////////////////////////////////////////////////////////////////
	
	public static List<Handsontable.Border> getBorders(List<CellData> cells)
	{
		Multimap<String, CellData> bordermap=buildBorderMap(cells);
		logBorderMap(bordermap);
		return splitByRange(bordermap);
	}
	
	private static Multimap<String, CellData> buildBorderMap(List<CellData> cells)
	{
		Multimap<String, CellData> multimap=LinkedHashMultimap.create();
		for (CellData celldata : cells)
		{
			Handsontable.BorderConfig border=new Handsontable.BorderConfig(celldata);
			if (border.hasParams())
				multimap.put(border.getKey(), celldata);
		}
		return multimap;
	}
	
	public static List<Handsontable.Border> splitByRange(Multimap<String, CellData> multimap)
	{
		List<Handsontable.Border> borders=Lists.newArrayList();
		for (String key : multimap.keySet())
		{
			List<CellData> cells=Lists.newArrayList(multimap.get(key));
			Multimap<String, CellData> byrow=splitByRow(cells);
			for (String row : byrow.keySet())
			{
				System.out.println("starting row: "+row);
				List<CellData> rowcells=Lists.newArrayList(byrow.get(row));
				List<Handsontable.Border> ranges=findBorderRanges(rowcells);
				System.out.println("  found "+ranges.size()+" ranges");
				borders.addAll(ranges);
			}
		}
		return borders;
	}
	
//	private static List<Handsontable.Border> findBorderRanges(List<CellData> cells)
//	{
//		List<Handsontable.Border> borders=Lists.newArrayList();
//		if (cells.isEmpty())
//			return borders;
//		CellData cell=cells.get(0);
//		Handsontable.Border border=new Handsontable.Border(cell);//cell.row, cell.col);
//		borders.add(border);
//		if (cells.size()==1)
//			return borders;
//		System.out.println("ref cell: row="+cell.row+", col="+cell.col+", rowspan="+cell.rowspan+", colspan="+cell.colspan);
//		int nextcol=cell.col+cell.colspan;//+1;
//		System.out.println("nextcol="+nextcol);
//		for (int index=1; index<cells.size(); index++)
//		{
//			cell=cells.get(index);
//			System.out.println("  cell: row="+cell.row+", col="+cell.col+", rowspan="+cell.rowspan+", colspan="+cell.colspan);
//			if (cell.col==nextcol)
//			{
//				System.out.println("extending range");
//				border.range.setTo(cell.row+cell.rowspan-1, cell.col+cell.colspan-1);
//			}
//			else
//			{
//				System.out.println("starting new range");
//				border=new Handsontable.Border(cell);//cell.row, cell.col);
//				borders.add(border);
//				nextcol=cell.col+cell.colspan;//+1;
//				System.out.println("nextcol="+nextcol);
//			}
//		}
//		return borders;
//	}
	
	private static List<Handsontable.Border> findBorderRanges(List<CellData> cells)
	{
		List<Handsontable.Border> borders=Lists.newArrayList();
		if (cells.isEmpty())
			return borders;
		CellData cell=cells.get(0);
		Handsontable.Border border=new Handsontable.Border(cell);
		borders.add(border);
		if (cells.size()==1)
			return borders;
		System.out.println("ref cell: row="+cell.row+", col="+cell.col+", rowspan="+cell.rowspan+", colspan="+cell.colspan);
		int nextcol=cell.col+cell.colspan;//+1;
		System.out.println("nextcol="+nextcol);
		for (int index=1; index<cells.size(); index++)
		{
			cell=cells.get(index);
			System.out.println("  cell: row="+cell.row+", col="+cell.col+", rowspan="+cell.rowspan+", colspan="+cell.colspan);
			if (cell.col==nextcol)
			{
				System.out.println("extending range");
				border.range.setTo(cell.row+cell.rowspan-1, cell.col+cell.colspan-1);
			}
			else
			{
				System.out.println("starting new range");
				border=new Handsontable.Border(cell);//cell.row, cell.col);
				borders.add(border);
				System.out.println("nextcol="+nextcol);
			}
			nextcol=cell.col+cell.colspan;//+1;
		}
		return borders;
	}
	
//	private static Multimap<Integer, CellData> splitByRow(List<CellData> cells)
//	{
//		Multimap<Integer, CellData> byrows=LinkedHashMultimap.create();
//		for (CellData cell : cells)
//		{
//			byrows.put(cell.row, cell);
//		}
//		return byrows;
//	}

	private static Multimap<String, CellData> splitByRow(List<CellData> cells)
	{
		Multimap<String, CellData> byrows=LinkedHashMultimap.create();
		for (CellData cell : cells)
		{
			String key=cell.row+":"+cell.rowspan;
			byrows.put(key, cell);
		}
		return byrows;
	}
	
	private static void logBorderMap(Multimap<String, CellData> multimap)
	{
		String filename=".temp/handsontable-borders.txt";
		FileHelper.writeFile(filename);
		System.out.println("border multimap");
		for (String key : multimap.keySet())
		{
			String str=key+": "+multimap.get(key).size();
			FileHelper.appendFile(filename, str);
			System.out.println(str);
			for (CellData celldata : multimap.get(key))
			{
				str="cell: row"+celldata.row+", col="+celldata.col+", rowspan="+celldata.rowspan+", colspan="+celldata.colspan;
				FileHelper.appendFile(filename, str);
			} 
		}
	}
}
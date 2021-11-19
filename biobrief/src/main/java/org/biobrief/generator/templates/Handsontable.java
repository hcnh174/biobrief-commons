package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;

import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.JsHelper;
import org.biobrief.util.JsHelper.Function;
import org.biobrief.util.JsHelper.JsList;
import org.biobrief.util.JsHelper.JsMap;
import org.biobrief.util.JsHelper.JsObject;
import org.biobrief.util.StringHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

/*
data: data,
colWidths: [100, 100, 100, 100, 120, 180],
rowHeaders: true,
colHeaders: true,
dropdownMenu: true,
className: 'htCenter',
mergeCells: true,
mergeCells:
[
	{row: 1, col: 1, rowspan: 3, colspan: 3}
],
cell:
[
	{row: 0, col: 0, readOnly: true},
	{row: 1, col: 1, readOnly: true, renderer: greenRenderer, className: 'htRight htBottom'},
	{row: 17, col: 17, renderer: boldRenderer}
],
customBorders:
[
	{
		range: {from: {row: 10, col: 10}, to: {row: 15, col: 15}},
		top: {width: 2, color: 'red'},
		right: {width: 2, color: 'red'},
		bottom: {width: 2, color: 'red'},
		left: {width: 2, color: 'red'}
	}	
]
*/
public final class Handsontable extends JsObject
{
	protected final String name;
	protected final Data data;
	protected List<Integer> colWidths;
	protected List<Integer> rowHeights;
	protected Boolean rowHeaders;
	protected Boolean colHeaders;
	protected Boolean dropdownMenu;
	protected Boolean wordWrap;
	protected String className;
	protected String readOnlyCellClassName="readonly";
	protected JsList<MergeCell> mergeCells=new JsList<MergeCell>();
	protected JsList<Cell> cell=new JsList<Cell>();
	protected JsList<Border> customBorders=new JsList<Border>();
	protected BindMap bindMap=new BindMap();
	protected Renderers renderers=new Renderers();
	
	public Handsontable(String name, int numrows, int numcols)
	{
		this.name=name;
		this.data=new Data(numrows, numcols);
	}
	
	public Handsontable(ExcelTemplate template)
	{
		this(template.getName(), template.numrows, template.numcolumns);
		this.colWidths=template.colWidths;
		this.rowHeights=template.rowHeights;
		this.rowHeaders=false;
		this.colHeaders=false;
		this.dropdownMenu=true;
		this.wordWrap=false;
		this.className="htCenter vtCenter";
		
		for (CellData cell : template.cells)
		{
			//System.out.println("item: row="+item.row+", col="+item.col+", value="+item.value);
			String value=StringHelper.dflt(cell.getValue());
			if (TemplateUtils.isControl(value))
				this.bindMap.add(cell.row, cell.col, TemplateUtils.getControlName(value));
			else this.data.set(cell.row, cell.col, StringHelper.dflt(cell.getValue()));//else
			if (cell.isMerged())
				this.addMergedCell(new MergeCell(cell.row, cell.col, cell.rowspan, cell.colspan));
		}
		
		for (CellData celldata : template.cells)
		{
			Handsontable.Cell cell=new Handsontable.Cell(celldata.row, celldata.col);
			if (TemplateUtils.isControl(StringHelper.dflt(celldata.getValue())))
				cell.readOnly=false;
			List<String> classes=HandsontableHelper.getClasses(celldata);
			if (!classes.isEmpty())
				cell.className=StringHelper.join(classes, " ");
			this.addCell(cell);
		}
		
		///////////////////////////////////////////////
		
		//customBorders.addAll(HandsontableHelper.getBorders(template.cells));
		
		for (CellData celldata : template.cells)
		{
			Handsontable.Border border=new Handsontable.Border(celldata);
			this.addBorder(border);
		}
	}
	
	private void addBorder(Handsontable.Border border)
	{
		if (border.hasParams())
			this.customBorders.add(border);
	}
	
	public Renderers getRenderers(){return renderers;}
	
	public String getName(){return name;}
	
	public String getFilename()
	{
		return name+".component.ts";
	}
	
	public String getHtmlFilename()
	{
		return getFilenameRoot()+".html";
	}
	
	public String getTypescriptFilename()
	{
		return getFilenameRoot()+".ts";
	}
		
	private String getFilenameRoot()
	{
		return "treatments/sheets/"+name+".component";// @TODO - hack! - determine path
	}
	
	public void addCell(Handsontable.Cell cell)
	{
		if (cell.hasParams())
			this.cell.add(cell);
		//else System.out.println("cell has no params, skipping");
	}
	
	public void addMergedCell(MergeCell mergeCell)
	{
		this.mergeCells.add(mergeCell);
	}
	
	@Override
	protected void getParams(Map<String, Object> params)
	{
		setParam(params, "data", data.getData());
		setParam(params, "colWidths", colWidths);
		setParam(params, "rowHeights", rowHeights);
		setParam(params, "rowHeaders", rowHeaders);
		setParam(params, "colHeaders", colHeaders);
		setParam(params, "dropdownMenu", dropdownMenu);
		setParam(params, "wordWrap", wordWrap);
		setParam(params, "readOnly", true);
		setStringParam(params, "readOnlyCellClassName", readOnlyCellClassName);
		setStringParam(params, "className", className);
		setParam(params, "mergeCells", mergeCells);
		setParam(params, "cell", cell);
		setParam(params, "customBorders", customBorders);
		setParam(params, "bindMap", bindMap);
	}
	
	public String toJavascript()
	{
		return JsHelper.toJavascript(getParams());
	}
	
	public static class MergeCell extends JsObject
	{
		protected Integer col;
		protected Integer row;
		protected Integer colspan;
		protected Integer rowspan;
		
		public MergeCell(){}
		
		public MergeCell(Integer row, Integer col, Integer rowspan, Integer colspan)
		{
			this.row=row;
			this.col=col;
			this.rowspan=rowspan;
			this.colspan=colspan;
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "row", row);
			setParam(params, "col", col);
			setParam(params, "rowspan", rowspan);
			setParam(params, "colspan", colspan);
		}
	}
	
	public static class Cell extends JsObject
	{
		protected Integer row;
		protected Integer col;
		protected Boolean readOnly;
		protected String className;
		protected String renderer;
		
		public Cell(){}
		
		public Cell(Integer row, Integer col)
		{
			this.row=row;
			this.col=col;
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "row", row);
			setParam(params, "col", col);			
			setParam(params, "readOnly", readOnly);
			setStringParam(params, "className", className);
			setParam(params, "renderer", renderer);
		}
		
		@Override
		public boolean hasParams() // true only if has params other than col and row
		{
			Map<String, Object> params=getParams();
			return params.values().size()>2;
		}
	}
	
	public static class CellAddress extends JsObject
	{
		protected Integer row;
		protected Integer col;
	
		public CellAddress(){}
		
		public CellAddress(Integer row, Integer col)
		{
			set(row, col);
		}
		
		public void set(Integer row, Integer col)
		{
			this.row=row;
			this.col=col;
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "row", row);
			setParam(params, "col", col);
		}
	}
	
	public static class Range extends JsObject
	{
		protected CellAddress from=new CellAddress();
		protected CellAddress to=new CellAddress();
		
		public Range(){}
		
		public Range(Range other)
		{
			this.from.col=other.from.col;
			this.to.col=other.to.col;
		}
		
		public void setFrom(Integer row, Integer col)
		{
			this.from.set(row, col);
		}
		
		public void setTo(Integer row, Integer col)
		{
			this.to.set(row, col);
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "from", from.getParams());
			setParam(params, "to", to.getParams());
		}
		
		protected CellAddress getFrom(){return from;}
		protected CellAddress getTo(){return to;}
	}
	
	public static class BorderItem extends JsObject
	{
		protected Integer width;
		protected String color;
		
		public BorderItem(){}
		
		public BorderItem(Integer width, String color)
		{
			this.width=width;
			this.color=color;
		}
		
		public BorderItem(BorderItem item)
		{
			this(item.width, item.color);
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "width", width);
			setStringParam(params, "color", color);
		}
		
		@Override
		public boolean hasParams()
		{
			return width!=null && width>0;
		}
		
		public String getKey()
		{
			return width+":"+color;
		}
	}
	
	public static class BorderConfig extends JsObject
	{
		protected BorderItem top=new BorderItem();
		protected BorderItem bottom=new BorderItem();
		protected BorderItem left=new BorderItem();
		protected BorderItem right=new BorderItem();
		
		public BorderConfig(){}
		
		public BorderConfig(CellData celldata)
		{
			left.width=celldata.borders.left.width;
			top.width=celldata.borders.top.width;
			right.width=celldata.borders.right.width;
			bottom.width=celldata.borders.bottom.width;
			
			left.color=celldata.borders.left.color;
			top.color=celldata.borders.top.color;
			right.color=celldata.borders.right.color;
			bottom.color=celldata.borders.bottom.color;
		}
		
		public BorderConfig(Integer width, String color)
		{
			setColor(color);
			setWidth(width);
		}
		
		public void setColor(String color)
		{
			this.top.color=color;
			this.bottom.color=color;
			this.left.color=color;
			this.right.color=color;
		}
		
		public void setWidth(Integer width)
		{
			this.top.width=width;
			this.bottom.width=width;
			this.left.width=width;
			this.right.width=width;
		}
		
		@Override
		protected void getParams(Map<String, Object> params)
		{
			if (top.hasParams())
				setParam(params, "top", top.getParams());
			if (bottom.hasParams())
				setParam(params, "bottom", bottom.getParams());
			if (left.hasParams())
				setParam(params, "left", left.getParams());
			if (right.hasParams())
				setParam(params, "right", right.getParams());
		}
		
		@Override
		public boolean hasParams()
		{
			return top.hasParams() || bottom.hasParams() || left.hasParams() || right.hasParams(); 
		}
		
		public String getKey()
		{
			List<String> list=Lists.newArrayList();
			list.add(top.getKey());
			list.add(bottom.getKey());
			list.add(left.getKey());
			list.add(right.getKey());
			return StringHelper.join(list, ";");
		}
	}
	
	/*
	range: {from: {row: 10, col: 10}, to: {row: 15, col: 15}},
	top: {width: 2, color: 'red'},
	right: {width: 2, color: 'red'},
	bottom: {width: 2, color: 'red'},
	left: {width: 2, color: 'red'}. 
	*/	
	public static class Border extends BorderConfig
	{
		protected Range range=new Range();
		
		public Border(){}
		
		public Border(CellData celldata)
		{
			super(celldata);
			this.range.from.row=celldata.row;
			this.range.from.col=celldata.col;
			this.range.to.row=celldata.row+celldata.rowspan-1;
			this.range.to.col=celldata.col+celldata.colspan-1;
		}

		@Override
		protected void getParams(Map<String, Object> params)
		{
			setParam(params, "range", range.getParams());
			super.getParams(params);
		}
	}
	
	public static class Data
	{
		private final int numrows;
		private final int numcols;
		private Table<Integer,Integer,Object> data=HashBasedTable.create();
		
		public Data(int numrows, int numcols)
		{
			this.numrows=numrows;
			this.numcols=numcols;
		}
		
		public void set(Integer row, Integer col, Object value)
		{
			if (StringHelper.hasContent(value))
				data.put(row, col, value);
		}
		
		public List<List<Object>> getData()
		{
			List<List<Object>> arr=initArray(numrows, numcols);
			for (Table.Cell<Integer,Integer,Object> cell : data.cellSet())
			{
				Integer row=cell.getRowKey();
				Integer col=cell.getColumnKey();
				String value=cell.getValue().toString();
				value=StringHelper.trim(value);
				value=StringHelper.replace(value, "\n"," ");
				arr.get(row).set(col, StringHelper.doubleQuote(value));
			}
			return arr;
		}
		
		public static List<List<Object>> initArray(int numrows, int numcols)
		{
			List<List<Object>> lists=Lists.newArrayList();
			for (int row=0; row<numrows; row++)
			{
				List<Object> list=Lists.newArrayList();
				for (int col=0; col<numcols; col++)
				{
					list.add(StringHelper.doubleQuote(""));
				}
				lists.add(list);
			}
			return lists;
		}
	}
	
	public static class BindMap extends JsMap<CellAddress>
	{
		public void add(Integer row, Integer col, String name)
		{
			add(new CellAddress(row, col), name);
		}
		
		public void add(CellAddress address, String name)
		{
			map.put(StringHelper.singleQuote(name), address);
		}
	}
	
	public static class Renderers
	{
		private Map<String, Function> renderers=Maps.newLinkedHashMap();
		
		public Function findOrCreateRenderer(CellData celldata)
		{
			String key=createKey(celldata);
			if (renderers.containsKey(key))
				return renderers.get(key);
			Function function=createRenderer(celldata);
			renderers.put(key, function);
			return function;
		}
		
		private Function createRenderer(CellData celldata)
		{
			Map<String, Object> map=celldata.getStyleMap();
			String name="renderer"+(renderers.size()+1);
			Function function=new Function(name,  Lists.newArrayList("instance", "td", "row", "col", "prop", "value", "cellProperties"));
			function.addCommand("Handsontable.renderers.TextRenderer.apply(this, arguments)");
			for (String key : map.keySet())
			{
				function.addCommand("td.style."+key+"="+map.get(key));
			}
			System.out.println("RENDERER "+function.getName()+"\n"+function.toJavascript());
			return function;
		}
		
		private String createKey(CellData celldata)
		{
			Map<String, Object> map=celldata.getStyleMap();
			List<String> arr=Lists.newArrayList();
			for (String key : map.keySet())
			{
				arr.add(key+":"+map.get(key));
			}
			return StringHelper.join(arr, ";");
		}
		
		public String toTypescript()
		{
			List<String> list=Lists.newArrayList();
			for (Function renderer : renderers.values())
			{
				list.add("let "+renderer.getName()+"="+renderer.toJavascript()+"\n");
			}
			return StringHelper.join(list, "");
		}
	}
}

package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.dictionary.FieldType;
import org.biobrief.generator.GeneratorConstants.ControlType;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class FormLayout extends TableLayout
{
	protected final EntityDefinition entityType;
	protected final FormParams params;
	
	public FormLayout(ExcelTemplate template, Dictionary dictionary, FormParams defaultParams)
	{
		super(template);
		this.params=parseFormParams(defaultParams, template);
		this.entityType=dictionary.getGroup(params.getGroup()).getEntity(params.getEntity());
		init();
		for (CellData cell : template.cells)
		{
			if (cell.row==0)
				continue;
			if (cell.col>numcols-1)// ignore any columns outside of the region spanned by the first row
				continue;
			put(template, cell);
		}
		mergeEmptyCells();
	}
	
	private static FormParams parseFormParams(FormParams params, ExcelTemplate template)
	{
		params.setParams(TemplateUtils.parseParams(template));
		params.setTitle(TemplateUtils.getTitle(template));
		return params;
	}
		
	public String getGroup()
	{
		return TemplateUtils.getGroup(getEntity());
	}

	private void put(ExcelTemplate template, CellData cell)
	{
		TableLayout.Cell td=createCell(cell);
		if (cell.isMerged())
		{
			td.rowspan=cell.rowspan;
			td.colspan=cell.colspan;
			for (int row=cell.row; row<cell.row+cell.rowspan; row++)
			{
				for (int col=cell.col; col<cell.col+cell.colspan; col++)
				{
					if (row==cell.row && col==cell.col)
						continue;
					hide(row, col);
				}
			}
		}
		td.setStyle(cell);
		put(cell.row, cell.col, td);
	}
	
	private TableLayout.Cell createCell(CellData cell)
	{
		if (TemplateUtils.isControl(TemplateUtils.getValue(cell)))
			return createControlCell(cell);
		else return createTextCell(cell);
	}
			
	private TableLayout.Cell createTextCell(CellData cell)
	{
		String value=TemplateUtils.getValue(cell);
		if (value.contains("["))
		{
			int start=value.indexOf("[")+1;
			int end=value.indexOf("]", start);
			if (end==-1)
				throw new CException("expected end bracket in label name: "+value);
			String name=value.substring(start, end);
			System.out.println("form auto label name="+name);
			if (entityType.hasFieldDefinition(name))
			{
				FieldDefinition field=entityType.getFieldDefinition(name);
				System.out.println("form auto label name="+name+" label="+field.getLabel());
				value=StringHelper.replace(value, "["+name+"]", field.getLabel());
			}
		}
		return new TableLayout.Cell(value);
	}
	
	private TableLayout.Cell createControlCell(CellData cell)
	{
		String value=TemplateUtils.getValue(cell);
		List<Item> items=Lists.newArrayList();
		int index=0;
		while (true)
		{
			int start=value.indexOf(TemplateUtils.CONTROL_START, index);//"${"
			if (start==-1)
				break;
			String text=value.substring(index, start);
			addText(items, text);
			start+=TemplateUtils.CONTROL_START.length();
			int end=value.indexOf(TemplateUtils.CONTROL_END, start);//"}"
			if (end==-1)
				throw new CException("cannot find end brace for control: "+value);
			String name=value.substring(start, end);
			items.add(createControl(name, cell));
			index=end+TemplateUtils.CONTROL_END.length();
		}
		String text=value.substring(index);
		addText(items, text);
		return new TableLayout.Cell(items);
	}
	
	private static void addText(List<Item> items, String text)
	{
		if (!text.equals(""))
			items.add(new Text(text));
	}
	
	private Item createControl(String value, CellData cell)
	{
		String name=getControlName(value);
		ControlParams params=getControlParams(value);
		FieldDefinition field=entityType.getFieldDefinition(name);
		String path=TemplateUtils.getControlPath(this.params.getRoot(), name);
		params.setCellwidth(getControlWidth(cell)+"px");
		params.setCellheight(getControlHeight(cell)+"px");
		if (field==null)
			return createMissingControl(name);
		return new Control(field, path, params);
	}
	
	private String getControlName(String value)
	{
		int index=value.indexOf("|");
		if (index==-1)
			return value;
		return value.substring(0,index);
	}
	
	private ControlParams getControlParams(String value)
	{
		ControlParams params=this.params.createControlParams();
		int index=value.indexOf("|");
		if (index==-1)
			return params;
		params.setParams(TemplateUtils.parseParams(value.substring(index+1), ";"));
		return params;
	}
	
	private int getControlWidth(CellData cell)
	{
		int width=0;
		//System.out.println("getting control height for cell ["+cell.row+":"+cell.col+"]: colspan="+cell.colspan+" rowspan="+cell.rowspan+" start col="+cell.col+" end col="+(cell.col+cell.colspan-1)+" colWidths="+StringHelper.join(colWidths));
		for (int col=cell.col; col<cell.col+cell.colspan; col++)//-1
		{
			width+=colWidths.get(col);
		}
		return width;
	}
	
	private int getControlHeight(CellData cell)
	{
		int height=0;
		for (int row=cell.row; row<cell.row+cell.rowspan; row++)//-1
		{
			height+=rowHeights.get(row);
		}
		return height;
	}
	
	private TableLayout.Item createMissingControl(String value)
	{
		String html="<span style=\"color:red\" title=\""+value+"\">??</span>";
		return new TableLayout.Text(html);
	}
	
	public FormParams getParams(){return params;}
	public String getTitle(){return params.getTitle();}
	public String getRoot(){return params.getRoot();}
	public String getPath(){return params.getPath();}
	public boolean getPanel(){return params.isPanel();}
	public EntityDefinition getEntity(){return entityType;}

	/////////////////////////////////////////////////
	
	public static class Control implements Item
	{
		protected final FieldDefinition field;
		protected final String path;
		protected final String name;
		protected final ControlParams params;
		
		public Control(FieldDefinition field, String path, ControlParams params)
		{
			this.field=field;
			this.path=path;
			this.name=field.getName();
			this.params=params;
			this.params.setParams(field.getFormConfig());
		}
		public FieldType getFieldType()
		{
			return field.getFieldType();
		}
		
		public FieldDefinition getField()
		{
			return field;
		}
		
		public boolean isReadonly()
		{
			return field.isReadonly() || params.isReadonly();
		}
		
		public boolean isDisabled()
		{
			return field.isDisabled() || params.isDisabled();
		}
		
		@Override
		public String toString()
		{
			return StringHelper.toString(this);
		}
		
		public ControlParams getParams(){return params;}
		public String getPath(){return path;}
		public String getName(){return name;}
		public Boolean isCalculated(){return field.isCalculated();}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormParams extends AbstractParams
	{
		private String id=TemplateUtils.generateId("form");
		private String group;
		private String entity;
		private boolean panel=true;
		private boolean readonly=false;
		private boolean disabled=false;
		private String path;
		private String root="model";
		private String title;
		private RenderMode renderMode;
		private String srcfile;
		private String outfile;
		
		public FormParams(Map<String, Object> values)
		{
			setParams(values);
		}
		
		@Override
		public boolean setNestedMode()
		{
			this.panel=false;
			this.readonly=true;
			return true;
		}
		
		@Override
		public boolean setEditMode()
		{
			this.panel=false;
			return true;
		}
		
		@Override
		public boolean setPrintMode()
		{
			this.readonly=true;
			return true;
		}
		
		@Override
		public boolean setSearchMode()
		{
			return true;
		}
		
		public ControlParams createControlParams()
		{
			ControlParams params=new ControlParams(this);
			return params;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ControlParams extends AbstractParams
	{
		private final FormParams formParams;
		private Integer rows=1;
		private String cellwidth;
		private String cellheight;
		private String width="100%";
		private boolean readonly=false;
		private boolean disabled=false;
		private Integer fieldwidth;
		private String suffix;
		private String xtype;
		private boolean required=false;
		private Integer labelwidth;
		private boolean editable=true;
		private ControlType control=ControlType.dflt;
		
		public ControlParams(FormParams formParams)
		{
			this.formParams=formParams;
			setMode(formParams.getMode());
		}
		
		@Override
		public boolean setPrintMode()
		{
			this.readonly=true;
			this.editable=false;
			return true;
		}
		
		public FormParams getFormParams(){return formParams;}
	}
}
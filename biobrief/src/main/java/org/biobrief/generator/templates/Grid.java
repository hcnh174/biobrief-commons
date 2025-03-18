package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.dictionary.GroupDefinition;
import org.biobrief.generator.GeneratorConstants.ContainerType;
import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

public class Grid extends AbstractTemplate
{
	public static final String AUTO="[AUTO]";
	
	protected final GridParams params;
	protected final EntityDefinition entity;
	protected final List<Column> columns=Lists.newArrayList();

	public Grid(String name, GridParams params, Dictionary dictionary)
	{
		super(name);
		this.params=params;
		this.entity=getEntity(dictionary, name);
		if (this.params.getTitle().equals(AUTO))
			this.params.setTitle(entity.getLabel());
		this.params.validate();
	}
	
	private EntityDefinition getEntity(Dictionary dictionary, String name)
	{
		if (params.getDynamic())
		{
			GroupDefinition group=dictionary.findOrCreateDynamicGroup("dynamic");
			return group.createDynamicEntity(name);
		}
		GroupDefinition group=dictionary.getGroup(params.getGroup());
		return group.getEntity(params.getEntity());
	}
	
	public Grid(ExcelTemplate template, GridParams defaultParams, Dictionary dictionary )
	{
		this(template.getName(), parseGridParams(defaultParams, template), dictionary);
		for (int col=0; col<template.numcolumns; col++)
		{
			ColumnParams colparams=parseColumnParams(template, col);
			System.out.println("template="+template.getName()+" col="+col+" body template="+colparams.getBody());
			String fieldname=TemplateUtils.getControlName(colparams.getBody());
			System.out.println(" fieldname="+fieldname);
			addColumn(fieldname, colparams);
		}
	}
	
	public Column addColumn(String fieldname, ColumnParams colparams)
	{
		FieldDefinition field=entity.getFieldDefinition(fieldname, true);
		return addColumn(field, colparams);
	}
	
	private Column addColumn(FieldDefinition field, ColumnParams colparams)
	{
		Column column=new Column(this, field, colparams);
		columns.add(column);
		return column;
	}

	public GridParams getParams(){return params;}
	
	private static GridParams parseGridParams(GridParams params, ExcelTemplate template)
	{
		params.setParams(TemplateUtils.parseParams(template));
		params.setTitle(TemplateUtils.getTitle(template));
		params.setDetail(getDetail(template));
		params.validate();
		return params;
	}
	
	private ColumnParams parseColumnParams(ExcelTemplate template, int col)
	{
		CellData th=template.getCell(1,col);// get column header from second row
		CellData td=template.getCell(2,col);// get column type from third row
		if (th==null || td==null)// || !TemplateUtils.isControl(td))
			throw new CException("null cell for column: "+col);
		ColumnParams params=this.params.createColumnParams(TemplateUtils.parseParams(td));
		params.setHeader(th.getStringValue().trim());
		params.setStyle(th.getStyle());
		params.setAlign(th.hAlign);
		params.setBody(TemplateUtils.getValue(td).trim());
		params.setWidth(template.getColumnWidth(col));
		params.setVisible(!TemplateUtils.isHidden(td));
		//System.out.println("td "+params.getBody()+" color="+td.bgColor);
		return params;
	}

	// get row expander template from fourth row
	private static String getDetail(ExcelTemplate template)
	{
		CellData detailcell=template.findCell(3, 0);
		return (detailcell!=null) ? detailcell.getStringValue() : "";
	}
	
	public EntityDefinition getEntity(){return entity;}
	public List<Column> getColumns(){return columns;}
	
	public static class Column
	{
		protected final Grid grid;
		protected final FieldDefinition field;
		protected final ColumnParams params;
		
		public Column(Grid grid, FieldDefinition field, ColumnParams params)
		{
			params.validate();
			this.grid=grid;
			this.field=field;
			this.params=params;
			if (params.getHeader().equals(AUTO))
				params.setHeader(field.getLabel());
		}
		
		public boolean isFiltered()
		{
			return grid.getParams().getFiltered() && params.getFilter();
		}
		
		public Grid getGrid(){return grid;}
		public FieldDefinition getField(){return field;}
		public ColumnParams getParams(){return params;}
		public String getHeader(){return params.getHeader();}
		public String getBody(){return params.getBody();}
	}
	
	/////////////////////////////////////////////////////////
	
	public static class GridParams extends AbstractParams
	{
		private String group;
		private String entity;
		private String title;
		private String path="rows";
		private String icon="true";
		private String detail;
		private boolean lazy=false;
		private boolean addbutton=true;
		private boolean refreshbutton=true;
		private boolean autohide=false;
		private boolean bookmarkcolumn=false;
		private boolean coltoggler=true;
		private ContainerType container=ContainerType.panel;
		private boolean contextmenu=true;
		private boolean deletecolumn=true;
		private boolean downloadcolumn=false;
		private boolean editable=true;
		private boolean editcolumn=true;
		private boolean editdialog=false;
		private boolean exportable=true;
		private boolean resettable=true;
		private boolean expandable=true;
		private boolean filtered=true;
		private boolean globalfilter=true;
		private boolean header=true;
		private boolean opencolumn=false;
		private boolean paging=true;
		private boolean resizable=true;
		private boolean reorderable=true;
		private boolean selectable=true;
		private boolean sortable=true;
		private boolean toolbar=true;
		private boolean stateful=false;
		private boolean dynamic=false;
		
		public GridParams(){}

		public GridParams(Map<String, Object> values)
		{
			setParams(values);
		}
		
		public GridParams(Map<String, Object> values, Mode mode)
		{
			this(values);
			setMode(mode);
		}

		@Override
		public boolean setEditMode()
		{
			this.toolbar=true;
			this.container=ContainerType.panel;
			this.editable=true;
			this.editdialog=true;
			return true;
		}
		
		public boolean setNestedMode()
		{
			this.toolbar=false;
			this.container=ContainerType.panel;
			this.editable=true;
			this.editdialog=true;
			return true;
		}
		
		@Override
		public boolean setReadonlyMode()
		{
			//this.lazy=true;
			this.toolbar=true;
			this.addbutton=false;
			this.coltoggler=true;
			this.container=ContainerType.none;
			this.deletecolumn=false;
			this.editable=false;
			this.editcolumn=false;
			this.editdialog=true;
			this.expandable=true;
			this.exportable=true;
			this.filtered=true;
			this.globalfilter=false;
			this.opencolumn=true;
			this.selectable=true;
			this.bookmarkcolumn=false;
			//this.contextmenu=false;
			return true;
		}
		
		@Override
		public boolean setPrintMode()
		{
			//this.lazy=false;
			this.addbutton=false;
			this.autohide=true;
			this.bookmarkcolumn=false;
			this.coltoggler=false;
			this.container=ContainerType.fieldset;
			this.deletecolumn=false;
			this.editable=false;
			this.editcolumn=false;
			this.editdialog=false;
			this.exportable=false;
			this.expandable=false;
			this.filtered=false;
			this.globalfilter=false;
			this.header=true;
			this.icon="false";
			this.paging=false;
			this.selectable=false;
			this.sortable=false;
			this.toolbar=false;
			this.contextmenu=false;
			return true;
		}

		@Override
		public boolean setSearchMode()
		{
			//this.lazy=true;
			this.toolbar=true;
			this.addbutton=false;
			this.coltoggler=true;
			this.container=ContainerType.none;
			this.deletecolumn=false;
			this.editable=false;
			this.editcolumn=false;
			this.editdialog=false;
			this.expandable=false;
			this.exportable=true;
			this.filtered=true;
			this.globalfilter=false;
			this.opencolumn=true;
			this.selectable=false;
			this.bookmarkcolumn=false;
			return true;
		}
		
		public ColumnParams createColumnParams(Map<String, Object> values)
		{
			ColumnParams params=new ColumnParams(getMode());
			params.setParams(values);
			return params;
		}
		
		@Override
		public void validate()
		{
			super.validate();
			if (!StringHelper.hasContent(title))
				throw new CException("title is not set");
		}
		
		public String getGroup(){return this.group;}
		public void setGroup(final String group){this.group=group;}
		
		public String getEntity(){return this.entity;}
		public void setEntity(final String entity){this.entity=entity;}

		public String getTitle(){return this.title;}
		public void setTitle(final String title){this.title=title;}

		public String getPath(){return this.path;}
		public void setPath(final String path){this.path=path;}

		public String getIcon(){return this.icon;}
		public void setIcon(final String icon){this.icon=icon;}

		public String getDetail(){return this.detail;}
		public void setDetail(final String detail){this.detail=detail;}

		public boolean getLazy(){return this.lazy;}
		public void setLazy(final boolean lazy){this.lazy=lazy;}

		public boolean getAddbutton(){return this.addbutton;}
		public void setAddbutton(final boolean addbutton){this.addbutton=addbutton;}

		public boolean getRefreshbutton(){return this.refreshbutton;}
		public void setRefreshbutton(final boolean refreshbutton){this.refreshbutton=refreshbutton;}
		
		public boolean getAutohide(){return this.autohide;}
		public void setAutohide(final boolean autohide){this.autohide=autohide;}
		
		public boolean getBookmarkcolumn(){return this.bookmarkcolumn;}
		public void setBookmarkcolumn(final boolean bookmarkcolumn){this.bookmarkcolumn=bookmarkcolumn;}

		public boolean getColtoggler(){return this.coltoggler;}
		public void setColtoggler(final boolean coltoggler){this.coltoggler=coltoggler;}

		public ContainerType getContainer(){return this.container;}
		public void setContainer(final ContainerType container){this.container=container;}
		
		public boolean getContextmenu(){return this.contextmenu;}
		public void setContextmenu(final boolean contextmenu){this.contextmenu=contextmenu;}

		public boolean getDeletecolumn(){return this.deletecolumn;}
		public void setDeletecolumn(final boolean deletecolumn){this.deletecolumn=deletecolumn;}

		public boolean getDownloadcolumn(){return this.downloadcolumn;}
		public void setDownloadcolumn(final boolean downloadcolumn){this.downloadcolumn=downloadcolumn;}
		
		public boolean getEditable(){return this.editable;}
		public void setEditable(final boolean editable){this.editable=editable;}

		public boolean getEditcolumn(){return this.editcolumn;}
		public void setEditcolumn(final boolean editcolumn){this.editcolumn=editcolumn;}

		public boolean getEditdialog(){return this.editdialog;}
		public void setEditdialog(final boolean editdialog){this.editdialog=editdialog;}

		public boolean getExportable(){return this.exportable;}
		public void setExportable(final boolean exportable){this.exportable=exportable;}

		public boolean getExpandable(){return this.expandable;}
		public void setExpandable(final boolean expandable){this.expandable=expandable;}

		public boolean getFiltered(){return this.filtered;}
		public void setFiltered(final boolean filtered){this.filtered=filtered;}

		public boolean getGlobalfilter(){return this.globalfilter;}
		public void setGlobalfilter(final boolean globalfilter){this.globalfilter=globalfilter;}

		public boolean getHeader(){return this.header;}
		public void setHeader(final boolean header){this.header=header;}

		public boolean getOpencolumn(){return this.opencolumn;}
		public void setOpencolumn(final boolean opencolumn){this.opencolumn=opencolumn;}
		
		public boolean getPaging(){return this.paging;}
		public void setPaging(final boolean paging){this.paging=paging;}

		public boolean getReorderable(){return this.reorderable;}
		public void setReorderable(final boolean reorderable){this.reorderable=reorderable;}
		
		public boolean getResettable(){return this.resettable;}
		public void setResettable(final boolean resettable){this.resettable=resettable;}

		public boolean getResizable(){return this.resizable;}
		public void setResizable(final boolean resizable){this.resizable=resizable;}
		
		public boolean getSelectable(){return this.selectable;}
		public void setSelectable(final boolean selectable){this.selectable=selectable;}

		public boolean getSortable(){return this.sortable;}
		public void setSortable(final boolean sortable){this.sortable=sortable;}

		public boolean getToolbar(){return this.toolbar;}
		public void setToolbar(final boolean toolbar){this.toolbar=toolbar;}
		
		public boolean getStateful(){return this.stateful;}
		public void setStateful(final boolean stateful){this.stateful=stateful;}
		
		public boolean getDynamic(){return this.dynamic;}
		public void setDynamic(final boolean dynamic){this.dynamic=dynamic;}
	}

	//////////////////////////////////////////////////////////////////////////
	
	public static class ColumnParams extends AbstractParams
	{
		private String header;
		private String body;
		private boolean filter=true;
		private String formatter;
		private boolean visible=true;
		private Style style=new Style();
		private Integer width;
		private HorizontalAlignment align=HorizontalAlignment.CENTER;
		
		public ColumnParams(){}
		
		public ColumnParams(Mode mode)
		{
			setMode(mode);
		}
		
		@Override
		public void validate()
		{
			super.validate();
			if (!StringHelper.hasContent(header))
				throw new CException("header is not set");
			if (!StringHelper.hasContent(body))
				throw new CException("body is not set");
			if (width==null)
				throw new CException("width is not set");
		}

		public String getHeader(){return this.header;}
		public void setHeader(final String header){this.header=header;}

		public String getBody(){return this.body;}
		public void setBody(final String body){this.body=body;}
		
		public boolean getFilter(){return this.filter;}
		public void setFilter(final boolean filter){this.filter=filter;}

		public String getFormatter(){return this.formatter;}
		public void setFormatter(final String formatter){this.formatter=formatter;}

		public boolean getVisible(){return this.visible;}
		public void setVisible(final boolean visible){this.visible=visible;}
		
		public Style getStyle(){return this.style;}
		public void setStyle(final Style style){this.style=style;}

		public Integer getWidth(){return this.width;}
		public void setWidth(final Integer width){this.width=width;}

		public HorizontalAlignment getAlign(){return this.align;}
		public void setAlign(final HorizontalAlignment align){this.align=align;}
	}
}

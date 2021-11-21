package org.biobrief.generator.angular;

import java.util.List;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.generator.GeneratorConstants;
import org.biobrief.generator.GeneratorConstants.ContainerType;
import org.biobrief.generator.I18n;
import org.biobrief.generator.Util;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.BookmarkColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.DeleteColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.DownloadColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.EditColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.ExpanderColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.FieldColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.OpenColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractColumn.SelectionColumn;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.BodyTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.CaptionTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.ColgroupTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.EmptyMessageTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.HeaderTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.PaginatorLeftTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.PaginatorRightTemplate;
import org.biobrief.generator.angular.PrimeTable.AbstractTemplate.RowExpanderTemplate;
import org.biobrief.generator.angular.PrimeTable.Body.BooleanBody;
import org.biobrief.generator.angular.PrimeTable.Body.DateBody;
import org.biobrief.generator.angular.PrimeTable.Body.EntityBody;
import org.biobrief.generator.angular.PrimeTable.Body.EnumBody;
import org.biobrief.generator.angular.PrimeTable.Body.TextBody;
import org.biobrief.generator.angular.PrimeTable.Container.Fieldset;
import org.biobrief.generator.angular.PrimeTable.Container.Panel;
import org.biobrief.generator.angular.PrimeTable.Filter.DateFilter;
import org.biobrief.generator.angular.PrimeTable.Filter.EntityFilter;
import org.biobrief.generator.angular.PrimeTable.Filter.EnumFilter;
import org.biobrief.generator.angular.PrimeTable.Filter.NullFilter;
import org.biobrief.generator.angular.PrimeTable.Filter.TextFilter;
import org.biobrief.generator.templates.Grid;
import org.biobrief.generator.templates.Style;
import org.biobrief.generator.templates.TemplateUtils;
import org.biobrief.util.CException;
import org.biobrief.util.Constants;
import org.biobrief.util.SimpleMap;
import org.biobrief.util.StringHelper;
import org.biobrief.util.UnhandledCaseException;

import com.google.common.collect.Lists;

public class PrimeTable extends AbstractAngularGrid
{
	private final TurboTable table;
	
	public PrimeTable(Grid grid)
	{
		super(grid);
		table=new TurboTable(grid);
	}
	
	@Override
	protected void render(RenderParams params, StringBuilder buffer)
	{
		renderTable(params, buffer);
		renderContextMenu(params, buffer);
	}
	
	private void renderTable(RenderParams params, StringBuilder buffer)
	{
		if (grid.getParams().getContainer()==ContainerType.panel)
		{
			Panel panel=new Panel(grid);
			panel.add(table);
			panel.render(params, buffer);
		}
		else if (grid.getParams().getContainer()==ContainerType.fieldset)
		{
			Fieldset fieldset=new Fieldset(grid);
			fieldset.add(table);
			fieldset.render(params, buffer);
		}
		else if (grid.getParams().getContainer()==ContainerType.none)
		{
			table.render(params, buffer);
		}
		else throw new UnhandledCaseException(grid.getParams().getContainer().name());
	}
	
	private void renderContextMenu(RenderParams params, StringBuilder buffer)
	{
		if (grid.getParams().getContextmenu())
			buffer.append("\n<p-contextMenu #cm [model]=\"contextMenu\"></p-contextMenu>\n\n");//"+grid.getId()+"
	}
	
	@Override
	public String toTypescript()
	{
		return table.renderColumnOptions();
	}

	public static class TurboTable extends AbstractHtmlRenderer
	{
		protected final Grid grid;
		protected final String path="rows";
		protected final CaptionTemplate caption;
		protected final ColgroupTemplate colgroups;
		protected final HeaderTemplate header;
		protected final BodyTemplate body;
		protected final RowExpanderTemplate expander;
		protected final EmptyMessageTemplate empty;
		protected final PaginatorLeftTemplate paginatorLeft;
		protected final PaginatorRightTemplate paginatorRight;
		protected final FieldColumns fieldColumns;
		protected final List<Column> columns=Lists.newArrayList();
		
		public TurboTable(Grid grid)
		{
			this.grid=grid;
			this.caption=new CaptionTemplate(this);
			this.colgroups=new ColgroupTemplate(this);
			this.header=new HeaderTemplate(this);
			this.body=new BodyTemplate(this);
			this.expander=new RowExpanderTemplate(this);
			this.empty=new EmptyMessageTemplate(this);
			this.paginatorLeft=new PaginatorLeftTemplate(this);
			this.paginatorRight=new PaginatorRightTemplate(this);
			this.fieldColumns=new FieldColumns(grid);
			
			if (grid.getParams().getSelectable())
				columns.add(new SelectionColumn(grid));
			if (expander.isEnabled())
				columns.add(new ExpanderColumn(grid));
			if (grid.getParams().getOpencolumn())
				columns.add(new OpenColumn(grid));
			if (grid.getParams().getBookmarkcolumn())
				columns.add(new BookmarkColumn(grid));
			columns.add(fieldColumns);
			if (grid.getParams().getDownloadcolumn())
				columns.add(new DownloadColumn(grid));
			if (grid.getParams().getEditcolumn())
				columns.add(new EditColumn(grid));
			if (grid.getParams().getDeletecolumn())
				columns.add(new DeleteColumn(grid));
		}
				
		public Integer getNumFixedColumns()
		{
			return columns.size()-1;
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			if (params.isAngular())
				renderAngular(params, buffer);
//			else if (params.isFreemarker())
//				renderFreemarker(params, buffer);
			else throw new CException("no handler for render mode: "+params.getMode());
		}
		
		protected void renderAngular(RenderParams params, StringBuilder buffer)
		{
			buffer.append("<p-table");
			renderAttributes(params, buffer);
			buffer.append(">\n");
			caption.render(params, buffer);
			paginatorLeft.render(params, buffer);
			paginatorRight.render(params, buffer);
			colgroups.render(params, buffer);
			header.render(params, buffer);
			body.render(params, buffer);
			expander.render(params, buffer);
			empty.render(params, buffer);
			buffer.append("</p-table>\n");
		}
		
		protected void renderAttributes(RenderParams params, StringBuilder buffer)
		{
			attr(buffer, "#grid");
			attr(buffer, "[value]", path);
			attr(buffer, "dataKey", "id");
			attr(buffer, "[columns]", "selectedColumns");
			//attr(buffer, "[virtualScroll]", true);
			
			//attrIf(buffer, "[resizableColumns]", "true", grid.getParams().getResizable());
			attrIf(buffer, "[reorderableColumns]", "true", grid.getParams().getReorderable());
			attrIf(buffer, "sortMode", "multiple", grid.getParams().getSortable());
			
			if (grid.getParams().getResizable())
			{
				attr(buffer, "[resizableColumns]", "true");
				attr(buffer, "columnResizeMode", "fit");//expand
			}			
			if (grid.getParams().getContextmenu())
			{
				attr(buffer, "[contextMenu]", "cm");
				attr(buffer, "[(contextMenuSelection)]", "selected");
			}				
			if (grid.getParams().getSelectable())
			{
				attr(buffer, "[(selection)]", "selected");
				//attr(buffer, "selectionMode", "multiple");
				attr(buffer, "(onRowSelect)", "onRowSelect($event)");
				attr(buffer, "(onRowUnselect)", "onRowUnselect($event)");
			}			
			if (grid.getParams().getPaging())
			{
				attr(buffer, "[paginator]", "true");
				attr(buffer, "[rows]", "rowsPerPage");
				attr(buffer, "[pageLinks]", "pageLinks");
				attr(buffer, "[rowsPerPageOptions]", "rowsPerPageOptions");
				attr(buffer, "[paginatorPosition]", "paginatorPosition");
				attr(buffer, "[alwaysShowPaginator]", "true");
			}
			
			if (grid.getParams().getExportable())
			{
				//attr(buffer, "csvSeparator", ",");
				attr(buffer, "exportFilename", grid.getEntity().getCollection());
			}
			attr(buffer, "[totalRecords]", "totalRecords");
			attr(buffer, "[loading]", "loading");
			if (grid.getParams().getLazy())
			{				
				attr(buffer, "[lazy]", "true");
				attr(buffer, "(onLazyLoad)", "lazyLoad($event)");
			}
			if (grid.getParams().getStateful())
			{
				attr(buffer, "stateStorage", "session");
				attr(buffer, "stateKey", grid.getEntity().getCollection());
			}
		}
		
		public String renderColumnOptions()
		{
			return fieldColumns.renderColumnOptions();
		}
	}
	
	public static class FieldColumns extends AbstractHtmlRenderer implements Column
	{
		private List<FieldColumn> columns=Lists.newArrayList();
		private boolean resizable;
		private boolean reorderable;
		private boolean sortable;
		
		public FieldColumns(Grid grid)
		{
			this.resizable=grid.getParams().getResizable();
			this.reorderable=grid.getParams().getReorderable();
			this.sortable=grid.getParams().getSortable();			
			for (Grid.Column column : grid.getColumns())
			{
				add(new FieldColumn(column));
			}
			//addAuditColumns(grid);
		}
		
		public String renderColumnOptions()
		{
			List<String> items=Lists.newArrayList();
			for (FieldColumn column : columns)
			{
				items.add(column.renderTypescript());
			}		
			StringBuilder buffer=new StringBuilder();
			buffer.append("this.cols = [\n");
			buffer.append(StringHelper.indent(StringHelper.join(items, ",\n")));
			buffer.append("\n];\n");
			return buffer.toString();
		}
		
		public void add(FieldColumn column)
		{
			this.columns.add(column);
		}
		
		public void addAuditColumns(Grid grid)
		{
			addAuditColumn(grid, Constants.CREATED_DATE);
			addAuditColumn(grid, Constants.CREATED_BY);
			addAuditColumn(grid, Constants.LAST_MODIFIED_DATE);
			addAuditColumn(grid, Constants.LAST_MODIFIED_BY);
		}
		
		private void addAuditColumn(Grid grid, String name)
		{
			if (!grid.getEntity().hasFieldDefinition(name))
				return;
			FieldDefinition field=grid.getEntity().getFieldDefinition(name);
			add(createAuditColumn(grid, field));
		}
		
		private FieldColumn createAuditColumn(Grid grid, FieldDefinition field)
		{
			Integer width=100;
			HorizontalAlignment align=HorizontalAlignment.CENTER;
			String header="i18n."+field.getName();
			String body="${"+field.getName()+"}";
			boolean filtered=true;
			boolean visible=false;
			return new FieldColumn(grid, width, align, field, header, body, filtered, visible);
		}

		//buffer.append("\t<col *ngFor=\"let col of columns\" [style.width]=\"col.width\">\n");
		@Override
		public String renderColgroup(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<col");
			attr(buffer, "*ngFor", "let col of columns");
			attr(buffer, "[style.width]", "col.width");
			buffer.append(">\n");
			return buffer.toString();
		}
		
		@Override
		public String renderHeader(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<th");
			attr(buffer, "*ngFor", "let col of columns");
			attr(buffer, "[ngSwitch]", "col.field");
			attr(buffer, "[style.width]", "col.width");
			attrIf(buffer, "pResizableColumn", resizable);
			attrIf(buffer, "pReorderableColumn", reorderable);
			attrIf(buffer, "[pSortableColumn]", "col.field", sortable);
			buffer.append(">\n");
			for (FieldColumn column : columns)
			{
				buffer.append("\t");
				column.renderHeader(params, buffer);
				buffer.append("\n");
			}			
			buffer.append("</th>\n");
			return buffer.toString();
		}
		
		@Override
		public String renderFilter(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<th");
			attr(buffer, "*ngFor", "let col of columns");
			attr(buffer, "[ngSwitch]", "col.field");
			attr(buffer, "class", "ui-resizable-column");
			buffer.append(">\n");
			for (FieldColumn column : columns)
			{
				buffer.append("\t");
				column.renderFilter(params, buffer);
				buffer.append("\n");
			}
			buffer.append("</th>\n");
			return buffer.toString();
		}
		
		@Override
		public String renderBody(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<td");
			attr(buffer, "*ngFor", "let col of columns");
			attr(buffer, "[ngSwitch]", "col.field");
			attr(buffer, "[style.maxWidth]", "col.width");// pending
			attr(buffer, "class", "ui-resizable-column");
			buffer.append(">\n");
			for (FieldColumn column : columns)
			{
				buffer.append("\t");
				column.renderBody(params, buffer);
				buffer.append("\n");
			}
			buffer.append("</td>\n");
			return buffer.toString();
		}

		@Override
		protected void render(RenderParams params, StringBuilder buffer) {}
	}
	
	////////////////////////////////////////////////////////
	
	public static abstract class AbstractTemplate extends AbstractHtmlRenderer
	{
		protected final TurboTable table;
		protected final String type;
		
		public AbstractTemplate(TurboTable table, String type)
		{
			this.table=table;
			this.type=type;
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			buffer.append("<ng-template");
			attr(buffer, "pTemplate", type);
			renderAttributes(params, buffer);
			buffer.append(">\n");
			buffer.append(indent(renderBody(params))).append("\n");
			buffer.append("</ng-template>\n");
		}
		
		private String renderBody(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			renderBody(params, buffer);
			return buffer.toString();
		}
		
		protected void renderAttributes(RenderParams params, StringBuilder buffer){}

		protected void renderBody(RenderParams params, StringBuilder buffer){}
		
		
		//////////////////////////////////////////////////////////////////////////
		
		public static class ColgroupTemplate extends AbstractTemplate
		{
			public ColgroupTemplate(TurboTable table)
			{
				super(table, "colgroup");
			}
			
			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-columns");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<colgroup>\n");
				for (Column column : table.columns)
				{
					buffer.append(indent(column.renderColgroup(params))).append("\n");
				}
				buffer.append("</colgroup>\n");
			}
		}
		
		///////////////////////////////////////////////////////////
		
		public static class CaptionTemplate extends AbstractTemplate
		{
			private final GeneratorConstants.Icon icon;
			private final boolean globalfilter;
			private final boolean coltoggler;
			private final boolean refreshbutton;
			private final boolean exportbutton;
			
			public CaptionTemplate(TurboTable table)
			{
				super(table, "caption");
				this.icon=GeneratorConstants.Icon.find(table.grid.getParams().getIcon(), GeneratorConstants.Icon.GRID);
				this.globalfilter=table.grid.getParams().getGlobalfilter();
				this.coltoggler=table.grid.getParams().getColtoggler();
				this.refreshbutton=table.grid.getParams().getRefreshbutton();
				this.exportbutton=table.grid.getParams().getExportable();
			}
			
			/*
			<div class="p-formgroup-inline">
				<div class="p-field">
					<div class="p-inputgroup">
						<span class="p-inputgroup-addon"><i class="fa fa-search"></i></span>
						<input type="text" pInputText [placeholder]="globalFilterPlaceholder" (input)="grid.filterGlobal($event.target.value, 'contains')" class="globalfilter">
					</div>
				</div>
				<div class="p-field">
					<p-multiSelect [options]="cols" [(ngModel)]="selectedColumns" optionLabel="header"
						[selectedItemsLabel]="selectedColumnsLabel" [style]="{minWidth: '100px', alignSelf: 'center', marginLeft: '2px', marginRight: '2px'}" defaultLabel="Choose columns"></p-multiSelect>
				</div>
				<p-button icon="fa fa-refresh" styleClass="p-button-raised p-button-secondary" (onClick)="refresh($event)" [style]="{marginRight: '.25em'}"></p-button>
				<p-splitButton icon="fa fa-download" (onClick)="grid.exportCSV()" [model]="exportOptions" [style]="{marginRight: '2px'}"></p-splitButton>
			</div>
			*/
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<div class=\"p-formgroup-inline\">\n");
				//buffer.append("<i class=\"fa "+icon.getCls()+" fa-lg fa-fw\" style=\"align-self: center; margin-right: 2px\"></i>\n");				 
				if (globalfilter)
				{
					buffer.append("<div class=\"p-field\">\n");
					buffer.append("\t<div class=\"p-inputgroup\">\n");
					buffer.append("\t\t<span class=\"p-inputgroup-addon\"><i class=\"fa fa-search\"></i></span>\n");
					buffer.append("\t\t<input type=\"text\" pInputText [placeholder]=\"globalFilterPlaceholder\"");
					buffer.append(" (input)=\"grid.filterGlobal($event.target.value, 'contains')\" class=\"globalfilter\">\n");
					buffer.append("\t</div>\n");
					buffer.append("</div>\n");
				}
				if (coltoggler)
				{
					buffer.append("<div class=\"p-field\">\n");
					buffer.append("\t<p-multiSelect [options]=\"cols\"");
					buffer.append(" [(ngModel)]=\"selectedColumns\"");
					buffer.append(" optionLabel=\"header\"");
					buffer.append(" [selectedItemsLabel]=\"selectedColumnsLabel\"");
					buffer.append(" [style]=\"{minWidth: '100px', alignSelf: 'center', marginLeft: '2px', marginRight: '2px'}\"");
					buffer.append(" defaultLabel=\"Choose columns\">");
					buffer.append("\t</p-multiSelect>\n");
					buffer.append("</div>\n");
				}
				if (refreshbutton)
					buffer.append("<p-button icon=\"fa fa-refresh\" styleClass=\"ui-button-raised ui-button-secondary\" (onClick)=\"refresh($event)\" [style]=\"{marginRight: '.25em'}\"></p-button>\n");
				if (exportbutton)
					buffer.append("<p-splitButton icon=\"fa fa-download\" (onClick)=\"grid.exportCSV()\" [model]=\"exportOptions\" [style]=\"{marginRight: '2px'}\"></p-splitButton>\n");
				buffer.append("</div>\n");
			}
			
//			@Override
//			protected void renderBody(RenderParams params, StringBuilder buffer)
//			{
//				buffer.append("<div class=\"flexrow\">\n");
//				buffer.append("<i class=\"fa "+icon.getCls()+" fa-lg fa-fw\" style=\"align-self: center; margin-right: 2px\"></i>\n");				 
//				if (globalfilter)
//				{
//					buffer.append("<div class=\"ui-inputgroup\">\n");
//					buffer.append("\t<span class=\"ui-inputgroup-addon\">");
//					buffer.append("<i class=\"fa fa-search\" style=\"margin:4px 4px 0 0\"></i>\n");
//					buffer.append("</span>\n");
//					buffer.append("<input type=\"text\" pInputText");// size=\"30\"
//					buffer.append(" [placeholder]=\"globalFilterPlaceholder\"");
//					buffer.append(" (input)=\"grid.filterGlobal($event.target.value, 'contains')\"");
//					//buffer.append(" style=\"width:auto\">\n");
//					buffer.append(" class=\"globalfilter\">\n");
//					buffer.append("</div>\n");
//				}
//				if (coltoggler)
//				{
//					buffer.append("<p-multiSelect [options]=\"cols\"");
//					buffer.append(" [(ngModel)]=\"selectedColumns\"");
//					buffer.append(" optionLabel=\"header\"\n");
//					buffer.append(" [selectedItemsLabel]=\"selectedColumnsLabel\"");
//					buffer.append(" [style]=\"{minWidth: '100px', alignSelf: 'center', marginLeft: '2px', marginRight: '2px'}\"");
//					buffer.append(" defaultLabel=\"Choose columns\">");
//					buffer.append("</p-multiSelect>\n");
//				}
//				if (refreshbutton)
//					buffer.append("<p-button icon=\"fa fa-refresh\" styleClass=\"ui-button-raised ui-button-secondary\" (onClick)=\"refresh($event)\" [style]=\"{marginRight: '.25em'}\"></p-button>\n");
//				if (exportbutton)
//					buffer.append("<p-splitButton icon=\"fa fa-download\" (onClick)=\"grid.exportCSV()\" [model]=\"exportOptions\" [style]=\"{marginRight: '2px'}\"></p-splitButton>\n");
//				
//				//buffer.append("<span style=\"align-self: center\">{{itemRange(page)}}</span>\n");
//				buffer.append("</div>\n");
//			}
		}
		
		public static class HeaderTemplate extends AbstractTemplate
		{
			private final Style headerStyle=new Style();
			private final Style filterStyle=new Style();
			
			public HeaderTemplate(TurboTable table)
			{
				super(table, "header");
				headerStyle.addClass("header-row");
				filterStyle.addClass("filter-row");
			}
			
			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-columns");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				renderHeaderRow(params, buffer);
				renderFilterRow(params, buffer);
			}
			
			protected void renderHeaderRow(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<tr");
				attrStyle(buffer, headerStyle);
				buffer.append(">\n");
				for (Column column : table.columns)
				{
					buffer.append(indent(column.renderHeader(params))).append("\n");
				}
				buffer.append("</tr>\n");
			}
			
			protected void renderFilterRow(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<tr");
				attrStyle(buffer, filterStyle);
				buffer.append(">\n");
				for (Column column : table.columns)
				{
					buffer.append(indent(column.renderFilter(params))).append("\n");
				}
				buffer.append("</tr>\n");
			}
		}	
		
		public static class BodyTemplate extends AbstractTemplate
		{			
			//private final Style style=new Style();
			
			public BodyTemplate(TurboTable table)
			{
				super(table, "body");
				//style.addClass("body-row");
			}

			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-columns", "columns");
				attr(buffer, "let-rowData");
				attr(buffer, "let-rowIndex", "rowIndex");
				attr(buffer, "let-expanded", "expanded");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<tr");
				//attrStyle(buffer, style);
				attr(buffer, "[ngClass]", "rowClass(rowData)");
				attr(buffer, "[pSelectableRow]", "rowData");
				attr(buffer, "[pContextMenuRow]", "rowData");
				buffer.append(">\n");
				for (Column column : table.columns)
				{
					buffer.append(indent(column.renderBody(params))).append("\n");
				}
				buffer.append("</tr>\n");
			}
		}
		
		public static class RowExpanderTemplate extends AbstractTemplate
		{		
			private final String template;
					
			public RowExpanderTemplate(TurboTable table)
			{
				super(table, "rowexpansion");
				this.template=getTemplate(table);
			}

			private static String getTemplate(TurboTable table)
			{
				String template=table.grid.getParams().getDetail();
				if (StringHelper.hasContent(template))
					return TemplateUtils.formatTemplate(template);
				else return "<yaml [model]=\"rowData\"></yaml>";
				//else return "<json [model]=\"rowData\"></json>";
				//else return "<div class=\"jsonexpander\"><pre>{{enums.diagnostic(rowData)}}</pre></div>";
			}
			
			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-columns", "columns");
				attr(buffer, "let-rowData");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<tr>\n");
				buffer.append("\t<td [attr.colspan]=\"columns.length+"+table.getNumFixedColumns()+"\">"+template+"</td>\n");
				buffer.append("</tr>\n");
			}
			
			public boolean isEnabled() {return table.grid.getParams().getExpandable();}
		}
		
		public static class EmptyMessageTemplate extends AbstractTemplate
		{			
			public EmptyMessageTemplate(TurboTable table)
			{
				super(table, "emptymessage");
			}
			
			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-columns");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<tr>\n");
				buffer.append("\t<td [attr.colspan]=\"columns.length+"+table.getNumFixedColumns()+"\">{{emptyText}}</td>\n");
				buffer.append("</tr>\n");
			}
		}
		
		/*
		public static class PaginatorLeftTemplate extends AbstractTemplate
		{
			private final boolean globalfilter;
			private final boolean coltoggler;
			private final boolean refreshbutton;
			private final boolean exportbutton;
			
			public PaginatorLeftTemplate(TurboTable table)
			{
				super(table, "paginatorleft");
				this.globalfilter=table.grid.getParams().getGlobalfilter();
				this.coltoggler=table.grid.getParams().getColtoggler();
				this.refreshbutton=table.grid.getParams().getRefreshbutton();
				this.exportbutton=table.grid.getParams().getExportable();
			}
			
			@Override
			protected void renderAttributes(RenderParams params, StringBuilder buffer)
			{
				attr(buffer, "let-page");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<div class=\"flexrow\">\n");
				if (globalfilter)
				{
					buffer.append("<div class=\"ui-inputgroup\">\n");
					buffer.append("\t<span class=\"ui-inputgroup-addon\">");
					buffer.append("<i class=\"fa fa-search\" style=\"margin:4px 4px 0 0\"></i>\n");
					buffer.append("</span>\n");
					buffer.append("<input type=\"text\" pInputText");// size=\"30\"
					buffer.append(" [placeholder]=\"globalFilterPlaceholder\"");
					buffer.append(" (input)=\"grid.filterGlobal($event.target.value, 'contains')\"");
					//buffer.append(" style=\"width:auto\">\n");
					buffer.append(" class=\"globalfilter\">\n");
					buffer.append("</div>\n");
				}
				if (coltoggler)
				{
					buffer.append("<p-multiSelect [options]=\"cols\"");
					buffer.append(" [(ngModel)]=\"selectedColumns\"");
					buffer.append(" optionLabel=\"header\"\n");
					buffer.append(" [selectedItemsLabel]=\"selectedColumnsLabel\"");
					buffer.append(" [style]=\"{minWidth: '100px', alignSelf: 'center', marginLeft: '2px', marginRight: '2px'}\"");
					buffer.append(" defaultLabel=\"Choose columns\">");
					buffer.append("</p-multiSelect>\n");
				}
				if (refreshbutton)
					buffer.append("<p-button icon=\"fa fa-refresh\" styleClass=\"ui-button-raised ui-button-secondary\" (onClick)=\"refresh($event)\" [style]=\"{marginRight: '.25em'}\"></p-button>");
				if (exportbutton)
					buffer.append("<p-splitButton icon=\"fa fa-download\" (onClick)=\"grid.exportCSV()\" [model]=\"exportOptions\" [style]=\"{marginRight: '2px'}\"></p-splitButton>\n");
				
				//buffer.append("<span style=\"align-self: center\">{{itemRange(page)}}</span>\n");
				buffer.append("</div>\n");
			}
		}
		*/
		
		public static class PaginatorLeftTemplate extends AbstractTemplate
		{			
			public PaginatorLeftTemplate(TurboTable table)
			{
				super(table, "paginatorleft");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<ng-content></ng-content>");
			}
		}
		
		public static class PaginatorRightTemplate extends AbstractTemplate
		{			
			public PaginatorRightTemplate(TurboTable table)
			{
				super(table, "paginatorright");
			}
			
			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<ng-content></ng-content>");
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	
	public interface Column
	{
		String renderColgroup(RenderParams params);
		String renderHeader(RenderParams params);
		String renderFilter(RenderParams params);
		String renderBody(RenderParams params);
	}
	
	public static abstract class AbstractColumn implements Column
	{
		protected String header;
		protected final Integer width;
		protected final Style headerStyle=new Style();
		protected final Style filterStyle=new Style();
		protected final Style bodyStyle=new Style();
		protected final boolean resizable;
		protected boolean visible=true;
				
		public AbstractColumn(Grid grid, Integer width, HorizontalAlignment align)
		{
			this.width=width;
			this.headerStyle.width(width);
			headerStyle.align(TemplateUtils.getHorizontalAlignment(align));
			bodyStyle.align(TemplateUtils.getHorizontalAlignment(align));
			this.resizable=grid.getParams().getResizable();
			if (this.resizable)
				bodyStyle.addClass("ui-resizable-column");
		}

		public String renderColgroup(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<col");
			renderColgroupAttributes(params, buffer);
			buffer.append(">");
			return buffer.toString();
		}
		
		public String renderHeader(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<th");
			renderHeaderAttributes(params, buffer);
			buffer.append(">");
			renderHeader(params, buffer);
			buffer.append("</th>");
			return buffer.toString();
		}
		
		public String renderFilter(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<th");
			renderFilterAttributes(params, buffer);
			buffer.append(">");
			renderFilter(params, buffer);
			buffer.append("</th>");
			return buffer.toString();
		}
		
		public String renderBody(RenderParams params)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<td");
			renderBodyAttributes(params, buffer);
			buffer.append(">");
			renderBody(params, buffer);
			buffer.append("</td>");
			return buffer.toString();
		}
		
		//////////////////////////////
		
		protected void renderColgroupAttributes(RenderParams params, StringBuilder buffer)
		{
			attr(buffer, "[style.width]", width);
			//attr(buffer, "[style.maxWidth]", width);
		}
		
		protected void renderHeaderAttributes(RenderParams params, StringBuilder buffer)
		{
			attrStyle(buffer, headerStyle);
			attrIf(buffer, "pResizableColumn", resizable);	
		}
		
		protected void renderFilterAttributes(RenderParams params, StringBuilder buffer)
		{
			attrStyle(buffer, filterStyle);
		}
		
		protected void renderBodyAttributes(RenderParams params, StringBuilder buffer)
		{
			attrStyle(buffer, bodyStyle);
		}
		
		protected void renderHeader(RenderParams params, StringBuilder buffer)
		{
			buffer.append(Util.renderText(params, header));
		}
		
		protected void renderFilter(RenderParams params, StringBuilder buffer){}
		
		protected void renderBody(RenderParams params, StringBuilder buffer)
		{
			buffer.append("BODY");
		}

		protected void toTypescript(SimpleMap map){}
	
		//////////////////////////////////////////////
		
		public static class FieldColumn extends AbstractColumn
		{
			protected final String field;
			protected final Filter filter;
			protected final Body body;
			
			public FieldColumn(Grid grid, Integer width, 
					HorizontalAlignment align, FieldDefinition field,
					String header, String body, boolean filtered, boolean visible)
			{
				super(grid, width, align);
				this.field=field.getName();
				this.header=header;
				this.body=getBody(body, field);
				this.filter=getFilter(field, filtered);
				this.visible=visible;
			}
			
			public FieldColumn(Grid.Column column)
			{
				super(column.getGrid(), column.getParams().getWidth(), column.getParams().getAlign());
				this.field=column.getField().getName();
				this.header=column.getHeader();
				this.body=getBody(column.getBody(), column.getField());
				this.filter=getFilter(column.getField(), column.isFiltered());
				this.visible=column.getParams().getVisible();
			}
					
			@Override
			protected void renderHeaderAttributes(RenderParams params, StringBuilder buffer)
			{
				super.renderHeaderAttributes(params, buffer);
				attr(buffer, "pSortableColumn", field);
			}
	
			@Override
			protected void renderHeader(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<colname-renderer");
				switchCase(buffer, this.field);
				attr(buffer, "title", Util.renderText(params, header));
				buffer.append(">");
				super.renderHeader(params, buffer);
				buffer.append("<p-sortIcon field=\""+field+"\"></p-sortIcon>");
				buffer.append("</colname-renderer>");
			}
			
//			@Override
//			protected void renderHeader(RenderParams params, StringBuilder buffer)
//			{
//				buffer.append("<span");
//				switchCase(buffer, this.field);
//				attr(buffer, "title", Util.renderText(params, header));
//				buffer.append(">");
//				super.renderHeader(params, buffer);
//				buffer.append("<p-sortIcon field=\""+field+"\"></p-sortIcon>");
//				buffer.append("</span>");
//			}
			
			@Override
			protected void renderFilter(RenderParams params, StringBuilder buffer)
			{
				super.renderFilter(params, buffer);
				filter.render(params, buffer);
			}
			
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				body.render(params, buffer);
			}
			
			private Filter getFilter(FieldDefinition field, boolean filtered)
			{
				if (!filtered)
					return new NullFilter(field);
				switch(field.getFieldType())
				{
				case DATE:
					return new DateFilter(field);
				case ENUM:
					return new EnumFilter(field);
				case ENTITY:
				case MASTER:
				case REF:
					return new EntityFilter(field);
				default:
					return new TextFilter(field);
				}
			}
			
			private Body getBody(String body, FieldDefinition field)
			{
				switch(field.getFieldType())
				{
				case ENUM:
					return new EnumBody(body, field);
				case BOOLEAN:
					return new BooleanBody(body, field);
				case DATE:
					return new DateBody(body, field);
				case MASTER:
				case ENTITY:
				case REF:
					return new EntityBody(body, field);
				default:
					return new TextBody(body, field);
				}
			}
			
			public String renderTypescript()
			{
				String field=StringHelper.singleQuote(this.field);
				String header=this.header;
				if (!Util.isI18n(header))
					header=StringHelper.doubleQuote(header);
				else header="this."+header;
				return "{field: "+field+", header: "+header+", width: '"+width+"px', visible: "+visible+"}";
			}
		}
		
		///////////////////////////////////////////////////////

		public static class ExpanderColumn extends AbstractColumn
		{
			public ExpanderColumn(Grid grid)
			{
				super(grid, 40, HorizontalAlignment.CENTER);
				header=I18n.expand.getText();
			}
			
			@Override
			protected void renderHeader(RenderParams params, StringBuilder buffer)
			{
				buffer.append(Util.renderText(params, header));
			}

			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("\n\t<a href=\"#\" [pRowToggler]=\"rowData\">\n");
				buffer.append("\t\t<i [ngClass]=\"expanded ? 'fa fa-fw fa-chevron-circle-down' : 'fa fa-fw fa-chevron-circle-right'\"></i>\n");
				buffer.append("\t</a>\n");
			}
		}
		
		//////////////////////////////////////////////
		
		public static class SelectionColumn extends AbstractColumn
		{
			public SelectionColumn(Grid grid)
			{
				super(grid, 40, HorizontalAlignment.CENTER);
				
			}

			@Override
			protected void renderHeader(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<p-tableHeaderCheckbox></p-tableHeaderCheckbox>");
			}

			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<p-tableCheckbox [value]=\"rowData\"></p-tableCheckbox>");
			}
		}
		
		///////////////////////////////////////////////////////
		
		public static abstract class ButtonColumn extends AbstractColumn
		{
			protected final Button button;
			
			public ButtonColumn(Grid grid, I18n label, GeneratorConstants.Icon icon, String onclick)
			{
				super(grid, 50, HorizontalAlignment.CENTER);
				this.header=label.getText();
				this.button=new Button(icon, onclick, true);
			}

			@Override
			protected void renderHeader(RenderParams params, StringBuilder buffer)
			{
				buffer.append(Util.renderText(params, header));
			}

			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				button.render(params, buffer);
			}
		}
		
		//////////////////////////////////////////////
		
		public static class OpenColumn extends ButtonColumn
		{
			public OpenColumn(Grid grid)
			{
				super(grid, I18n.open, GeneratorConstants.Icon.OPEN, "openItem(rowData)");
				this.header="<i class=\""+GeneratorConstants.Icon.OPEN.getCls()+" fa-lg\"></i>";
			}
		}
		
		///////////////////////////////////////////////////////
		
		public static class EditColumn extends ButtonColumn
		{
			public EditColumn(Grid grid)
			{
				super(grid, I18n.edit, GeneratorConstants.Icon.EDIT, "editItem(rowData)");
				String icon=GeneratorConstants.Icon.ADD.getCls();
				this.header="<p-button (onClick)=\"addItem()\" icon=\""+icon+"\"></p-button>";
			}
		}
		
		///////////////////////////////////////////////////////
		
		public static class DeleteColumn extends ButtonColumn
		{
			public DeleteColumn(Grid grid)
			{
				super(grid, I18n.del, GeneratorConstants.Icon.DELETE, "deleteItem(rowData)");
				String icon=GeneratorConstants.Icon.DELETE.getCls();
				this.header="<p-button (onClick)=\"deleteItems()\" icon=\""+icon+"\"></p-button>";
			}
		}
		
		//////////////////////////////////////////////////////////
		
		public static class DownloadColumn extends ButtonColumn
		{
			public DownloadColumn(Grid grid)
			{
				super(grid, I18n.download, GeneratorConstants.Icon.DOWNLOAD, "downloadItem(rowData)");
				String icon=GeneratorConstants.Icon.DOWNLOAD.getCls();
				this.header="<p-button (onClick)=\"downloadItems()\" icon=\""+icon+"\"></p-button>";
			}
		}
		
		///////////////////////////////////////////////////////
		
		public static class BookmarkColumn extends AbstractColumn
		{
			public BookmarkColumn(Grid grid)
			{
				super(grid, 38, HorizontalAlignment.CENTER);
				header=GeneratorConstants.Icon.BOOKMARK_OFF.name();	
			}

			@Override
			protected void renderHeader(RenderParams params, StringBuilder buffer)
			{
				buffer.append(Util.renderText(params, header));
			}

			@Override
			protected void renderBody(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<bookmark-button");
				attr(buffer, "[ngModel]", "item['bookmarked']");
				attr(buffer, "(onChange)", "toggleBookmark(item['id'], $event)");
				buffer.append(">");
				buffer.append("</bookmark-button>\n");
			}
		}
	}
	
	public static class Body extends AbstractHtmlRenderer
	{
		protected final String body;
		
		public Body(String body)
		{
			this.body=body;
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			if (params.isAngular())
				renderAngular(params, buffer);
			else if (params.isFreemarker())
				renderFreemarker(params, buffer);
			else throw new CException("no handler for render mode: "+params.getMode());
		}

		protected void renderAngular(RenderParams params, StringBuilder buffer) {}
		
		protected void renderFreemarker(RenderParams params, StringBuilder buffer) {}
	
		public static abstract class FieldBody extends Body
		{
			protected final FieldDefinition field;
			
			public FieldBody(String body, FieldDefinition field)
			{
				super(body);
				this.field=field;
			}
		}
		
		public static class TextBody extends FieldBody
		{		
			public TextBody(String body, FieldDefinition field)
			{
				super(body, field);
			}
			
			@Override
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				String body=TemplateUtils.formatTemplate(this.body);
				buffer.append("<text-renderer");
				switchCase(buffer, this.field.getName());
				mouseOverText(buffer);				
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append(body);
				buffer.append("</text-renderer>");
			}
			
			protected void mouseOverText(StringBuilder buffer)
			{
				if (body.contains("<"))
					return;
				String title=TemplateUtils.formatTemplate(this.body);
//				title=StringHelper.replace(title, "${", "");
//				title=StringHelper.replace(title, "}", "");
//				System.out.println("getMouseoverText: "+title);
				attr(buffer, "title", title);
			}
			
//			protected void mouseOverText(StringBuilder buffer)
//			{
//				if (body.contains("<"))
//					return;
//				String title=body;
//				title=StringHelper.replace(title, "${", "");
//				title=StringHelper.replace(title, "}", "");
//				System.out.println("getMouseoverText: "+title);
//				attr(buffer, "[title]", title);
//			}
			
			@Override
			protected void renderFreemarker(RenderParams params, StringBuilder buffer)
			{
				String path="item."+field.getName();
				buffer.append(Util.renderFreemarkerField(path, field));
			}
		}
		
		public static class BooleanBody extends FieldBody
		{		
			public BooleanBody(String body, FieldDefinition field)
			{
				super(body, field);
			}
			
			@Override
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<boolean-renderer");
				switchCase(buffer, this.field.getName());
				attr(buffer, "[value]", "rowData."+field.getName());
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</boolean-renderer>");
			}
	
			@Override
			protected void renderFreemarker(RenderParams params, StringBuilder buffer)
			{
				String path="item."+field.getName();
				buffer.append(Util.renderFreemarkerField(path, field));
			}
		}
		
		public static class DateBody extends FieldBody
		{		
			public DateBody(String body, FieldDefinition field)
			{
				super(body, field);
			}
			
			//<date-renderer [date]="rowData.date"></date-renderer>			
			@Override
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<date-renderer");
				switchCase(buffer, this.field.getName());
				attr(buffer, "[date]", "rowData."+field.getName());
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</date-renderer>");
			}
		}
		
		//////////////////////////////////////////////////////
		
		public static class EnumBody extends FieldBody
		{			
			public EnumBody(String body, FieldDefinition field)
			{
				super(body, field);
			}
	
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				String tag=getTag();
				buffer.append("<"+tag);
				switchCase(buffer, this.field.getName());
				attr(buffer, "type", field.getType());
				attr(buffer, "[value]", "rowData."+field.getName());
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</"+tag+">");
			}
			
			private String getTag()
			{
				String type=field.getType().toLowerCase();
				type=StringHelper.replace(type,  "_", "-");
				return type+"-renderer";
			}
			
//			protected void renderAngular(RenderParams params, StringBuilder buffer)
//			{
//				buffer.append("<enum-renderer");
//				switchCase(buffer, this.field.getName());
//				attr(buffer, "type", field.getType());
//				attr(buffer, "[value]", "rowData."+field.getName());
//				attr(buffer, "[grid]", "grid");
//				buffer.append(">");
//				buffer.append("</enum-renderer>");
//			}
			
			protected void renderFreemarker(RenderParams params, StringBuilder buffer)
			{
				buffer.append("${item."+field+"}: <span style=\"color:red\">enums.getEnum('"+field.getType()+"', item[col.field])</span>");
			}
		}
		
		//////////////////////////////////////////////////
		
		public static class EntityBody extends FieldBody
		{
			public EntityBody(String body, FieldDefinition field)
			{
				super(body, field);
			}
	
			//<span *ngSwitchCase="'sample'"><sample-renderer [id]="rowData.sample"></sample-renderer></span>
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				String tag=Util.renderTag(field.getType(), "renderer");
				buffer.append("<"+tag);
				switchCase(buffer, this.field.getName());
				attr(buffer, "type", field.getType());
				attr(buffer, "[id]", "rowData."+field.getName());
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</"+tag+">");
			}
			
//			protected void renderFreemarker(RenderParams params, StringBuilder buffer)
//			{
//				buffer.append("${item."+field+"}: <span style=\"color:red\">enums.getMaster('"+enumtype+"', item[col.field])</span>");
//			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static abstract class Filter extends AbstractHtmlRenderer
	{
		protected final FieldDefinition field;
		protected boolean enabled=true;
		protected String placeholder="";
		protected String matchMode="equals";
		
		public Filter(FieldDefinition field)
		{
			this.field=field;
		}

		protected void switchCase(StringBuilder buffer)
		{
			switchCase(buffer, field.getName());
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer){}

		public boolean isEnabled(){return enabled;}
		public String getPlaceholder(){return placeholder;}
		public String getMatchMode(){return matchMode;}
		
		//////////////////////////////////////////////////////////////////////////////////
	
		public static class NullFilter extends Filter
		{
			public NullFilter(FieldDefinition field)
			{
				super(field);
				enabled=false;
			}
		}
		
		public static class TextFilter extends Filter
		{
			public TextFilter(FieldDefinition field)
			{
				super(field);
			}
			
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<text-filter");
				switchCase(buffer);
				attr(buffer, "(onChange)", "grid.filter($event, '"+field.getName()+"', '"+matchMode+"')");
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</text-filter>");
				
			}
		}

		public static class EnumFilter extends Filter
		{
			public EnumFilter(FieldDefinition field)
			{
				super(field);
			}
			
			//<enum-filter  *ngSwitchCase="'type'" [type]="'RunType'" (onChange)="grid.filter($event, 'type', 'equals')"></enum-filter>
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<enum-filter");
				switchCase(buffer);
				attr(buffer, "type", field.getType());
				attr(buffer, "(onChange)", "grid.filter($event, '"+field.getName()+"', '"+matchMode+"')");
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</enum-filter>");
			}
		}
		
		//<sample-filter *ngSwitchCase="'sample'" (onChange)="grid.filter($event, 'sample', 'equals')"></sample-filter>
		public static class EntityFilter extends Filter
		{			
			public EntityFilter(FieldDefinition field)
			{
				super(field);
			}
	
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				String tag=Util.renderTag(field.getType(), "filter");
				buffer.append("<"+tag);
				switchCase(buffer);
				attr(buffer, "(onChange)", "grid.filter($event, '"+field.getName()+"', '"+matchMode+"')");
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</"+tag+">");
			}
		}
		
		public static class DateFilter extends Filter
		{
			public DateFilter(FieldDefinition field)
			{
				super(field);
			}
			
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<date-filter");
				switchCase(buffer);
				attr(buffer, "(onChange)", "grid.filter($event, '"+field.getName()+"', '"+matchMode+"')");
				attr(buffer, "[grid]", "grid");
				buffer.append(">");
				buffer.append("</date-filter>");
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public static class Button extends AbstractHtmlRenderer
	{
		private final I18n label;
		private final GeneratorConstants.Icon icon;
		private final String onclick;
		private final Boolean enabled;
		
		public Button(I18n label, GeneratorConstants.Icon icon, String onclick, boolean enabled)
		{
			this.label=label;
			this.icon=icon;
			this.onclick=onclick;
			this.enabled=enabled;
		}
		
		public Button(I18n label, String onclick, boolean enabled)
		{
			this(label, null, onclick, enabled);
		}
		
		public Button(GeneratorConstants.Icon icon, String onclick, boolean enabled)
		{
			this(null, icon, onclick, enabled);
		}

		//<button type="button" pButton icon="fa-file-o" iconPos="left" label="CSV" (click)="dt.exportCSV()" style="float:left"></button>
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			if (!enabled)
				return;
			buffer.append("<p-button");
			if (icon!=null)
			{
				attr(buffer, "icon", icon.getCls());
				attr(buffer, "iconPos", "left");
			}
			if (label!=null)
				attr(buffer, "label", label.getText());
			attr(buffer, "styleClass", "width:30px;padding:5px");
			attr(buffer, "(onClick)", onclick);
			buffer.append(">");
			buffer.append("</p-button>");
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	
	public static abstract class Container extends AbstractHtmlRenderer
	{
		protected final String header;
		protected final GeneratorConstants.Icon icon;
		protected final boolean autohide;
		protected final String path;
		protected final List<HtmlRenderer> items=Lists.newArrayList();
		
		public Container(Grid grid)
		{
			this.header=grid.getParams().getTitle();
			this.icon=GeneratorConstants.Icon.find(grid.getParams().getIcon(), GeneratorConstants.Icon.GRID);
			this.autohide=grid.getParams().getAutohide();
			this.path=grid.getParams().getPath();
		}
		
		public void add(HtmlRenderer item)
		{
			items.add(item);
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			for (HtmlRenderer item : items)
			{
				buffer.append(item.render(params));
			}
		}

		////////////////////////////////////////////
		
		public static class Panel extends Container
		{		
			public Panel(Grid grid)
			{
				super(grid);
			}
			
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<p-panel");
				attr(buffer, "styleClass", "nopadding");
				if (autohide)
					attr(buffer, "*ngIf", "hasRows("+path+")");
				buffer.append(">\n");
				buffer.append(Util.renderHeader(params, header, icon));
				super.render(params, buffer);
				buffer.append("</p-panel>\n");
			}
		}
		
		////////////////////////////////////////
		
		public static class Fieldset extends Container
		{
			public Fieldset(Grid grid)
			{
				super(grid);
			}
	
			@Override
			protected void render(RenderParams params, StringBuilder buffer)
			{
				if (params.isAngular())
					renderAngular(params, buffer);
				else if (params.isFreemarker())
					renderFreemarker(params, buffer);
				else throw new CException("no handler for render mode: "+params.getMode());
			}
			
			protected void renderAngular(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<p-fieldset");
				attr(buffer, "toggleable", "true");
				if (autohide)
					attr(buffer, "*ngIf", "hasRows("+path+")");
				buffer.append(">\n");
				buffer.append(Util.renderHeader(params, header, null));
				super.render(params, buffer);
				buffer.append("</p-fieldset>\n");
			}
			
			protected void renderFreemarker(RenderParams params, StringBuilder buffer)
			{
				buffer.append("<#if "+path+"?? && "+path+"?size gt 0>\n");
				buffer.append("<fieldset>\n");
				buffer.append("<legend>"+Util.renderFreemarkerText(header)+"</legend>\n");
				super.render(params, buffer);
				buffer.append("</fieldset>\n");
				buffer.append("</#if>\n");
			}
		}
	}
}

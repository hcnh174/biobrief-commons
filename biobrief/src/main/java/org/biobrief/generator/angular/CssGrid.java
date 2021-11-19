package org.biobrief.generator.angular;

import java.util.List;

import org.biobrief.generator.Util;
import org.biobrief.generator.templates.Style;
import org.biobrief.generator.templates.TableLayout;
import org.biobrief.generator.templates.TableLayout.Cell;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

public class CssGrid extends AbstractHtmlRenderer
{	
	protected final List<CssGridCell> cells=Lists.newArrayList();
	protected final Style style=new Style();

	public CssGrid(TableLayout layout)
	{
		style.addClass("cssgrid");
		style.put("display", "grid");
		style.put("grid-gap", "0px");
		style.put("grid-template-columns", Util.getGridTemplateCols(layout.getColWidths()));
		
		for (Cell cell : layout.getVisibleCells())
		{
			cells.add(createCell(cell));
		}
	}

	protected CssGridCell createCell(Cell cell)
	{
		return new CssGridCell(cell);
	}

	public void add(CssGridCell cell)
	{
		cells.add(cell);
	}
	
	public void addClass(String cls)
	{
		this.style.addClass(cls);
	}

	@Override
	protected void render(RenderParams params, StringBuilder buffer)
	{
		buffer.append("<div");
		attrStyle(buffer, style);
		buffer.append(">\n");
		for (CssGridCell cell : cells)
		{
			buffer.append(cell.render(params));//.append("\n");
		}
		buffer.append("</div>\n");
	}
	
	public static class CssGridCell extends AbstractHtmlRenderer
	{
		protected final List<HtmlRenderer> renderers=Lists.newArrayList();
		protected final Integer row;
		protected final Integer col;
		protected final Integer rowspan;
		protected final Integer colspan;
		protected final Style style=new Style();

		public CssGridCell(int row, int col, int rowspan, int colspan)
		{
			this.row=row;
			this.col=col;
			this.rowspan=rowspan;
			this.colspan=colspan;
		}
		
		public CssGridCell(TableLayout.Cell cell)
		{
			this(cell.getRow(), cell.getCol()+1, cell.getRowspan(), cell.getColspan());//+1 TODO check
			setStyle(cell);
		}
		
		public void add(HtmlRenderer renderer)
		{
			this.renderers.add(renderer);
		}
		
		public void add(String value)
		{
			this.renderers.add(new ValueRenderer(value));
		}
				
		private void setStyle(TableLayout.Cell cell)
		{
			if (cell.getStyle().hasStyle(Style.TEXT_ALIGN))
				style.addClass("align-"+cell.getStyle().getStyle(Style.TEXT_ALIGN));
			style.put("grid-area", Util.getGridArea(row, col, rowspan, colspan));
		}
				
		protected void render(RenderParams params, StringBuilder buffer)
		{
			buffer.append("\t<div");
			attrStyle(buffer, style);
			buffer.append(">");
			
			if (renderers.size()>1)
				renderFlex(params, buffer);
			else renderPlain(params, buffer);

			buffer.append("</div>");
		}
		
		protected void renderPlain(RenderParams params, StringBuilder buffer)
		{
			for (HtmlRenderer renderer : renderers)
			{
				buffer.append(renderer.render(params));
			}
		}
		
		protected void renderFlex(RenderParams params, StringBuilder buffer)
		{
			buffer.append("<div class=\"flexrow\">");
			for (HtmlRenderer renderer : renderers)
			{
				buffer.append(flex(params, renderer));
			}
			buffer.append("</div>");
		}
		
		private String flex(RenderParams params, HtmlRenderer renderer)
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("<div");
			if (!renderer.grow())
				attr(buffer, "class", "fixeditem");//attr(buffer, "fxFlex", "nogrow");
			else attr(buffer, "class", "flexitem");//attr(buffer, "fxFlex");
			buffer.append(">");
			buffer.append(renderer.render(params));
			buffer.append("</div>");
			return buffer.toString();
		}
		
		@Override
		protected String postRender(RenderParams params, String html)
		{
			return StringHelper.indent(html)+"\n";
		}
	}
}

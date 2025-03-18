package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants;
import org.biobrief.generator.Util;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.templates.Style;
import org.biobrief.util.StringHelper;

public abstract class AbstractHtmlRenderer implements HtmlRenderer
{
	@Override
	public final String render(RenderParams params)
	{
		StringBuilder buffer=new StringBuilder();
		render(params, buffer);
		String html=buffer.toString();
		html=postRender(params, html);
		//System.out.println("rendered="+html);
		return html;
	}

	public String render()
	{
		return render(new RenderParams(RenderMode.angular));
	}
	
	protected abstract void render(RenderParams params, StringBuilder buffer);
	
	protected String postRender(RenderParams params, String html)
	{
		return html;
	}
	
	protected static void attr(StringBuilder buffer, String name)
	{
		buffer.append(" "+name);
	}
	
	protected static void attr(StringBuilder buffer, String name, GeneratorConstants.Icon icon)
	{
		if (icon!=null)
			StringHelper.attr(buffer, name, icon.getCls());
	}
	
	protected static void attr(StringBuilder buffer, String name, Object value)
	{
		if (Util.isI18n(value) && !name.startsWith("["))
			name="["+name+"]";
		StringHelper.attr(buffer, name, value);
	}
	
	protected static void attr(StringBuilder buffer, Style style)
	{
		attr(buffer, "[style]", Util.renderPrimeng(style));
	}
	
	protected static void attrStyle(StringBuilder buffer, Style style)
	{
		if (style.hasClasses())
			attr(buffer, "class", style.getClasses());
		if (style.hasStyle())
			attr(buffer, "style", style.getStyle());
	}
	
	protected static void attrIf(StringBuilder buffer, String name, Object value, boolean enabled)
	{
		if (enabled)
			attr(buffer, name, value);
	}
	
	protected static void attrIf(StringBuilder buffer, String name, boolean enabled)
	{
		if (enabled)
			attr(buffer, name);
	}
	
	protected static void switchCase(StringBuilder buffer, String field)
	{
		buffer.append(switchCase(field));
	}
	
	protected static String switchCase(String field)
	{
		return StringHelper.attr("*ngSwitchCase", StringHelper.singleQuote(field));
	}
	
	protected static String renderText(RenderParams params, String value)
	{
		return Util.renderText(params, value);
	}
	
	protected String indent(String value)
	{
		return StringHelper.indent(value);
	}
	
	protected String indent(String value, int num)
	{
		return StringHelper.indent(value, num);
	}
	
	@Override
	public boolean grow()
	{
		return true;
	}
}

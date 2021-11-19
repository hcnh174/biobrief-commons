package org.biobrief.generator.templates;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.StringHelper;

@SuppressWarnings("serial")
public class Style extends LinkedHashMap<String, String>
{
	public static final String BACKGROUND_COLOR="background-color";
	public static final String TEXT_ALIGN="text-align";
	
	protected List<String> classes=Lists.newArrayList();
	protected Map<String,String> defaults=Maps.newLinkedHashMap();
	
	public Style(){}
	
	public Style(Style style)
	{
		putAll(style);
		classes.addAll(style.classes);
	}
	
	public void setDefaultValue(String name, String value)
	{
		defaults.put(name, value);
	}

	public boolean isDefaultValue(String name, String value)
	{
		if (!containsKey(name))
			return false;
		return get(name).equals(value);
	}
	
	@Override
	public String put(String name, String value)
	{
		if (!StringHelper.hasContent(name) || !StringHelper.hasContent(value))
			return null;
		if (isDefaultValue(name, value))
			return value;
		return super.put(name, value);
	}
	
	public void addClass(String cls)
	{
		//System.out.println("adding class: "+cls);
		classes.add(cls);
	}

	public void width(Integer width)
	{
		put("width", width+"px");
	}
	
	public void width(String width)
	{
		put("width", width);
	}
	
	public void backgroundColor(String color)
	{
		put(BACKGROUND_COLOR, color);
	}

	public void bold()
	{
		put("font-weight", "bold");
	}
	
	public void italics()
	{
		put("font-style", "italic");
	}
	
	public void underline()
	{
		put("text-decoration", "underline");
	}
	
	public void color(String color)
	{
		put("color", color);
	}
	
	public void fontSize(Integer size)
	{
		put("font-size", size+"pt");
	}
	
	////////////////////////////////////////
	
	public String backgroundColor()
	{
		return get(BACKGROUND_COLOR);
	}
	
	public boolean hasBackgroundColor()
	{
		return hasStyle(BACKGROUND_COLOR);
	}
	
	public void removeBackgroundColor()
	{
		remove(BACKGROUND_COLOR);
	}
	
	///////////////////////////////////////////////
	
	public void put(String name, Integer value)
	{
		put(name, value.toString());
	}
	
	public void setStyle(CellData cell)
	{
		cell.getStyle(this);
	}
	
	public boolean hasClasses()
	{
		return !classes.isEmpty();
	}
	
	public boolean hasStyle()
	{
		return !isEmpty();
	}
	
	public boolean hasStyle(String name)
	{
		return containsKey(name);
	}
	
	public String getClasses()
	{
		return StringHelper.join(classes, " ");
	}
	
	public void align(String value)
	{
		put(TEXT_ALIGN, value);
	}
	
	public void valign(String value)
	{
		put("vertical-align", value);
	}
	
	public String getStyle()
	{
		return StringHelper.getStyle(this);
	}
	
	public String getStyle(String name)
	{
		return get(name);
	}
	
	//////////////////////////////////////
	
	public void removeClass(String name)
	{
		classes.remove(name);
	}
	
	public void removeStyle(String name)
	{
		remove(name);
	}
	
	@Override
	public String toString()
	{
		return getStyle();
	}

}

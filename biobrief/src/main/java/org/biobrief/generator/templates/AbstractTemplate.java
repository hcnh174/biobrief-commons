package org.biobrief.generator.templates;

import org.biobrief.generator.GeneratorConstants;
import org.biobrief.util.StringHelper;

public abstract class AbstractTemplate
{
	protected final String name;
	
	public AbstractTemplate(String name)
	{
		if (name.startsWith(GeneratorConstants.WORKBOOK_LOCAL_TEMPLATE_PREFIX))// remove hyphen prefix for workbook-local templates
			name=name.substring(GeneratorConstants.WORKBOOK_LOCAL_TEMPLATE_PREFIX.length());
		this.name=name;
	}

	public String getName(){return name;}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
}
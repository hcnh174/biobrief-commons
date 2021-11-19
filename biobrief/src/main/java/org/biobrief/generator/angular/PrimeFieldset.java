package org.biobrief.generator.angular;

import org.biobrief.generator.templates.Fieldset;
import org.biobrief.util.CException;

public class PrimeFieldset extends AbstractPrimeForm
{		
	public PrimeFieldset(Fieldset fieldset)
	{
		super(fieldset);
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
		attr(buffer, "toggleable", true);
		buffer.append(">\n");
		buffer.append("<p-header>");
		buffer.append(renderText(params, this.form.getTitle()));
		buffer.append("</p-header>\n");
		super.render(params, buffer);
		buffer.append("</p-fieldset>\n");
	}
	
	protected void renderFreemarker(RenderParams params, StringBuilder buffer)
	{
		buffer.append("<fieldset>\n");
		buffer.append("<legend>"+form.getTitle()+"</legend>\n");
		super.render(params, buffer);
		buffer.append("</fieldset>\n");
	}
}

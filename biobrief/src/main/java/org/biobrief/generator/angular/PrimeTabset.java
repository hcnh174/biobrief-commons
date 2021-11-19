package org.biobrief.generator.angular;

import org.biobrief.generator.templates.Fieldset;
import org.biobrief.util.CException;

public class PrimeTabset extends AbstractPrimeForm
{
	public PrimeTabset(Fieldset fieldset)
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
		String root=this.form.getRoot();//ctscan
		String path=this.form.getPath();//patient.ctscans
		buffer.append("<p-fieldset");
		attr(buffer, "toggleable", true);
		attr(buffer, "*ngFor", "let "+root+" of "+path+"; let i=index");
		buffer.append(">\n");
		buffer.append("<p-header>");
		buffer.append(renderText(params, this.form.getTitle())).append("回目{{i+1}}");
		buffer.append("</p-header>\n");
		super.render(params, buffer);
		buffer.append("</p-fieldset>\n");
	}

	protected void renderFreemarker(RenderParams params, StringBuilder buffer)
	{
		String root=this.form.getRoot();//ctscan
		String path=this.form.getPath();//patient.ctscans
		buffer.append("<#list "+path+" as "+root+">\n");
		buffer.append("<fieldset>\n");
		buffer.append("<legend>"+form.getTitle()+"</legend>\n");
		super.render(params, buffer);
		buffer.append("</fieldset>\n");
		buffer.append("</#list>\n");
	}
}

package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants;
import org.biobrief.generator.Util;
import org.biobrief.generator.templates.Form;

public class PrimeForm extends AbstractPrimeForm
{	
	private final static GeneratorConstants.Icon icon=GeneratorConstants.Icon.FORM;
	
	public PrimeForm(Form form)
	{
		super(form);
	}

	@Override
	protected void render(RenderParams params, StringBuilder buffer)
	{
		if (params.isAngular())
			renderAngular(params, buffer);
		else if (params.isFreemarker())
			renderFreemarker(params, buffer);
		else params.noHandler();
	}
	
	protected void renderAngular(RenderParams params, StringBuilder buffer)
	{
		if (form.getPanel())
		{
			buffer.append("<p-panel>\n");
			buffer.append(renderHeader(params));
			buffer.append(renderAngularForm(params));
			buffer.append(renderNgContent(params));
			buffer.append(renderFooter(params));
			buffer.append("</p-panel>\n");
		}
		else buffer.append(renderAngularForm(params));
	}
		
	protected String renderAngularForm(RenderParams params)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("<form #form=\"ngForm\">\n");
		super.render(params, buffer);
		buffer.append("</form>\n");
		return buffer.toString();
	}
	
	protected void renderFreemarker(RenderParams params, StringBuilder buffer)
	{
		buffer.append(renderHeader(params));
		buffer.append("<form>\n");
		super.render(params, buffer);
		buffer.append("</form>\n");
	}
	
	private String renderHeader(RenderParams params)
	{
		return Util.renderHeader(params, form.getTitle(), icon);
	}
	
	private String renderNgContent(RenderParams params)
	{
		return Util.renderNgContent()+"\n";
	}
	
	private String renderFooter(RenderParams params)
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append("<p-footer>\n");
		buffer.append("\t<p-button icon=\"pi pi-check\" label=\"Save\" styleClass=\"ui-button-raised ui-button-info\" (onClick)=\"saveItem()\" [style]=\"{marginRight: '.25em'}\"></p-button>\n");
		buffer.append("\t<p-button label=\"Cancel\" styleClass=\"ui-button-raised ui-button-secondary\" (onClick)=\"cancelItem()\"></p-button>\n");
		buffer.append("</p-footer>\n");
		return buffer.toString();
	}
	
	public String getFilename()
	{
		return form.getGroup()+"/view/forms/"+form.getName()+".component.html";
	}
}

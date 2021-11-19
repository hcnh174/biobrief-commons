package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants;
import org.biobrief.generator.Util;
import org.biobrief.generator.templates.Dialog;
import org.biobrief.util.StringHelper;

public class PrimeDialog extends AbstractHtmlRenderer
{
	private final String name;
	private final GeneratorConstants.Icon icon;
	private final String title;
	private final boolean modal;
	private final HtmlRenderer content;
	private final String filename;
	private final String expression;
	
	public PrimeDialog(Dialog dialog)
	{
		this.name=dialog.getName();
		this.icon=GeneratorConstants.Icon.find(dialog.getIcon(), GeneratorConstants.Icon.EDIT);
		this.modal=dialog.getModal();
		this.title=dialog.getTitle();
		this.content=new ValueRenderer(dialog.getContent());
		this.filename=dialog.getGroup()+"/dialogs/"+dialog.getName()+".component.html";
		this.expression=null;
	}
	
	public PrimeDialog(String title, String content, String expression)
	{
		this.title=title;
		this.content=new ValueRenderer(content);
		this.name=null;
		this.icon=GeneratorConstants.Icon.EDIT;
		this.modal=true;
		this.filename=null;
		this.expression=expression;
	}
	
	public String getName(){return name;}
	public String getFilename(){return filename;}

	@Override
	protected void render(RenderParams params, StringBuilder buffer)
	{
		buffer.append("<p-dialog");
		if (StringHelper.hasContent(expression))
			attr(buffer, "*ngIf", expression);
		attr(buffer, "[(visible)]", "dialogVisible");
		attr(buffer, "[modal]", modal);
		attr(buffer, "appendTo", "body");
		attr(buffer, "positionLeft", "200");
		attr(buffer, "positionTop", "100");
		attr(buffer, "(onHide)", "close()");
		buffer.append(">\n");
		buffer.append(Util.renderHeader(params, title, icon));
		buffer.append(content.render(params)).append("\n");
		buffer.append("<p-footer>\n");
		buffer.append("\t<div class=\"ui-dialog-buttonpane ui-helper-clearfix\">\n");
		buffer.append("\t<p-button icon=\"fa-close\" (onClick)=\"close()\" label=\"Close\"></p-button>\n");
		buffer.append("\t</div>\n");
		buffer.append("</p-footer>\n");	
		buffer.append("</p-dialog>\n");
	}
}

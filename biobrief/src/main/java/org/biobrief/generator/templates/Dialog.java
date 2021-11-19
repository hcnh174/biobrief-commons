package org.biobrief.generator.templates;

import java.util.Map;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

public class Dialog extends AbstractTemplate
{
	protected final EntityDefinition entityType;
	protected final DialogParams params;
	protected final String content;
	
	public Dialog(ExcelTemplate template, Dictionary dictionary, DialogParams defaultParams)
	{
		super(template.getName());
		this.params=parseDialogParams(template, defaultParams);
		this.entityType=dictionary.getGroup(params.getGroup()).getEntity(params.getEntity());
		this.content=getContent(template);
	}
	
	private static DialogParams parseDialogParams(ExcelTemplate template, DialogParams params)
	{
		params.setTitle(TemplateUtils.getTitle(template));
		return params;
	}
	
	private String getContent(ExcelTemplate template)
	{
		CellData contentcell=template.getCell(1, 0);
		String content=contentcell.getStringValue();
		if (!StringHelper.hasContent(content))
			throw new CException("no content found for dialog: "+name);
		return content;
	}
	
	public String getGroup()
	{
		return TemplateUtils.getGroup(getEntity());
	}
	
	public EntityDefinition getEntity(){return entityType;}
	public String getIcon(){return params.getIcon();}
	public String getTitle(){return params.getTitle();}
	public boolean getModal(){return params.getModal();}
	public String getContent(){return content;}
	
	public static class DialogParams extends AbstractParams
	{
		private String group;
		private String entity;
		private String title;
		private String icon="fa-edit";
		private boolean modal=true;
		
		public DialogParams(Map<String, Object> values)
		{
			setParams(values);
		}
			
		public String getGroup(){return this.group;}
		public void setGroup(final String group){this.group=group;}
		
		public String getEntity(){return this.entity;}
		public void setEntity(final String entity){this.entity=entity;}

		public String getTitle(){return this.title;}
		public void setTitle(final String title){this.title=title;}

		public String getIcon(){return this.icon;}
		public void setIcon(final String icon){this.icon=icon;}

		public boolean getModal(){return this.modal;}
		public void setModal(final boolean modal){this.modal=modal;}
	}
}

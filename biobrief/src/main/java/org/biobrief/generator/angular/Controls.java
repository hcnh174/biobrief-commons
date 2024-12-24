package org.biobrief.generator.angular;

import org.biobrief.dictionary.FieldType;
import org.biobrief.generator.GeneratorConstants.ControlType;
import org.biobrief.generator.GeneratorConstants.KeyFilter;
import org.biobrief.generator.Util;
import org.biobrief.generator.templates.Form;
import org.biobrief.util.CException;
import org.biobrief.util.UnhandledCaseException;

//http://j2html.com/examples.html
public class Controls
{	
	public static Control createTextControl(Form.Control cell)
	{
		if (cell.getParams().isEditable() && cell.getFieldType()==FieldType.INTEGER)
			return new KeyFilterControl(cell, KeyFilter.Integer);
		if (cell.getParams().isEditable() && cell.getFieldType()==FieldType.FLOAT)
			return new KeyFilterControl(cell, KeyFilter.Number);
		return new TextControl(cell);
	}
	
	public static Control createTextAreaControl(Form.Control cell)
	{
		return new TextAreaControl(cell);
	}
	
	public static Control createCheckboxControl(Form.Control cell)
	{
		if (!cell.getParams().isEditable())
			return new ReadonlyCheckboxControl(cell);
		else return new PrimeCheckboxControl(cell);
	}
	
	public static Control createSelectControl(Form.Control cell)
	{
		System.out.println("createSelectControl: controlType "+cell.getParams().getControl());
		if (!cell.getParams().isEditable())
			return new ReadonlySelectControl(cell);
		else if (cell.getParams().getControl()==ControlType.checkbox)
			return new PrimeCheckboxSelectControl(cell);
		else return new PrimeSelectControl(cell);
	}
	
	public static Control createMasterControl(Form.Control cell)
	{
		if (!cell.getParams().isEditable())
			return new ReadonlySelectControl(cell);
		return new PrimeSelectControl(cell);
	}
	
	public static Control createDateControl(Form.Control cell)
	{
		if (!cell.getParams().isEditable())
			return new DisplayControl(cell);
		else return new PrimeDateControl(cell);
	}
	
	public static Control createFormulaControl(Form.Control cell)
	{
		return new FormulaControl(cell);
	}
	
	public static Control createUnknownControl(Form.Control cell)
	{
		return new TextControl(cell);
	}

	/////////////////////////////////////////////////
	
	public abstract static class Control extends AbstractHtmlRenderer
	{
		protected final Form.Control control;
		protected final String path;
		protected final String name;
		protected String width;
		protected final Boolean readonly;
		
		public Control(Form.Control control)
		{
			this.control=control;
			this.path=control.getPath();
			this.name=control.getName();
			this.width=control.getParams().getWidth();//control.getWidth();
			this.readonly=control.isReadonly();
		}
		
		@Override
		protected void render(RenderParams params, StringBuilder buffer)
		{
			if (params.isAngular())
				renderAngular(buffer);
			else if (params.isFreemarker())
				renderFreemarker(buffer);
			else throw new CException("no handler for render mode: "+params.getMode());
		}

		protected abstract void renderAngular(StringBuilder buffer);
		
		protected void renderFreemarker(StringBuilder buffer)
		{
			buffer.append("<span");
			attr(buffer, "class", "value");
			//attr(buffer, "style", "width:"+width+"");
			buffer.append(">");
			buffer.append(renderFreemarkerControl());
			buffer.append("</span>");
		}
		
		protected String renderFreemarkerControl()
		{
			return Util.renderFreemarkerField(path, control.getField());
		}
	}
	
	///////////////////////////////////
	
	public static class TextControl extends Control
	{
		public TextControl(Form.Control control)
		{
			super(control);
		}
		
		//<input type="text" required [(ngModel)]="patient.name" ngControl="name" style="width:150px">
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<input");
			attr(buffer, "type", "text");
			attr(buffer, "pInputText");
			//attr(buffer, "style", "width:"+width);
			attr(buffer, "[fluid]", "true");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	
//	public static class NonEditableControl extends Control
//	{
//		public NonEditableControl(Form.Control control)
//		{
//			super(control);
//		}
//		
//		//<input type="text" required [(ngModel)]="patient.name" ngControl="name" style="width:150px">
//		@Override
//		protected void renderAngular(StringBuilder buffer)
//		{
//			buffer.append("<input");
//			attr(buffer, "type", "text");
//			attr(buffer, "pInputText");
//			attr(buffer, "class", "noneditablecontrol");
//			attr(buffer, "style", "width:"+width);
//			attr(buffer, "name", name);
//			attr(buffer, "[(ngModel)]", path);
//			attr(buffer, "readonly");
//			buffer.append(">");
//		}
//	}
//	
	////////////////////////////////////////////////////////////////////////
	
	public static class MaskControl extends TextControl
	{
		final private String alias;
		
		public MaskControl(Form.Control control)
		{
			super(control);
			this.alias=getAlias(control);
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<p-inputMask");
			attr(buffer, "alias", alias);
			attr(buffer, "[style]", "{'width':'"+width+"'}");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</p-inputMask>");
		}
		
		private static String getAlias(Form.Control control)
		{
			switch (control.getFieldType())
			{
			case INTEGER:
				return "integer";
			case FLOAT:
				return "float";
			default:
				throw new UnhandledCaseException(control.getFieldType());
			}	
		}
	}
	
	public static class SpinnerControl extends TextControl
	{
		public SpinnerControl(Form.Control control)
		{
			super(control);
		}
		
		//<p-spinner [(ngModel)]="item[col.field]" min="0"></p-spinner>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<p-spinner");
			//attr(buffer, "min", 0);
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "[formatInput]", false);
			attr(buffer, "thousandSeparator", "");
			//attr(buffer, "[style]", "{'width':'"+width+"'}");
			//if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</p-spinner>");
		}
	}
	

	public static class KeyFilterControl extends TextControl
	{
		private KeyFilter filter;
		
		public KeyFilterControl(Form.Control control, KeyFilter filter)
		{
			super(control);
			this.filter=filter;
		}
		
		// <input type="text" pKeyFilter="int">
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<input");
			attr(buffer, "type", "text");
			attr(buffer, "pInputText");
			attr(buffer, "pKeyFilter", filter.getFilter());
			attr(buffer, "style", "width:"+width);
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
		}
	}
	
	////////////////////////////////////////////////////////////
	
	public static class DisplayControl extends TextControl
	{
		private final String value;
		
		public DisplayControl(Form.Control control, String value)
		{
			super(control);
			this.value=value;
		}
		
		public DisplayControl(Form.Control control)
		{
			this(control, control.getPath()+" | null");
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<input");
			attr(buffer, "type", "text");
			attr(buffer, "pInputText");
			attr(buffer, "style", "width:"+width+"");
			attr(buffer, "[value]", value);
			attr(buffer, "readonly");
			buffer.append(">");
		}
	}

	public static class FormulaControl extends DisplayControl
	{
		public FormulaControl(Form.Control control)
		{
			super(control, control.getName()+"("+control.getParams().getFormParams().getRoot()+")");
		}
	}
	
	/////////////////////////////////////
	
	public static class TextAreaControl extends TextControl
	{		
		public TextAreaControl(Form.Control control)
		{
			super(control);
		}
		
		//<textarea pInputTextarea cols="100" rows="4"  autoResize="autoResize"="autoResize"></textarea>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<textarea");
			buffer.append(" pTextarea");
			//attr(buffer, "variant", "filled");
			attr(buffer, "autoResize", "autoResize");
			//attr(buffer, "style", "width:"+width);
			attr(buffer, "[fluid]", "true");
			if (control.getParams().getRows()>1)
				attr(buffer, "rows", control.getParams().getRows());
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</textarea>");
		}
	}
	
	//////////////////////////////////////////////////////////////
	
	public static class CheckboxControl extends Control
	{
		protected final String label;
		
		public CheckboxControl(Form.Control control)
		{
			super(control);
			this.label=this.getLabel(control);
		}
		
		protected String getLabel(Form.Control control)
		{
			return control.getField().getLabel();
		}
				
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<input");
			attr(buffer, "type", "checkbox");
			attr(buffer, "name", name);
			attr(buffer, "value", "true");
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("&nbsp;");
			buffer.append("<span>");
			buffer.append(label);
			buffer.append("</span>");
		}
		
		@Override		
		protected void renderFreemarker(StringBuilder buffer)
		{
			buffer.append("<span");
			attr(buffer, "class", "value");
			attr(buffer, "style", "width:20px");
			buffer.append(">");
			buffer.append(renderFreemarkerControl());
			buffer.append("</span>");
			buffer.append("<span>");
			buffer.append(label);
			buffer.append("</span>");
		}
	}
	
	/////////////////////////////////////////////////////////////////
	
	public static class PrimeCheckboxControl extends CheckboxControl
	{
		public PrimeCheckboxControl(Form.Control control)
		{
			super(control);
		}

		//<p-checkbox name="groupname" value="val2" [(ngModel)]="selectedValues"></p-checkbox>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<p-checkbox");
			attr(buffer, "label", label);
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "[binary]", "true");
			//attr(buffer, "variant", "filled");
			buffer.append(">");
			buffer.append("</p-checkbox>");
		}
	}	
	
	
	/////////////////////////
	
//	public static class PrimeDynamicCheckboxControl extends Control
//	{
//		public PrimeDynamicCheckboxControl(Form.Control control)
//		{
//			super(control);
//		}
//		
//		@Override
//		protected void renderAngular(StringBuilder buffer)
//		{
//			<div *ngFor="let category of categories" class="field-checkbox">
//		    <p-checkbox 
//		        [(ngModel)]="selectedCategories"
//		        [label]="category.name" 
//		        name="group" 
//		        [value]="category" />
//		</div>
//			
////			buffer.append("<i class=\"fa\" [class.fa-check-square-o]=\""+this.path+"\" [class.fa-square-o]=\"!"+this.path+"\"></i>");
////			buffer.append("&nbsp;");
////			buffer.append("<span>");
////			buffer.append(label);
////			buffer.append("</span>");
//		}
//	}
	
	////////////////////////
	
	public static class ReadonlyCheckboxControl extends CheckboxControl
	{
		public ReadonlyCheckboxControl(Form.Control control)
		{
			super(control);
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<i class=\"fa\" [class.fa-check-square-o]=\""+this.path+"\" [class.fa-square-o]=\"!"+this.path+"\"></i>");
			buffer.append("&nbsp;");
			buffer.append("<span>");
			buffer.append(label);
			buffer.append("</span>");
		}
	}

	////////////////////////////////////////////////////////////
	
	public static class SelectControl extends Control
	{
		protected final FieldType fieldType;
		protected final String type;
		
		public SelectControl(Form.Control control)
		{
			super(control);
			fieldType=control.getField().getFieldType();
			type=control.getField().getType();
		}
		
		
		//<option *ngFor="let item of util.enums('Sex')" [value]="item.id">{{item.name}}</option>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<select");
			attr(buffer, "style", "width:"+width+"");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">\n");
			
			buffer.append("<option");
			if (fieldType==FieldType.MASTER)
				attr(buffer, "*ngFor", "let item of enums.masters('"+type+"')");
			else attr(buffer, "*ngFor", "let item of "+getOptions());//enums.enums('"+type+"')
			attr(buffer, "[value]", "item.value");
			buffer.append(">");
			buffer.append("{{item.label}}");
			buffer.append("</option>\n");
			buffer.append("</select>");
		}
		
		protected String getOptions()
		{
			if (fieldType==FieldType.ENUM)
				return "enums."+type;//return "enums.enums('"+type+"')";
			//else if (fieldType==FieldType.MASTER)
			//	return "enums.masters('"+type+"')";
			else throw new CException("unhandled dropdown field type: "+fieldType);
		}
	}
	
	//////////////////////////////////////////////////////////////////
	
	public static class PrimeSelectControl extends SelectControl
	{
		public PrimeSelectControl(Form.Control control)
		{
			super(control);
		}
		
		//<p-dropdown [options]="enums.enums('Sex')" 
		//[(ngModel)]="patient.sex" name="sex" [style]="{'width':'64px'}">
		//</p-dropdown>
		//<p-dropdown [options]="enums.CtdbRecruitmentStatus"
		//	[(ngModel)]="model.ctdb.recruitmentStatus" name="recruitmentStatus"
		//	optionLabel="label" optionValue="value"
		//	[style]="{width: '205px'}"></p-dropdown>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{			
			buffer.append("<p-select");
			attr(buffer, "[options]", getOptions());
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "[filter]", "true");
			attr(buffer, "[style]", "{'width':'"+width+"'}");
			//attr(buffer, "appendTo", "body");
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</p-select>");
		}
	}
	
//	public static class PrimeSelectControl extends SelectControl
//	{
//		public PrimeSelectControl(Form.Control control)
//		{
//			super(control);
//		}
//		
//		//<p-dropdown [options]="enums.enums('Sex')" 
//		//[(ngModel)]="patient.sex" name="sex" [style]="{'width':'64px'}">
//		//</p-dropdown>
//		//<p-dropdown [options]="enums.CtdbRecruitmentStatus"
//		//	[(ngModel)]="model.ctdb.recruitmentStatus" name="recruitmentStatus"
//		//	optionLabel="label" optionValue="value"
//		//	[style]="{width: '205px'}"></p-dropdown>
//		@Override
//		protected void renderAngular(StringBuilder buffer)
//		{			
//			buffer.append("<p-dropdown");
//			attr(buffer, "[options]", getOptions());
//			attr(buffer, "name", name);
//			attr(buffer, "[(ngModel)]", path);
//			attr(buffer, "[filter]", "true");
//			attr(buffer, "[style]", "{'width':'"+width+"'}");
//			attr(buffer, "appendTo", "body");
//			if (readonly) attr(buffer, "readonly");
//			buffer.append(">");
//			buffer.append("</p-dropdown>");
//		}
//	}
	
	// Note: does not seem to work using {label: '', value: ''} objects, so only use with string array class
	public static class PrimeCheckboxSelectControl extends SelectControl
	{
		public PrimeCheckboxSelectControl(Form.Control control)
		{
			super(control);
		}
		
//        <div *ngFor="let item of enums.CtdbCancerType" class="flex items-center">
//	        <p-checkbox name="cancerType" [(ngModel)]="model.cancerType" [inputId]="item" [value]="item"/>
//	        <label [for]="item" class="ml-2"> {{item}} </label>
//	    </div>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<div");
			attr(buffer, "*ngFor", "let item of "+getOptions());
			attr(buffer, "class", "flex items-center");
			buffer.append(">");

				buffer.append("<p-checkbox");
				attr(buffer, "name", name);
				attr(buffer, "[(ngModel)]", path);
				attr(buffer, "[inputId]", "item");
				attr(buffer, "[value]", "item");
				buffer.append("/>");
				
				buffer.append("<label");
				attr(buffer, "[for]", "item");
				attr(buffer, "class", "ml-2");
				buffer.append(">");
				buffer.append(" {{item}} ");
				buffer.append("</label>");
			
			buffer.append("</div>");
		}
	}
	
//	// Note: does not seem to work using {label: '', value: ''} objects, so only use with string array class
//	public static class PrimeCheckboxSelectControl extends SelectControl
//	{
//		public PrimeCheckboxSelectControl(Form.Control control)
//		{
//			super(control);
//		}
//		
////		<div *ngFor="let category of categories" class="field-checkbox">
////		    <p-checkbox 
////		        [(ngModel)]="selectedCategories"
////		        [label]="category.name" 
////		        name="group" 
////		        [value]="category" />
////		</div>
//		@Override
//		protected void renderAngular(StringBuilder buffer)
//		{
//			buffer.append("<div");
//			attr(buffer, "*ngFor", "let item of "+getOptions());
//			attr(buffer, "class", "field-checkbox");
//			buffer.append(">");
//
//				buffer.append("<p-checkbox");
//				attr(buffer, "name", name);
//				attr(buffer, "[(ngModel)]", path);
//				attr(buffer, "[label]", "item");
//				attr(buffer, "[value]", "item");
//				buffer.append(">");
//				buffer.append("</p-checkbox>");
//			
//			buffer.append("</div>");
//		}
//	}
	
	//////////////////////////////////////////////////////
	
	public static class ReadonlySelectControl extends SelectControl
	{
		public ReadonlySelectControl(Form.Control control)
		{
			super(control);
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{			
			buffer.append("<input");
			attr(buffer, "type", "text");
			attr(buffer, "pInputText");
			attr(buffer, "style", "width:"+width+"");
			attr(buffer, "[value]", getValue());
			attr(buffer, "readonly");
			buffer.append(">");
		}
		
		private String getValue()
		{
			if (fieldType==FieldType.MASTER)
				return "enums.getMaster('"+type+"', "+path+")";
			else return "enums.getEnum('"+type+"', "+path+")";
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	
	public static class DateControl extends Control
	{
		public DateControl(Form.Control control)
		{
			super(control);
		}

		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<input");
			attr(buffer, "type", "text");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "style", "width:"+width+"");
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
		}
	}
	
	///////////////////////////////////////////////////////////
	
	public static class PrimeDateControl extends DateControl
	{
		public PrimeDateControl(Form.Control control)
		{
			super(control);
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<p-datepicker");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "[showIcon]", "true");
			attr(buffer, "[monthNavigator]", "true");
			attr(buffer, "[yearNavigator]", "true");
			attr(buffer, "[selectOtherMonths]", "true");
			//attr(buffer, "[locale]", "i18n.locale");
			attr(buffer, "yearRange", "1900:2100");
			attr(buffer, "dateFormat", "yy/mm/dd");
			//attr(buffer, "dataType", "string");
			//attr(buffer, "appendTo", "body");
			attr(buffer, "[style]", "{'width':'100%'}");//, 'padding-right':'30px'
			//if (readonly)
			attr(buffer, "[readonlyInput]", "true");
			buffer.append(">");
			buffer.append("</p-datepicker>");
		}
	}
	
//	public static class PrimeDateControl extends DateControl
//	{
//		public PrimeDateControl(Form.Control control)
//		{
//			super(control);
//		}
//		
//		@Override
//		protected void renderAngular(StringBuilder buffer)
//		{
//			buffer.append("<p-calendar");
//			attr(buffer, "name", name);
//			attr(buffer, "[(ngModel)]", path);
//			attr(buffer, "[showIcon]", "true");
//			attr(buffer, "[monthNavigator]", "true");
//			attr(buffer, "[yearNavigator]", "true");
//			attr(buffer, "[selectOtherMonths]", "true");
//			//attr(buffer, "[locale]", "i18n.locale");
//			attr(buffer, "yearRange", "1900:2100");
//			attr(buffer, "dateFormat", "yy/mm/dd");
//			//attr(buffer, "dataType", "string");
//			attr(buffer, "appendTo", "body");
//			attr(buffer, "[style]", "{'width':'100%', 'padding-right':'30px'}");
//			//if (readonly)
//			attr(buffer, "[readonlyInput]", "true");
//			buffer.append(">");
//			buffer.append("</p-calendar>");
//		}
//	}
	
	//<calendar-control name="birthdate" [(ngModel)]="patient.birthdate" width="95"></calendar-control>
	public static class CustomDateControl extends DateControl
	{
		public CustomDateControl(Form.Control control)
		{
			super(control);
		}
		
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<calendar-control");
			attr(buffer, "name", name);
			attr(buffer, "[(ngModel)]", path);
			attr(buffer, "width", width);//yy/mm/dd
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</calendar-control>");
		}
	}
	
	public static class VaadinDateControl extends DateControl
	{
		public VaadinDateControl(Form.Control control)
		{
			super(control);
		}
		
		//<vaadin-date-picker [(value)]="patient.birthdate"></vaadin-date-picker>
		@Override
		protected void renderAngular(StringBuilder buffer)
		{
			buffer.append("<vaadin-date-picker");
			attr(buffer, "[(value)]", path);
			if (readonly) attr(buffer, "readonly");
			buffer.append(">");
			buffer.append("</vaadin-date-picker>");
		}
	}
}

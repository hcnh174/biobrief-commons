package org.biobrief.generator.angular;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.generator.Util;
import org.biobrief.generator.angular.AngularGeneratorParams.HandsontableGeneratorParams;
import org.biobrief.generator.templates.ExcelTemplate;
import org.biobrief.generator.templates.Handsontable;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.JsBeautifier;
import org.biobrief.util.MessageWriter;

import com.google.common.collect.Maps;

public class HandsontableGenerator extends AbstractLayoutGenerator
{
	private final Map<String, Handsontable> handsontables=Maps.newLinkedHashMap();
	
	public static void generate(HandsontableGeneratorParams params, MessageWriter writer)
	{
		HandsontableGenerator generator=new HandsontableGenerator(params, writer);
		generator.generate(params.getTemplate());
	}
	
	protected HandsontableGenerator(HandsontableGeneratorParams params, MessageWriter writer)
	{
		super(params, writer);
	}
	
	protected void generate(Workbook workbook)
	{
		load(workbook);
		write();
	}
	
	protected void load(Workbook workbook)
	{
		for (ExcelTemplate template : getTemplates(workbook))
		{
			addTemplate(generateHandsontable(template));
		}
	}
	
	//////////////////////////////////////////////
	
	protected Handsontable generateHandsontable(ExcelTemplate template)
	{
		return new Handsontable(template);
	}
	
	////////////////////////////////////////////////////
	
	protected void addTemplate(Handsontable handsontable)
	{
		if (this.handsontables.containsKey(handsontable.getName()))
			throw new CException("duplicate handsontable name "+handsontable.getName());
		this.handsontables.put(handsontable.getName(), handsontable);
	}
	
	////////////////////////////////////////////////////////
	
	protected void write()
	{
		for (Handsontable handsontable : handsontables.values())
		{
			write(handsontable);
		}
	}

	protected void write(Handsontable handsontable)
	{
		updateJavascriptFile(handsontable);
	}
	
	private void updateJavascriptFile(Handsontable handsontable)
	{
		//String filename=Util.JAVASCRIPT_DIRECTORY+"/txsheets/"+handsontable.getName()+".js";
		String filename=handsontable.getName()+".js";
		if (!FileHelper.exists(filename))
			throw new CException("cannot find txsheet file: "+filename);
		writer.println("handsontable javascript file: "+filename);
		String hotconfig=JsBeautifier.jsBeautify(handsontable.toJavascript());
		String js="var hotconfig="+hotconfig+";\n";
		String str=FileHelper.readFile(filename);
		str=Util.insertText(Util.INIT, str, js, true);
//		if (overwrite)
//			overwriteFile(filename, str);
//		else writeFile(Util.GENERATED_JAVASCRIPT_DIRECTORY+"/txsheets/"+handsontable.getName()+".js", str);
	}
}

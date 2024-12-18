package org.biobrief.generator.angular;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.generator.angular.AngularGeneratorParams.AbstractTemplateGeneratorParams;
import org.biobrief.generator.templates.ExcelTemplate;
import org.biobrief.generator.templates.TemplateUtils;
import org.biobrief.util.CException;
import org.biobrief.util.ExcelHelper;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;

public abstract class AbstractLayoutGenerator
{
	protected final ExcelHelper excel=new ExcelHelper();
	protected final AbstractTemplateGeneratorParams params;
	protected final MessageWriter writer;	
	
	protected AbstractLayoutGenerator(AbstractTemplateGeneratorParams params, MessageWriter writer)
	{
		this.params=params;
		this.writer=writer;
	}

	protected void generate(String filename)
	{
		String copy=ExcelHelper.createCopy(filename, params.getOutDir(), writer);
		Workbook workbook=excel.openWorkbook(copy);
		generate(workbook);
		excel.closeWorkbook(workbook);
		FileHelper.deleteFile(copy);
	}
	
	protected Map<String, Object> loadDefaultParams(Workbook workbook)
	{
		return TemplateUtils.loadDefaultParams(workbook);
	}
		
	protected abstract void generate(Workbook workbook);
	
	protected List<ExcelTemplate> getTemplates(Workbook workbook)
	{
		return TemplateUtils.getTemplates(workbook);
	}
	
	protected void overwriteFile(String filename, String html)
	{
		if (!params.getOverwrite())
			return;
		if (!FileHelper.exists(filename))
			throw new CException("template file does not exist: "+filename);
		writeFile(filename, html);
	}
	
	protected void writeFile(String filename, String html)
	{
		writer.println("writing file:"+filename);
		FileHelper.writeFile(filename, html, true);
	}
}

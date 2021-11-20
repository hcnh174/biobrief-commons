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
//	protected final String dir;
//	protected final String tmpDir;AbstractTemplateGeneratorParams
	//protected final Dictionary dictionary;
//	protected final RenderMode mode;
//	protected final boolean overwrite;
	protected final MessageWriter writer;	
	
	protected AbstractLayoutGenerator(AbstractTemplateGeneratorParams params, MessageWriter writer)
	{
		this.params=params;
//		this.dir=params.getDir();
//		this.tmpDir=params.getTmpDir();
//		this.dictionary=params.getDictionary();
//		this.mode=params.getMode();
//		this.overwrite=params.getOverwrite();
		this.writer=writer;
	}
	
//	protected void generate()
//	{
//		for (String filename : ExcelHelper.listFilesRecursively(params.getTemplateDir()))
//		{
//			generate(filename);
//		}
//	}
	
	protected void generate(String filename)
	{
		String copy=params.getOutDir()+"/"+FileHelper.stripPath(filename);
		System.out.println("create temporary copy of Excel file: filename="+filename+" copy="+copy);
		if (FileHelper.exists(copy))
			FileHelper.deleteFile(copy);
		FileHelper.copyFile(filename, copy);
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
		FileHelper.writeFile(filename, html);
	}
}

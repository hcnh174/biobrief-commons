package org.biobrief.generator.angular;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.generator.Util;
import org.biobrief.generator.angular.AngularGeneratorParams.GridGeneratorParams;
import org.biobrief.generator.templates.ExcelTemplate;
import org.biobrief.generator.templates.Grid;
import org.biobrief.generator.templates.Grid.GridParams;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;

public abstract class AbstractGridGenerator extends AbstractLayoutGenerator
{
	protected AbstractGridGenerator(GridGeneratorParams params, MessageWriter writer)//boolean typescript,
	{
		super(params, writer);
	}

	protected void generate(Workbook workbook)
	{
		Map<String, Object> params=loadDefaultParams(workbook);
		//System.out.println("grid settings: "+params);"
		for (ExcelTemplate template : getTemplates(workbook))
		{
			if (template.getName().endsWith("-grid"))
				generate(template, params);
			else throw new CException("unrecognized sheet name: "+template.getName());
		}
	}
	
	protected void generate(ExcelTemplate template, Map<String, Object> params)
	{
		writer.println("generating grid: "+template.getName());
		Grid grid=new Grid(template, new GridParams(params), this.params.getDictionary());
		AbstractAngularGrid nggrid=createAngularGrid(grid);
		updateHtmlFile(nggrid);
		updateTypescriptFile(nggrid);
	}
	
	protected abstract AbstractAngularGrid createAngularGrid(Grid grid);
	
	private void updateHtmlFile(AbstractAngularGrid nggrid)
	{
		String srcFile=nggrid.getHtmlFilename(params.getSrcDir());
		if (!FileHelper.exists(srcFile))
			throw new CException("cannot find grid src html file: "+srcFile);
		writer.println("grid src html file: "+srcFile);
		String html=nggrid.render(new RenderParams(params.mode))+"\n";
		//System.out.println("html="+html);
		String str=FileHelper.readFile(srcFile);
		str=Util.insertHtml(str, html, true);
//		if (overwrite)
//			overwriteFile(filename, str);
		String outfile=nggrid.getHtmlFilename(params.getOutDir());
		FileHelper.writeFile(outfile, html);
	}
	
//	private void updateHtmlFile(AbstractAngularGrid nggrid)
//	{
//		//String filename=Util.ANGULAR_APP_DIRECTORY+"/"+nggrid.getHtmlFilename();
////		String filename=nggrid.getHtmlFilename();
////		if (!FileHelper.exists(filename))
////			throw new CException("cannot find grid template file: "+filename);
////		writer.println("grid template file: "+filename);
////		String html=nggrid.render(new RenderParams(params.mode))+"\n";
////		//System.out.println("html="+html);
////		String str=FileHelper.readFile(filename);
////		str=Util.insertHtml(str, html, true);
////		if (overwrite)
////			overwriteFile(filename, str);
////		else writeFile(Util.GENERATED_ANGULAR_DIRECTORY+"/"+nggrid.getHtmlFilename(), str);
//	}
	
	private void updateTypescriptFile(AbstractAngularGrid nggrid)
	{
		String srcFile=nggrid.getTypescriptFilename(params.getSrcDir());
		if (!FileHelper.exists(srcFile))
			throw new CException("cannot find grid typescript file: "+srcFile);
		writer.println("grid src typescript file: "+srcFile);
		String ts=nggrid.toTypescript();
		String str=FileHelper.readFile(srcFile);
		str=Util.insertText(Util.INIT, str, ts, true);
//		if (overwrite)
//			overwriteFile(filename, str);
		String outfile=nggrid.getTypescriptFilename(params.getOutDir());
		FileHelper.writeFile(outfile, ts);
	}
	
//	private void updateTypescriptFile(AbstractAngularGrid nggrid)
//	{
////		String filename=Util.ANGULAR_APP_DIRECTORY+"/"+nggrid.getTypescriptFilename();
////		String filename=nggrid.getTypescriptFilename();
////		if (!FileHelper.exists(filename))
////			throw new CException("cannot find grid typescript file: "+filename);
////		writer.println("grid typescript file: "+filename);
////		String ts=nggrid.toTypescript();//+"\n";
////		String str=FileHelper.readFile(filename);
////		str=Util.insertText(Util.INIT, str, ts, true);
////		if (overwrite)
////			overwriteFile(filename, str);
////		else writeFile(Util.GENERATED_ANGULAR_DIRECTORY+"/"+nggrid.getTypescriptFilename(), str);
//	}
}

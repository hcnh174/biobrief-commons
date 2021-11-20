package org.biobrief.generator.angular;

//import org.biobrief.core.CoreConstants.PrintView;
import org.biobrief.generator.AbstractGeneratorService;
import org.biobrief.generator.angular.AngularGeneratorParams.FormGeneratorParams;
import org.biobrief.generator.angular.AngularGeneratorParams.GridGeneratorParams;
import org.biobrief.generator.angular.AngularGeneratorParams.HandsontableGeneratorParams;
import org.biobrief.generator.angular.AngularGeneratorParams.ModelGeneratorParams;
import org.biobrief.util.MessageWriter;
import org.springframework.stereotype.Service;

@Service
public class AngularGeneratorService extends AbstractGeneratorService
{
//	private final String angularDir;
//	private final String formDir;
//	private final String gridDir;
//	private final String handsontableDir;
	
//	public AngularGeneratorService(GeneratorProperties properties)
//	{
//		this.angularDir=properties.getAngularDir();
//		this.formDir=angularDir+"/forms";
//		this.gridDir=angularDir+"/grids";
//		this.handsontableDir=angularDir+"/handsontable";
//	}
	
	public void generateModels(ModelGeneratorParams params, MessageWriter writer)
	{
		ModelGenerator.generate(params, writer);
	}
	
	////////////////////////////////////////////////////
	
	public void generateForms(FormGeneratorParams params, MessageWriter writer)
	{
//		FormGeneratorParams params=new FormGeneratorParams(getDictionary(), formDir, tmpDir);
//		params.setDir(formDir);
//		params.setDictionary();
//		params.setMode(mode);
//		params.setOverwrite(overwrite);
		//FormGenerator.generate(formDir, getDictionary(), mode, overwrite, writer);
		FormGenerator.generate(params, writer);
	}
	
//	public void generateForm(String name, FormGeneratorParams params, MessageWriter writer)
//	{
////		GeneratorParams params=new GeneratorParams();
////		params.setDir(formDir);
////		params.setDictionary(getDictionary());
////		params.setMode(RenderMode.ANGULAR);
////		params.setOverwrite(overwrite);
//		FormGenerator.generate(name, params, writer);
//		//FormGenerator.generate(name, formDir, getDictionary(), RenderMode.ANGULAR, overwrite, writer);
//	}
	
//	public void generatePrintForms(boolean overwrite, MessageWriter writer)
//	{
//		for (PrintView view : PrintView.values())
//		{
//			FormGenerator.generate(view.getFile(), formDir, getDictionary(), RenderMode.FREEMARKER, overwrite, writer);
//		}
//	}
	
	////////////////////////////////////////////////////
	
	public void generateGrids(GridGeneratorParams params, MessageWriter writer)
	{
		GridGenerator.generate(params, writer);
	}
	
//	public void generateGrid(String name, GridGeneratorParams params, MessageWriter writer)
//	{
//		GridGenerator.generate(name, params, writer);
//	}
	
	////////////////////////////////////////////////
	
	public void generateSheets(HandsontableGeneratorParams params, MessageWriter writer)
	{
		HandsontableGenerator.generate(params, writer);
	}
	
//	public void generateSheet(String name, HandsontableGeneratorParams params, MessageWriter writer)
//	{
//		HandsontableGenerator.generate(name, params, writer);
//	}
	
	////////////////////////////////////////////////////
	
//	@SuppressWarnings("rawtypes")
//	public void generateEnums(List<Class> enumclasses, boolean overwrite, MessageWriter writer)
//	{
//		EnumGenerator.generate(getDictionary(), enumclasses, overwrite, writer);
//	}
	
	public void generateI18n(boolean overwrite, MessageWriter writer)
	{
		AngularI18nGenerator.generate(overwrite, writer);
	}
}

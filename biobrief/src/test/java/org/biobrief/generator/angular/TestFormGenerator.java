package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	@Test
	public void createAngularCtdbEditTrialForm()
	{
		String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms2.xlsx";
		String dictDir="c:/workspace/hucgc/data/dictionary";
		String srcDir="C:/workspace/hucgc/.temp/generator";
		String outDir=srcDir;
		RenderMode mode=RenderMode.ANGULAR;
		
		String[] argv={xlsxfile, dictDir, srcDir, outDir, mode.name()};
		
		FormGenerator.main(argv);
	}
	
	//@Test
	public void createFreemarkerCtdbEditTrialForm()
	{
		String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms2.xlsx";
		String dictDir="c:/workspace/hucgc/data/dictionary";
		String srcDir="C:/workspace/hucgc/.temp/generator";
		String outDir=srcDir;
		RenderMode mode=RenderMode.FREEMARKER;
		
		String[] argv={xlsxfile, dictDir, srcDir, outDir, mode.name()};
		
		FormGenerator.main(argv);
	}
	
//	@Test
//	public void createReportCtdbEditTrialForm()
//	{
//		String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms.xlsx";
//		String dictDir="c:/workspace/hucgc/data/dictionary";
//		String srcDir="C:/workspace/hucgc/.temp/generator";
//		String outDir=srcDir;
//		String[] argv={xlsxfile, dictDir, srcDir, outDir};
//		
//		FormGenerator.main(argv);
//	}
}
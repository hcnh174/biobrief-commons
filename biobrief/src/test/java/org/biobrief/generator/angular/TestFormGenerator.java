package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	@Test
	public void createCtdbEditTrialForm()
	{
		String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms2.xlsx";
		String dictDir="c:/workspace/hucgc/data/dictionary";
		String srcDir="C:/workspace/hucgc/.temp/generator";
		String outDir=srcDir;
		String[] argv={xlsxfile, dictDir, srcDir, outDir};
		
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
package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	private static final String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms8.xlsx";
	private static final String dictDir="c:/workspace/hucgc/data/dictionary";
	private static final String tempDir="C:/workspace/hucgc/.temp/generator";
	//private static final String outDir=srcDir;
	
	@Test
	public void createForms()
	{
		String[] argv={xlsxfile, dictDir, tempDir};
		FormGenerator.main(argv);
	}
	
//	@Test
//	public void createFreemarkerCtdbEditTrialForm()
//	{
//		String[] argv={xlsxfile, dictDir, tempDir};
//		FormGenerator.main(argv);
//	}
}
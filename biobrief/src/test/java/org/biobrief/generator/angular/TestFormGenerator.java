package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	private static final String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms6.xlsx";
	private static final String dictDir="c:/workspace/hucgc/data/dictionary";
	private static final String srcDir="C:/workspace/hucgc/.temp/generator";
	private static final String outDir=srcDir;
	
	@Test
	public void createAngularCtdbEditTrialForm()
	{
		RenderMode mode=RenderMode.ANGULAR;
		String[] argv={xlsxfile, dictDir, srcDir, outDir, mode.name()};
		FormGenerator.main(argv);
	}
	
	@Test
	public void createFreemarkerCtdbEditTrialForm()
	{
		RenderMode mode=RenderMode.FREEMARKER;	
		String[] argv={xlsxfile, dictDir, srcDir, outDir, mode.name()};
		FormGenerator.main(argv);
	}
}
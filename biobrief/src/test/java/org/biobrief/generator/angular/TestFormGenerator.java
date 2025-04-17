package org.biobrief.generator.angular;

import org.biobrief.generator.angular.AngularGeneratorParams.FormGeneratorParams;
import org.biobrief.util.Context;
import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	private static final String baseDir="C:/workspace/hucgc";
	private static final String dataDir=baseDir+"/data";
	private static final String dictDir=dataDir+"/dictionary";
	private static final String templateDir=dataDir+"/templates";
	private static final String tempDir="C:/workspace/hucgc/.temp/generator";
	private static final String formDir=templateDir+"/forms";
	//private static final String gridDir=templateDir+"/grids";
	
	@Test
	public void generateForms()
	{
		Context context=new Context();
		String xlsxfile=formDir+"/hucgc-forms11.xlsx";
		FormGeneratorParams params=new FormGeneratorParams(baseDir, xlsxfile, dictDir, tempDir);
		FormGenerator.generate(params, context.getOut());
	}
}


//@Test
//public void createForms()
//{
//	String[] argv={xlsxfile, dictDir, tempDir};
//	FormGenerator.main(argv);
//}

//@Test
//public void createFreemarkerCtdbEditTrialForm()
//{
//	String[] argv={xlsxfile, dictDir, tempDir};
//	FormGenerator.main(argv);
//}
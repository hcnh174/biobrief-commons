package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	@Test
	public void creatFieldset()
	{
		String xlsxfile="C:/workspace/hucgc/data/templates/forms/hucgc-forms.xlsx";
		String srcDir="C:/temp/generator/forms";
		String outDir="C:/temp/generator/forms";
		String[] argv={xlsxfile, srcDir, outDir};
		FormGenerator.main(argv);
	}
}
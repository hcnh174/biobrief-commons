package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestFormGenerator
public class TestFormGenerator
{
	@Test
	public void creatFieldset()
	{
		String xlsxfile="C:/workspace/gangenome/data/templates/forms/ekipane-forms.xlsx";
		String outDir="C:/temp/generator/forms";
		String[] argv={xlsxfile, outDir};
		FormGenerator.main(argv);
	}
}
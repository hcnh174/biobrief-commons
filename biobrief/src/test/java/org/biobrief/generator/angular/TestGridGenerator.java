package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestGridGenerator
public class TestGridGenerator
{
	@Test
	public void creatFieldset()
	{
		String template="C:/workspace/gangenome/data/templates/grids/report-grids.xlsx";
		String outDir="C:/temp/generator/grids";
		String[] argv={template, outDir};
		GridGenerator.main(argv);
	}
}
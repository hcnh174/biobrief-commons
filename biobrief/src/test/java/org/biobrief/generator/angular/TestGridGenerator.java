package org.biobrief.generator.angular;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestGridGenerator
public class TestGridGenerator
{
	@Test
	public void creatFieldset()
	{
		String template="C:/workspace/gangenome/data/templates/grids/report-grids.xlsx";
		String srcDir="C:/workspace/gangenome/gangenome-angular2/src/app/reports/view/grids";
		String outDir="C:/temp/generator/grids";
		String[] argv={template, srcDir, outDir};
		GridGenerator.main(argv);
	}
}
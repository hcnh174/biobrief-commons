package org.biobrief.generator.angular;

import org.biobrief.generator.angular.AngularGeneratorParams.GridGeneratorParams;
import org.biobrief.generator.templates.Grid;
import org.biobrief.util.MessageWriter;

//create an in-memory excel template
public class GridGenerator extends AbstractGridGenerator
{
	public static void main(String[] argv)
	{
		String template=argv[0];
		String srcDir=argv[1];
		String outDir=argv[2];
		System.out.println("template="+template);
		System.out.println("srcDir="+srcDir);
		System.out.println("outDir="+outDir);
		
		GridGeneratorParams params=new GridGeneratorParams(template, srcDir, outDir);
		MessageWriter out=new MessageWriter();
		generate(params, out);
	}
	
	public static void generate(GridGeneratorParams params, MessageWriter writer)
	{
		GridGenerator generator=new GridGenerator(params, writer);
		generator.generate(params.getTemplate());
	}
	
	//////////////////////////////////////////
	
	private GridGenerator(GridGeneratorParams params, MessageWriter writer)
	{
		super(params, writer);//, true, true);
	}
	
	@Override
	protected AbstractAngularGrid createAngularGrid(Grid grid)
	{
		return new PrimeTable(grid);
	}
}

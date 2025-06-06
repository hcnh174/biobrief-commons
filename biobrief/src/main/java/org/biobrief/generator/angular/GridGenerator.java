package org.biobrief.generator.angular;

import org.biobrief.generator.angular.AngularGeneratorParams.GridGeneratorParams;
import org.biobrief.generator.templates.Grid;
import org.biobrief.util.MessageWriter;

//create an in-memory excel template
public class GridGenerator extends AbstractGridGenerator
{
	public static void main(String[] argv)
	{
		String baseDir=argv[0];
		String template=argv[1];
		String dictDir=argv[2];
		String tempDir=argv[3];
		//RenderMode mode=RenderMode.valueOf(argv[4]);
		
		System.out.println("baseDir="+baseDir);
		System.out.println("template="+template);
		System.out.println("dictDir="+dictDir);
		System.out.println("tempDir="+tempDir);
		//System.out.println("mode="+mode);
		
		GridGeneratorParams params=new GridGeneratorParams(baseDir, template, dictDir, tempDir);//, mode);
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

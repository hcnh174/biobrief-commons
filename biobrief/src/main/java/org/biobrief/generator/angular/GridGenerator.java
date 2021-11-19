package org.biobrief.generator.angular;

import org.biobrief.generator.Util;
import org.biobrief.generator.angular.AngularGeneratorParams.GridGeneratorParams;
import org.biobrief.generator.templates.Grid;
import org.biobrief.util.MessageWriter;

//create an in-memory excel template
public class GridGenerator extends AbstractGridGenerator
{
	public static void generate(GridGeneratorParams params, MessageWriter writer)
	{
		AbstractGridGenerator generator=new GridGenerator(params, writer);
		generator.generate();
	}
	
	public static void generate(String name, GridGeneratorParams params, MessageWriter writer)
	{
		Util.checkName(name);
		AbstractGridGenerator generator=new GridGenerator(params, writer);
		generator.generate(params.getDir()+"/"+name+".xlsx");
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

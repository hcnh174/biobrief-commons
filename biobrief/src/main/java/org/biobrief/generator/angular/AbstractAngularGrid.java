package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.templates.Grid;

public abstract class AbstractAngularGrid extends AbstractHtmlRenderer implements AngularLayout
{	
	protected final Grid grid;
	
	public AbstractAngularGrid(Grid grid)
	{
		this.grid=grid;
	}
	
	@Override
	public String getName()
	{
		return grid.getName();
	}
	
	public RenderMode getRenderMode()
	{
		return grid.getParams().getRenderMode();
	}
	
	public String getHtmlFilename()
	{
		return grid.getParams().getHtmlFilename();
	}
	
	public String getTypescriptFilename()
	{
		return grid.getParams().getTypescriptFilename();
		//return getFilenameRoot(srcDir)+".ts";
	}
	
	/*
	public String getHtmlFilename(String srcDir)
	{
		return getFilenameRoot(srcDir)+".html";
	}
	
	public String getTypescriptFilename(String srcDir)
	{
		return getFilenameRoot(srcDir)+".ts";
	}
	
	private String getFilenameRoot(String srcDir)
	{
		return srcDir+"/"+grid.getName()+".component";
	}
	*/
	
	public String toTypescript()
	{
		return "";
	}
}

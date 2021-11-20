package org.biobrief.generator.angular;

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
	
	public String toTypescript()
	{
		return "";
	}
}

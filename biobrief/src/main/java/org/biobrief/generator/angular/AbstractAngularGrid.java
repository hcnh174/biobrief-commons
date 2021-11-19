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
	
	public String getHtmlFilename()
	{
		return getFilenameRoot()+".html";
	}
	
	public String getTypescriptFilename()
	{
		return getFilenameRoot()+".ts";
	}
	
	private String getFilenameRoot()
	{
		return grid.getGroup()+"/view/grids/"+grid.getName()+".component";
	}
	
	public String toTypescript()
	{
		return "";
	}
}

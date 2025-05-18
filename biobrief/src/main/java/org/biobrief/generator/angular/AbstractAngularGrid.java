package org.biobrief.generator.angular;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
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
		return findFiles(".html").get(0);
		//return grid.getParams().getHtmlFilename();
	}
	
	public String getTypescriptFilename()
	{
		return findFiles(".ts").get(0);
		//return grid.getParams().getTypescriptFilename();
	}
	
	private List<String> findFiles(String suffix)
	{
		List<String> list=Lists.newArrayList();
		for (String filename : grid.getParams().getFiles())
		{
			if (filename.endsWith(suffix))
				list.add(filename);
		}
		return list;
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

package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.util.CException;

public class RenderParams
{
	private final RenderMode mode;
	
	public RenderParams(RenderMode mode)
	{
		this.mode=mode;
	}
	
	public RenderMode getMode(){return mode;}
	
	public boolean isAngular()
	{
		return mode==RenderMode.ANGULAR;
	}
	
	public boolean isFreemarker()
	{
		return mode==RenderMode.FREEMARKER;
	}
	
	public String noHandler()
	{
		throw new CException("no handler for render mode: "+mode);
	}
}

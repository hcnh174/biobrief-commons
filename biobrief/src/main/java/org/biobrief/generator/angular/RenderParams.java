package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.util.CException;

public class RenderParams
{
	private final RenderMode renderMode;
	
	public RenderParams(RenderMode renderMode)
	{
		this.renderMode=renderMode;
	}
	
	public RenderMode getMode(){return renderMode;}
	
	public boolean isAngular()
	{
		return renderMode==RenderMode.angular;
	}
	
	public boolean isFreemarker()
	{
		return renderMode==RenderMode.freemarker;
	}
	
	public String noHandler()
	{
		throw new CException("no handler for render mode: "+renderMode);
	}
}

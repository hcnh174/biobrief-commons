package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;

public interface AngularLayout extends HtmlRenderer
{	
	public String getName();
	public RenderMode getRenderMode();
}

package org.biobrief.generator.angular;

public interface HtmlRenderer
{
	String render(RenderParams params);
	
	boolean grow();
}

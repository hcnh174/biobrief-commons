package org.biobrief.generator.angular;

public class ValueRenderer extends AbstractHtmlRenderer
{
	private String value;
	
	public ValueRenderer(String value)
	{
		this.value=value;
	}
	
	@Override
	public boolean grow()
	{
		return false;
	}
	
	@Override
	protected void render(RenderParams params, StringBuilder buffer)
	{
		if (value==null)
			return;
		buffer.append(value);
	}
}
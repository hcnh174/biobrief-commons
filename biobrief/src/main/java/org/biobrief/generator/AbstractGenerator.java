package org.biobrief.generator;

public abstract class AbstractGenerator
{
	protected final GeneratorParams params;
	
	public AbstractGenerator(GeneratorParams params)
	{
		this.params=params;
	}
}

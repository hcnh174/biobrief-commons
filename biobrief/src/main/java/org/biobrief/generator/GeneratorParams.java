package org.biobrief.generator;

import org.biobrief.dictionary.Dictionary;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class GeneratorParams
{
	protected String baseDir;
	protected Boolean overwrite=false;
	
	public GeneratorParams(String baseDir)
	{
		this.baseDir=baseDir;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class EntityGeneratorParams extends GeneratorParams
	{
		protected Dictionary dictionary;
		
		public EntityGeneratorParams(String baseDir, Dictionary dictionary)
		{
			super(baseDir);
			this.dictionary=dictionary;
		}
	}
}
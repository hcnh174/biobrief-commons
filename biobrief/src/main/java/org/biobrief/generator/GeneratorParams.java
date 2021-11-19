package org.biobrief.generator;

import org.biobrief.dictionary.Dictionary;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class GeneratorParams
{
//	protected Dictionary dictionary;
//	protected FileType fileType;
//	protected String dir;
//	protected String tmpDir;
//	protected RenderMode mode;
	protected Boolean overwrite=false;
	
//	public GeneratorParams(FileType fileType, Dictionary dictionary)
//	{
//		this.fileType
//		this.dictionary=dictionary;
//	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class EntityGeneratorParams extends GeneratorParams
	{
		protected Dictionary dictionary;
		
		public EntityGeneratorParams(Dictionary dictionary)
		{
			this.dictionary=dictionary;
		}
	}
}
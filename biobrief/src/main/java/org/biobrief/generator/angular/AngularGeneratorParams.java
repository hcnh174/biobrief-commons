package org.biobrief.generator.angular;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class AngularGeneratorParams extends EntityGeneratorParams
{
	protected String tempDir;
	protected Boolean overwrite=true;
	
	public AngularGeneratorParams(String baseDir, String dictDir, String tempDir)
	{
		super(baseDir, new Dictionary(dictDir));
		this.tempDir=tempDir;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractTemplateGeneratorParams extends AngularGeneratorParams
	{
		protected String template;
		
		public AbstractTemplateGeneratorParams(String baseDir, String template, String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
		{
			super(baseDir, dictDir, tempDir);
			this.template=template;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public FormGeneratorParams(String baseDir, String template, String dictDir, String tempDir)
		{
			super(baseDir, template, dictDir, tempDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class GridGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public GridGeneratorParams(String baseDir, String template, String dictDir, String tempDir)
		{
			super(baseDir, template, dictDir, tempDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class HandsontableGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public HandsontableGeneratorParams(String baseDir, String template, String dictDir, String tempDir)
		{
			super(baseDir, template, dictDir, tempDir);
		}
	}
}
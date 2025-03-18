package org.biobrief.generator.angular;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class AngularGeneratorParams extends EntityGeneratorParams
{
	//protected String srcDir;
	//protected String outDir;
	//protected RenderMode mode;//=RenderMode.ANGULAR;
	protected String tempDir;
	protected Boolean overwrite=true;
	
	public AngularGeneratorParams(String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
	{
		super(new Dictionary(dictDir));
		this.tempDir=tempDir;
		//this.srcDir=srcDir;
		//this.outDir=outDir;
		//this.mode=mode;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ModelGeneratorParams extends AngularGeneratorParams
	{
		public ModelGeneratorParams(String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
		{
			super(dictDir, tempDir);//, srcDir, outDir);//, mode);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractTemplateGeneratorParams extends AngularGeneratorParams
	{
		protected String template;
		
		public AbstractTemplateGeneratorParams(String template, String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
		{
			super(dictDir, tempDir);//, srcDir, outDir);//, mode);
			this.template=template;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public FormGeneratorParams(String template, String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
		{
			super(template, dictDir, tempDir);//, srcDir, outDir);//, mode);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class GridGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public GridGeneratorParams(String template, String dictDir, String tempDir)//, String srcDir, String outDir)//, RenderMode mode)
		{
			super(template, dictDir, tempDir);//, srcDir, outDir);//, mode);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class HandsontableGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public HandsontableGeneratorParams(String template, String dictDir, String tempDir)//, RenderMode mode)
		{
			super(template, dictDir, tempDir);//, srcDir, outDir);//, mode);
		}
	}
}
package org.biobrief.generator.angular;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class AngularGeneratorParams extends EntityGeneratorParams
{
	//protected String dir;
	protected String outDir;
	protected RenderMode mode=RenderMode.ANGULAR;
	protected Boolean overwrite=false;
	
	public AngularGeneratorParams(Dictionary dictionary, String outDir)
	{
		super(dictionary);
		//this.dir=dir;
		this.outDir=outDir;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ModelGeneratorParams extends AngularGeneratorParams
	{
		public ModelGeneratorParams(Dictionary dictionary, String outDir)
		{
			super(dictionary, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractTemplateGeneratorParams extends AngularGeneratorParams
	{
		protected String template;
		
		public AbstractTemplateGeneratorParams(Dictionary dictionary, String template, String outDir)
		{
			super(dictionary, outDir);
			this.template=template;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormGeneratorParams extends AbstractTemplateGeneratorParams
	{
		
		public FormGeneratorParams(Dictionary dictionary, String template, String outDir)
		{
			super(dictionary, template, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class GridGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public GridGeneratorParams(Dictionary dictionary, String template, String outDir)
		{
			super(dictionary, template, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class HandsontableGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public HandsontableGeneratorParams(Dictionary dictionary, String template, String outDir)
		{
			super(dictionary, template, outDir);
		}
	}
}
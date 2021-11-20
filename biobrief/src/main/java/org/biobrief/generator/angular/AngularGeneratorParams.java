package org.biobrief.generator.angular;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class AngularGeneratorParams extends EntityGeneratorParams
{
	protected String srcDir;
	protected String outDir;
	protected RenderMode mode=RenderMode.ANGULAR;
	protected Boolean overwrite=false;
	
	public AngularGeneratorParams(String srcDir, String outDir)
	{
		super(new Dictionary());
		this.srcDir=srcDir;
		this.outDir=outDir;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ModelGeneratorParams extends AngularGeneratorParams
	{
		public ModelGeneratorParams(String srcDir, String outDir)
		{
			super(srcDir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class AbstractTemplateGeneratorParams extends AngularGeneratorParams
	{
		protected String template;
		
		public AbstractTemplateGeneratorParams(String template, String srcDir, String outDir)
		{
			super(srcDir, outDir);
			this.template=template;
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormGeneratorParams extends AbstractTemplateGeneratorParams
	{
		
		public FormGeneratorParams(String template, String srcDir, String outDir)
		{
			super(template, srcDir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class GridGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public GridGeneratorParams(String template, String srcDir, String outDir)
		{
			super(template, srcDir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class HandsontableGeneratorParams extends AbstractTemplateGeneratorParams
	{
		public HandsontableGeneratorParams(String template, String srcDir, String outDir)
		{
			super(template, srcDir, outDir);
		}
	}
}
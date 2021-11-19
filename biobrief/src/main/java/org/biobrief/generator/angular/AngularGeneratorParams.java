package org.biobrief.generator.angular;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
public class AngularGeneratorParams extends EntityGeneratorParams
{
	protected String dir;
	protected String outDir;
	protected RenderMode mode=RenderMode.ANGULAR;
	protected Boolean overwrite=false;
	
	public AngularGeneratorParams(Dictionary dictionary, String dir, String outDir)
	{
		super(dictionary);
		this.dir=dir;
		this.outDir=outDir;
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ModelGeneratorParams extends AngularGeneratorParams
	{
		public ModelGeneratorParams(Dictionary dictionary, String dir, String outDir)
		{
			super(dictionary, dir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FormGeneratorParams extends AngularGeneratorParams
	{
		public FormGeneratorParams(Dictionary dictionary, String dir, String outDir)
		{
			super(dictionary, dir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class GridGeneratorParams extends AngularGeneratorParams
	{
		public GridGeneratorParams(Dictionary dictionary, String dir, String outDir)
		{
			super(dictionary, dir, outDir);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class HandsontableGeneratorParams extends AngularGeneratorParams
	{
		public HandsontableGeneratorParams(Dictionary dictionary, String dir, String outDir)
		{
			super(dictionary, dir, outDir);
		}
	}
}
package org.biobrief.generator.java;

import java.util.List;

import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;

public final class ClassGenerator extends AbstractClassGenerator
{
	public static void generate(ClassGeneratorParams params)
	{
		ClassGenerator generator=new ClassGenerator(params);
		generator.generate();
	}
	
	private ClassGenerator(ClassGeneratorParams params)
	{
		super(params);
	}
	
	@Override
	protected List<FieldDefinition> getFieldDefinitions(EntityDefinition entityType)
	{
		return getGeneratedFieldDefinitions(entityType);
	}
	
	@Override
	protected String getClassFile(EntityDefinition entityType)
	{
		if (!entityType.isClassEntity())
			return null;
		//String path=properties.getPackageDir(entityType.getGroup());
		//return path+entityType.getClassFile();
		return ((ClassGeneratorParams)params).getClassfile();
	}
	
	//////////////////////////
}

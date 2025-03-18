package org.biobrief.generator.angular;

import java.util.List;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.GroupDefinition;
import org.biobrief.generator.GeneratorException;
import org.biobrief.generator.Util;
import org.biobrief.generator.angular.AngularGeneratorParams.ModelGeneratorParams;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;

import com.google.common.collect.Lists;

public final class ModelGenerator
{
	public static void generate(ModelGeneratorParams params, MessageWriter writer)
	{
		for (EntityDefinition entityType : getEntityDefinitions(params.getDictionary()))
		{
			Model model=new Model(entityType);
			updateModelFile(params, entityType, model, writer);
		}
	}
	
	private static List<EntityDefinition> getEntityDefinitions(Dictionary dictionary)
	{		
		List<EntityDefinition> list=Lists.newArrayList();
		for (GroupDefinition group : dictionary.getEntityGroups())
		{
			list.addAll(group.getEntities());
		}
		return list;
	}
	
	private static void updateModelFile(ModelGeneratorParams params, EntityDefinition entityType, Model model, MessageWriter writer)
	{
		String modelfile=params.getOutDir()+"/"+model.getFilename();
		if (!FileHelper.exists(modelfile))
			throw new CException("cannot find model file: "+modelfile);
		writer.println("model file: "+modelfile);
		try
		{
			String str=FileHelper.readFile(modelfile);
			str=Util.insertText(Util.DECLARATIONS, str, model.createDeclarations(), true);
		}
		catch (GeneratorException e)
		{
			e.setFilename(modelfile);
			throw e;
		}
	}
}

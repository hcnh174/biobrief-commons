package org.biobrief.generator.java;

import java.util.Collection;
import java.util.List;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.dictionary.GroupDefinition;
import org.biobrief.generator.AbstractGenerator;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;
import org.biobrief.generator.Util;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class AbstractClassGenerator extends AbstractGenerator
{	
	public AbstractClassGenerator(ClassGeneratorParams params)
	{
		super(params);
	}
	
	public void generate()
	{
		for (GroupDefinition group : ((ClassGeneratorParams)params).getDictionary().getEntityGroups())
		{
			generate(group);
		}
	}
	
	public void generate(GroupDefinition group)
	{
		for (EntityDefinition entityType : group.getEntities())
		{
			updateClassFile(entityType);
		}
	}

	protected abstract String getClassFile(EntityDefinition entityType);
	
	private final void updateClassFile(EntityDefinition entityType)
	{
		String classfile=getClassFile(entityType);
		if (classfile==null)
			return;
		try
		{
			String str=FileHelper.readFile(classfile);
			str=Util.insertText(Util.DECLARATIONS, str, createDeclarations(entityType), true);
			str=Util.insertText(Util.INIT, str, createInit(entityType), false);
			str=Util.insertText(Util.ACCESSORS, str, createAccessors(entityType), false);
			replaceFile(classfile, str, entityType);
		}
		catch (CException e)
		{
			throw new CException("error updating class file: "+classfile, e);
		}
	}
	
	protected void replaceFile(String classfile, String str, EntityDefinition entityType)
	{
//		Util.replaceFile(FileType.CLASS, classfile, str, params.getOverwrite(), entityType.getGroup());
	}
	
	private final String createDeclarations(EntityDefinition entityType)
	{
		List<String> buffer=Lists.newArrayList();
		for (FieldDefinition field : getFieldDefinitions(entityType))
		{
			add(buffer, createDeclaration(field));
		}
		return StringHelper.join(buffer, "");
	}
	
	protected String createDeclaration(FieldDefinition field)
	{
		return field.createDeclaration();
	}
	
	private final String createAccessors(EntityDefinition entityType)
	{
		List<String> buffer=Lists.newArrayList();
		for (FieldDefinition field : getFieldDefinitions(entityType))
		{
			add(buffer, createAccessors(field));
		}
		return StringHelper.join(buffer, "\n");
	}
	
	protected String createAccessors(FieldDefinition field)
	{
		return field.createAccessors();
	}
	
	////////////////////////////////////
	
	private final String createInit(EntityDefinition entityType)
	{
		List<String> buffer=Lists.newArrayList();
		for (FieldDefinition field : getFieldDefinitions(entityType))
		{
			add(buffer, createInit(field));
		}
		return StringHelper.join(buffer, "");
	}
	
	protected String createInit(FieldDefinition field)
	{
		return field.createInit();
	}

	//////////////////////////////////////////////////////////////////////////////
	
	protected Collection<FieldDefinition> getFieldDefinitions(EntityDefinition entityType)
	{
		return entityType.getFieldDefinitions();
	}
	
	//////////////////////////////////////////////////////
	
	private static void add(List<String> buffer, String value)
	{
		if (value!=null && !value.equals(""))
			buffer.add(value);
	}
	
	protected static List<FieldDefinition> getGeneratedFieldDefinitions(EntityDefinition entityType)
	{
		List<FieldDefinition> fields=Lists.newArrayList();
		for (FieldDefinition field : entityType.getFieldDefinitions())
		{
			if (field.isGenerated())
				fields.add(field);
		}
		return fields;
	}
	
	////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class ClassGeneratorParams extends EntityGeneratorParams
	{
		protected String classfile;
		
		public ClassGeneratorParams(String baseDir, Dictionary dictionary, String classfile)
		{
			super(baseDir, dictionary);
			this.classfile=classfile;
		}
	}
}

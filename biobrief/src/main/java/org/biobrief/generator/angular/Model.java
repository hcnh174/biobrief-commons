package org.biobrief.generator.angular;

import java.util.List;

import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

public class Model
{
	protected final EntityDefinition entityType;	
	protected String namespace;
	protected String entityName;
	protected List<Field> fields=Lists.newArrayList();
	
	public Model(EntityDefinition entityType)
	{
		this.entityType=entityType;
		for (FieldDefinition field : entityType.getFieldDefinitions())
		{
			if (!field.isInherited())
				fields.add(new Field(field));
		}
	}
	
	public String createDeclarations()
	{
		List<String> lines=Lists.newArrayList();
		for (Field field : fields)
		{
			lines.add(field.createDeclaration());//"\t"+
		}
		return StringHelper.join(lines, "\n");//+"\n"
	}
	
	public String createInit()
	{
		List<String> lines=Lists.newArrayList();
		for (Field field : fields)
		{
			field.createInit(lines);
		}
		return StringHelper.join(lines, "\n");
	}
	
	public String getFilename()
	{
		String path=entityType.getGroup().getName().toLowerCase();
		path+="/model";
		if (StringHelper.hasContent(entityType.getSubdir()))
			path+="/"+entityType.getSubdir();
		String name=StringHelper.toKebobCase(entityType.getName());
		return path+"/"+name+".ts";
	}
	
	public static class Field
	{
		protected FieldDefinition field;
		protected String name;
		protected String type;
		protected boolean multi;
		protected boolean nested;
		
		public Field(FieldDefinition field)
		{
			this.field=field;
			this.name=field.getName();
			this.type=field.getTsType();//getType(field);
			this.multi=field.isMulti();
			this.nested=field.isEntity();
		}
		
		public String createDeclaration()
		{
			return name+": "+type+";";
		}
		
		public void createInit(List<String> inits)
		{
			if (!field.isGenerated())
				return;
			String dflt=field.getDefault();
			if (!StringHelper.hasContent(dflt))
				return;
			inits.add(this.name+": "+dflt); 
		}
			
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public String getType(){return this.type;}
		public void setType(final String type){this.type=type;}
	}
}
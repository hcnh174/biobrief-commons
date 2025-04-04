package org.biobrief.generator.sql;

import java.util.List;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.dictionary.GroupDefinition;
import org.biobrief.generator.AbstractGenerator;
import org.biobrief.generator.GeneratorParams.EntityGeneratorParams;
import org.biobrief.generator.Util;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

public final class SqlTableGenerator extends AbstractGenerator
{	
	private SqlTableGenerator(SqlGeneratorParams params)
	{
		super(params);
	}
	
	public static void generate(SqlGeneratorParams params)
	{
		SqlTableGenerator generator=new SqlTableGenerator(params);
		generator.generate();
	}
	
	private void generate()
	{
		Dictionary dictionary=((SqlGeneratorParams)params).getDictionary();
		for (GroupDefinition group : dictionary.getEntityGroups())
		{
			generate(group);
		}
//		updateSqlFile(dictionary.getGroup("legacy").getEntity("nash"));
//		updateSqlFile(dictionary.getGroup("legacy").getEntity("fecalsamples"));
	}
	
	private void generate(GroupDefinition group)
	{
		for (EntityDefinition entityType : group.getEntities())
		{
			updateSqlFile(entityType);
		}
	}
	
	private void updateSqlFile(EntityDefinition entityType)
	{
//		Optional<String> sqldir=properties.getSqlDir(entityType.getGroup());
//		if (!sqldir.isPresent())
//			return;
		//String sqlfile=sqldir.get()+"/"+entityType.getTable()+".sql";
		String sqlfile=((SqlGeneratorParams)params).getSqlfile();
		String str=FileHelper.readFile(sqlfile);
		str=Util.insertText(Util.FIELDS, str, createSql(entityType), "--", true);
//		Util.replaceFile(FileType.SQL, sqlfile, str, params.getOverwrite(), entityType.getGroup());
	}
	
	private String createSql(EntityDefinition entityType)
	{
		List<String> buffer=Lists.newArrayList();
		for (FieldDefinition field : entityType.getFieldDefinitions())
		{
			if (field.isCalculated())
				{continue;}
			buffer.add(field.createSql());
		}
		return StringHelper.join(buffer, "");
	}
	
	/////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SqlGeneratorParams extends EntityGeneratorParams
	{
		protected String sqlfile;
		
		public SqlGeneratorParams(String baseDir, Dictionary dictionary, String sqlfile)
		{
			super(baseDir, dictionary);
			this.sqlfile=sqlfile;
		}
	}
}

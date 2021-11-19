package org.biobrief.generator;

import org.biobrief.generator.java.AbstractClassGenerator.ClassGeneratorParams;
import org.biobrief.generator.java.ClassGenerator;
import org.biobrief.generator.sql.SqlTableGenerator;
import org.biobrief.generator.sql.SqlTableGenerator.SqlGeneratorParams;
import org.biobrief.util.MessageWriter;
import org.springframework.stereotype.Service;

@Service
public class GeneratorService extends AbstractGeneratorService
{	
//	private final GeneratorProperties properties;
//	
//	public GeneratorService(GeneratorProperties properties)
//	{
//		this.properties=properties;
//	}
	
	public void generateClasses(ClassGeneratorParams params, MessageWriter writer)
	{
		//Util.resetClassDirectory();
		ClassGenerator.generate(params);
	}
	
//	public void generateGraphQLTypings(GeneratorParams params, MessageWriter writer)
//	{
//		//Util.resetGraphQLDirectory();
//		GraphQLTypeGenerator.generate(params);
//	}

	public void generateSqlTables(SqlGeneratorParams params, MessageWriter writer)
	{
		//Util.resetSqlDirectory();
		SqlTableGenerator.generate(params);
	}
	
//	public void generateSqlViews(boolean overwrite, MessageWriter writer)
//	{
//		Dictionary dict=getDictionary();
//		Util.resetSqlDirectory();
//		GroupDefinition group=dict.getGroup(EntityGroup.patients);
//		SqlViewGenerator.generate(properties, group.getEntity("Patient"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("Interview"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("Hbv"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("Hcv"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("Hcc"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("Condition"), overwrite);
//		SqlViewGenerator.generate(properties, group.getEntity("SnpData"), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_PATIENT), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_INTERVIEW), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_HBV), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_HCV), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_HCC), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_CONDITION), overwrite);
////		SqlViewGenerator.generate(properties, dict.getEntity(EntityType.PATIENTS_SNPDATA), overwrite);
//	}
	
//	public void generateEnums(boolean overwrite, MessageWriter writer)
//	{
//		EnumCodeGenerator.generate(properties, overwrite);
//	}
	
//	public void generateI18n(boolean overwrite, MessageWriter writer)
//	{
//		I18nGenerator.generate(overwrite);
//	}
}

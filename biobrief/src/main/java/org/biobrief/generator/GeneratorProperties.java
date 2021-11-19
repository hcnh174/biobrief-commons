package org.biobrief.generator;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.biobrief.dictionary.GroupDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Validated
@ConfigurationProperties("generator") @Data
public class GeneratorProperties
{
	@Valid @NotNull private String dir;//=Constants.BASE_DIR;
	@Valid @NotNull private String tmpDir;//=Constants.BASE_DIR;
	@Valid @NotNull private String angularDir;//=Constants.DATA_DIR+"/templates";
	@Valid @NotNull private String solrDir;//=CoreConstants.SOLR_DIR;
	@Valid @NotNull private String scriptsDir;//=CoreConstants.SCRIPTS_DIR;
	
//	public String getProjectDir(GroupDefinition group)
//	{
//		return dir+"/hlsg-"+group.getName();
//	}
//	
//	public String getPackageDir(GroupDefinition group)
//	{
//		String path=getProjectDir(group);
//		path+="/src/main/java";
//		path+="/org/hlsg/"+group.getName();
//		return path;
//	}
//	
//	public String getSqlDir()
//	{
//		return dir+"/sql";
//	}
//	
//	public Optional<String> getSqlDir(GroupDefinition group)
//	{
//		if (!group.getPersistenceType().isSql())
//			return Optional.empty();
//		return Optional.of(getSqlDir()+"/"+group.getName());
//	}
//	
//	public String getViewDir()
//	{
//		return getSqlDir()+"/views";
//	}
}
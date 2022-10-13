package org.biobrief.generator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
}
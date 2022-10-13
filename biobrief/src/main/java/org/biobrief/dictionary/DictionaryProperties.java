package org.biobrief.dictionary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("dictionary")
public class DictionaryProperties
{
	@Valid @NotNull private String dir;//=Constants.DATA_DIR+"/dictionary";

	public String getDir(){return this.dir;}
	public void setDir(final String dir){this.dir=dir;}
}
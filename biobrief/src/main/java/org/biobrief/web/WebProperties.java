package org.biobrief.web;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("web")
public class WebProperties
{
	@Valid @NotNull private String uploadDir;
	
	public String getUploadDir(){return this.uploadDir;}
	public void setUploadDir(final String uploadDir){this.uploadDir=uploadDir;}
}
package org.biobrief.web;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Validated @Data
@ConfigurationProperties("web")
public class WebProperties
{
	@Valid @NotNull private String uploadDir;
	@Valid @NotNull private String freemarkerPath;//"file:biobrief-app/src/main/resources/templates/"
}
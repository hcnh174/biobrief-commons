package org.biobrief.generator;

import org.biobrief.dictionary.DictionaryConfiguration;
import org.biobrief.generator.angular.AngularGeneratorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(GeneratorProperties.class)
@Import(DictionaryConfiguration.class)
public class GeneratorConfiguration
{
	{System.out.println("GeneratorConfiguration");}
	
	@Bean
	public GeneratorService generatorService()
	{
		return new GeneratorService();
	}
	@Bean
	public AngularGeneratorService angularGeneratorService()
	{
		return new AngularGeneratorService();
	}
}

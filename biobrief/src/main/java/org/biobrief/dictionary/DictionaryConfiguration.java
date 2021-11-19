package org.biobrief.dictionary;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DictionaryProperties.class)
public class DictionaryConfiguration
{
	{System.out.println("DictionaryConfiguration");}
	
	@Bean
	public DictionaryService dictionaryService(DictionaryProperties properties)
	{
		return new DictionaryService(properties);
	}
}

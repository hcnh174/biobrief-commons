package org.biobrief.pipelines;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(PipelineProperties.class)
public class PipelineConfiguration
{
	{System.out.println("PipelineConfiguration");}

	@Primary @Bean
	public PipelineProperties pipelineProperties()
	{
		return new PipelineProperties();
	}
	
	@Bean
	public PipelineService pipelineService()
	{
		return new PipelineService();
	}
}

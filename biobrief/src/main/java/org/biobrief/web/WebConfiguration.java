package org.biobrief.web;

import java.util.Properties;

import org.biobrief.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebConfiguration
{	
	{System.out.println("WebConfiguration");}
	
	@Autowired WebProperties properties;
	
	@Bean
	public FreeMarkerConfigurer freemarkerConfigurer()
	{
		FreeMarkerConfigurer configurer=new FreeMarkerConfigurer();
		String freemarkerPath=properties.getFreemarkerPath();
		configurer.setTemplateLoaderPath(freemarkerPath);
		configurer.setDefaultEncoding(StringHelper.UTF8);
		Properties settings=new Properties();
		settings.setProperty("default_encoding", StringHelper.UTF8);
		settings.setProperty("output_encoding", StringHelper.UTF8);
		configurer.setFreemarkerSettings(settings);
		return configurer;
	}
	
	@Bean
	public FreemarkerService freemarkerService()
	{
		FreeMarkerConfigurer configurer=freemarkerConfigurer();
		return new FreemarkerService(configurer.getConfiguration());
	}
	
	//http://stackoverflow.com/questions/27381781/java-spring-boot-how-to-map-my-app-root-to-index-html
	//http://docs.spring.io/spring/docs/5.0.0.RC1/javadoc-api/
	@Configuration
	public class MyWebMvcConfig implements WebMvcConfigurer
	{
		@Override
		public void addViewControllers(ViewControllerRegistry registry)
		{
			registry.addViewController("/").setViewName("forward:/index");//.html
		}
	}
}

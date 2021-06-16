package org.biobrief.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@ComponentScan(basePackages={"org.biobrief.mongo"})
@EnableAutoConfiguration
//@EnableConfigurationProperties(PatientProperties.class)
//@EntityScan(basePackages={"org.biobrief.patients"})
//@EnableMongoRepositories(basePackages={"org.biobrief.patients"})
public class MongoConfiguration
{
	{System.out.println("MongoConfiguration");}
	
	@Bean
	public GridFsDao gridFsDao()
	{
		return new GridFsDao();
	}
}

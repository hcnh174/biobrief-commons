package org.biobrief.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(SolrProperties.class)
public class SolrConfiguration
{
	{System.out.println("SolrConfiguration");}
	
	@Bean
	SolrService solrService(SolrProperties properties)
	{
		return new SolrService(properties);
	}
	
	@Bean
	SolrClient solrClient()
	{
		return SolrHelper.getSolrClient();
		//return new HttpSolrClient.Builder("http://1703-030.b.hiroshima-u.ac.jp:8983/solr").build();
	}
}

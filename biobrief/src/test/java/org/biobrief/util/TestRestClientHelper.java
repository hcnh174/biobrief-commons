package org.biobrief.util;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import lombok.Data;

//gradle --stacktrace --info test --tests *TestRestClientHelper
//https://github.com/spring-projects/spring-retry
//https://www.baeldung.com/spring-boot-testing
//https://stackoverflow.com/questions/27236216/is-it-possible-to-set-retrypolicy-in-spring-retry-based-on-httpstatus-status-cod
//https://stackoverflow.com/questions/24292373/spring-boot-rest-controller-how-to-return-different-http-status-codes
//@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
public class TestRestClientHelper
{	
	//@Test
	public void testRestClient()
	{
		RestClient restClient=RestClient.create();
		String result = restClient.get()
			.uri("https://example.com")
			.retrieve()
			.body(String.class);
		System.out.println(result);
	}
	
	//@Test
	public void testGet()
	{
		RestClient restClient=RestClient.create();
		MessageWriter out=new MessageWriter();
		String url="https://example.com";
		String result=RestClientHelper.get(restClient, url, out);
		System.out.println("result="+result);
	}
	
	@Test
	public void testGetClinvar()
	{
		RestClient restClient=RestClient.create();
		MessageWriter out=new MessageWriter();
		String url="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=clinvar&term=CDK4[gene]+%22copy+number+gain%22[Type%20of%20variation]&retmode=json";
		SearchResult result=RestClientHelper.get(restClient, url, SearchResult.class, out);
		System.out.println("result="+JsonHelper.toJson(result));
	}
	
	/////////////////////////////
	
	@Data
	public static class SearchResult
	{
		protected Header header;
		protected EsearchResult esearchresult;

		@Data
		public static class Header
		{
			protected String type;
			protected String version;
		}
		
		@Data
		public static class EsearchResult
		{
			protected Integer count;
			protected Integer retmax;
			protected Integer retstart;
			protected List<Integer> idlist=Lists.newArrayList();
		}
	}
}
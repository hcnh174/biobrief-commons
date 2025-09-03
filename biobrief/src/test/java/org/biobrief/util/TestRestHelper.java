package org.biobrief.util;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

//gradle --stacktrace --info test --tests *TestRestHelper
//https://github.com/spring-projects/spring-retry
//https://www.baeldung.com/spring-boot-testing
//https://stackoverflow.com/questions/27236216/is-it-possible-to-set-retrypolicy-in-spring-retry-based-on-httpstatus-status-cod
//https://stackoverflow.com/questions/24292373/spring-boot-rest-controller-how-to-return-different-http-status-codes
//@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(TestRestHelper.InfoServiceConfiguration.class)
public class TestRestHelper
{	
	@Autowired
	private InfoService infoService;
	
	@Test
	public void testRetry()
	{
		Info info=infoService.lookup();
		System.out.println("succeeded: info="+info);
	}
	
	@EnableRetry
	@Service
	public static class InfoService
	{
		private int count=0;
		
		@Retryable 
		public Info lookup()
		{
			System.out.println("InfoService.lookup count="+count++);
			String url="http://localhost:8888/api/json/util/unreliable?prob=0.5";
			RestTemplate restTemplate=new RestTemplate();
			
			HttpHeaders httpHeaders = new HttpHeaders();
			//httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, Object> params= new LinkedMultiValueMap<>();
			//params.add("prob", 0.1f);
			
			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, httpHeaders);
			//ResponseEntity<Info> response = (ResponseEntity<Info>)restTemplate.getForEntity(url, request , Info.class);
			ResponseEntity<Info> response = restTemplate.exchange(url, HttpMethod.GET, request, Info.class);//, 1);
			if (response.getStatusCode()==HttpStatus.SERVICE_UNAVAILABLE)// 503
				throw new CException("request failed: "+response.getStatusCode()+" for url "+url);
			return response.getBody();
			
			//out.println(StringHelper.toString(response));
			//return response.getBody();
			
			
//			HttpEntity<Info> request = new HttpEntity<Info>(httpHeaders);
//			ResponseEntity<Info> response = restTemplate.exchange(url, HttpMethod.GET, request, Info.class);//, 1);
//			
//			if (response.getStatusCode() != HttpStatus.OK)
//				throw new CException("request failed: "+response.getStatusCode()+" for url "+url);
//			Info info=response.getBody();
//			return info;
			//assertThat(value).isEqualTo("expected response");
		}
	}
	
//	public static class ServiceUnavailableException extends Exception
//	{
//		
//	}
	
	@TestConfiguration
    static class InfoServiceConfiguration
    {
        @Bean
        InfoService infoService()
        {
            return new InfoService();
        }
    }
	
	public static class Info
	{
		protected Date date=new Date();
		protected String osname=System.getProperty("os.name");
		
		public Date getDate(){return this.date;}
		public void setDate(final Date date){this.date=date;}

		public String getOsname(){return this.osname;}
		public void setOsname(final String osname){this.osname=osname;}
	}
}
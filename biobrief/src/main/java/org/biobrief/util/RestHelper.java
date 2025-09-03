package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;

//https://docs.spring.io/spring/docs/5.1.4.RELEASE/spring-framework-reference/web.html#spring-web
//https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate
//https://docs.spring.io/spring/docs/5.1.4.RELEASE/spring-framework-reference/web-reactive.html#webflux-client
//https://www.baeldung.com/spring-5-webclient
//https://www.baeldung.com/rest-template
//https://codeburst.io/this-is-how-easy-it-is-to-create-a-rest-api-8a25122ab1f3
//https://dzone.com/articles/doing-stuff-with-spring-webflux
//https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/
//https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate
//https://stackoverflow.com/questions/26003912/throttling-resttemplate-invocations
//https://stackoverflow.com/questions/54199734/how-to-implement-and-limit-api-calls-per-second-in-spring-rest
//https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client
//https://stackoverflow.com/questions/62557080/how-to-configure-and-build-a-custom-resttemplate-for-each-client-in-spring
public class RestHelper
{
	public static final Integer TIMEOUT=10;//seconds
	public static final Long DEFAULT_SLEEP=10000l;//millis
	
	public static String get(RestTemplate restTemplate, String url, MessageWriter out)
	{
		Map<String, Object> vars = Maps.newLinkedHashMap();
		return (String)restTemplate.getForObject(url, String.class, vars);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(RestTemplate restTemplate, String url, Class<?> cls, MessageWriter out)
	{
		Map<String, Object> vars = Maps.newLinkedHashMap();
		T result = (T)restTemplate.getForObject(url, cls, vars);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(RestTemplate restTemplate, String server, RestParams params, Class<?> cls, MessageWriter out)
	{
		Map<String, Object> vars = params.getVars();
		String url=server+params.getUrl();//"/" // todo breaking change! check for errors
		System.out.println("RestHelper.get url="+url);
		T result = (T)restTemplate.getForObject(url, cls, vars);
		return result;
	}
	
	//https://attacomsian.com/blog/spring-boot-resttemplate-get-request-parameters-headers
	//https://stackoverflow.com/questions/21101250/sending-get-request-with-authentication-headers-using-resttemplate
	@SuppressWarnings({ "rawtypes" })
	public static String get(RestTemplate restTemplate, String url, Headers headers, MessageWriter out)
	{
		HttpEntity request = new HttpEntity(headers.getHttpHeaders());
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class, 1);
		if (response.getStatusCode() != HttpStatus.OK)
			throw new CException("request failed: "+response.getStatusCode()+" for url "+url);
		return response.getBody(); 
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T post(RestTemplate restTemplate, String server, RestParams params, Class<?> cls, MessageWriter out)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> map=createMultiValueMap(params);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
		String url=server+params.getUrl();//"/"+ // todo breaking change! check for errors
		out.println("RestHelper.post url="+url);
		ResponseEntity<T> response = (ResponseEntity<T>)restTemplate.postForEntity(url, request , cls);
		out.println(StringHelper.toString(response));
		return response.getBody();
	}
	
	public static <P> String post(RestTemplate restTemplate, String url, P params, MessageWriter out)
	{
		HttpEntity<P> request = new HttpEntity<P>(params);
		ResponseEntity<String> response = (ResponseEntity<String>)restTemplate.postForEntity(url, request , String.class);
		return response.getBody();
	}
	
	//https://www.baeldung.com/spring-resttemplate-post-json
	public static String postJson(RestTemplate restTemplate, String url, String json, MessageWriter out)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(json, headers);
		return restTemplate.postForObject(url, request , String.class);
	}
	
	public static BufferedImage getImage(String server, String url, MessageWriter out)
	{
		return ImageHelper.getImage(server+"/"+url, out);
	}
	
	//https://javadeveloperzone.com/spring-boot/spring-boot-resttemplate-download-file-example/
	public static boolean downloadFile(RestTemplate restTemplate, String url, String filename, MessageWriter out)
	{
		try
		{
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<byte[]> response = restTemplate
					.exchange(url, HttpMethod.GET, entity, byte[].class);
			byte[] body=response.getBody();
			System.out.println("respone body: "+body);
			if (body==null)
				return false;
			Files.write(Paths.get(filename), response.getBody());
			return true;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	///////////////////////////////////////////////////////
	
	private static MultiValueMap<String, Object> createMultiValueMap(RestParams params)
	{
		MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
		for (Entry<String, Object> entry : params.getVars().entrySet())
		{
			map.add(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	//////////////////////////////////////////////////////////////
	
	public interface RestParams
	{
		Map<String, Object> getVars();
		String getUrl();
	}
	
	@SuppressWarnings("serial")
	public static class Headers extends LinkedHashMap<String, String>
	{
		public Headers() {}
		
		public Headers(String name, String value)
		{
			put(name, value);
		}
		
		public HttpHeaders getHttpHeaders()
		{
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			for (String name : keySet())
			{
				httpHeaders.set(name,  get(name));
			}
			return httpHeaders;
		}
	}
	
	public static String createKey(String url)
	{
		return url;
	}
	
	public static String createKey(String url, Object params)
	{
		return url+":"+StringHelper.toString(params);
	}
	
	public static void sleep(MessageWriter out)
	{
		sleep(DEFAULT_SLEEP, out);
	}
	
	public static void sleep(long millis, MessageWriter out)
	{
		ThreadHelper.sleep(millis, out);
	}
}
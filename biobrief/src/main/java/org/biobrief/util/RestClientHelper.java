package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

//https://www.baeldung.com/spring-boot-restclient
//https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient
//https://medium.com/@javedmj786/from-restClient-to-restclient-webclient-in-spring-boot-4-a-practical-migration-guide-dd2e02708f66?sk=60c20acf38525c601663352432e4abb2
////https://medium.com/@javedmj786/from-restClient-to-restclient-webclient-in-spring-boot-4-a-practical-migration-guide-dd2e02708f66
public class RestClientHelper
{
	public static final Integer TIMEOUT=10;//seconds
	public static final Long DEFAULT_SLEEP=10000l;//millis

	public static String get(RestClient restClient, String url, MessageWriter out)
	{
		return restClient.get()
				.uri(url)
				.retrieve()
				.body(String.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(RestClient restClient, String url, Class<?> cls, MessageWriter out)
	{
		return (T)restClient.get()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(cls);
	}
	
//	@SuppressWarnings("unchecked")
//	public static <T> T get(RestClient restClient, String server, RestParams params, Class<?> cls, MessageWriter out)
//	{
//		Map<String, Object> vars = params.getVars();
//		String url=server+params.getUrl();//"/" // todo breaking change! check for errors
//		System.out.println("RestHelper.get url="+url);
//		return (T)restClient.get()
//				.uri(url)
//				.accept(MediaType.APPLICATION_JSON)
//				.retrieve()
//				.body(cls);
//	}
	
	public static String get(RestClient restClient, String url, Headers headers, MessageWriter out)
	{
		ResponseEntity<String> response=restClient.get()
				.uri(url)
				.headers(h -> applyHeaders(h, headers))
				.retrieve()
				.toEntity(String.class);
		if (!response.getStatusCode().is2xxSuccessful()) 
			throw new CException("request failed: "+response.getStatusCode()+" for url "+url);
		return response.getBody();
	}
	
	//https://attacomsian.com/blog/spring-boot-restClient-get-request-parameters-headers
	//https://stackoverflow.com/questions/21101250/sending-get-request-with-authentication-headers-using-restClient
//	@SuppressWarnings({ "rawtypes" })
//	public static String get(RestClient restClient, String url, Headers headers, MessageWriter out)
//	{
//		HttpEntity request = new HttpEntity(headers.getHttpHeaders());
//		ResponseEntity<String> response = restClient.exchange(url, HttpMethod.GET, request, String.class, 1);
//		if (response.getStatusCode() != HttpStatus.OK)
//			throw new CException("request failed: "+response.getStatusCode()+" for url "+url);
//		return response.getBody(); 
//	}
	
	@SuppressWarnings("unchecked")
	public static <T> T post(RestClient restClient, String server, RestParams params, Class<?> cls, MessageWriter out)
	{
		String url=server+params.getUrl();//"/"+ // todo breaking change! check for errors
		out.println("RestHelper.post url="+url);
		MultiValueMap<String, Object> form=createMultiValueMap(params);
		T response=(T)restClient.post()
				.uri(url)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(cls);
		out.println(StringHelper.toString(response));
		return response;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public static <T> T post(RestClient restClient, String server, RestParams params, Class<?> cls, MessageWriter out)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> map=createMultiValueMap(params);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
		String url=server+params.getUrl();//"/"+ // todo breaking change! check for errors
		out.println("RestHelper.post url="+url);
		ResponseEntity<T> response = (ResponseEntity<T>)restClient.postForEntity(url, request , cls);
		out.println(StringHelper.toString(response));
		return response.getBody();
	}
	*/

	public static <P> String post(RestClient restClient, String url, P params, MessageWriter out)
	{
		return restClient.post()
				.uri(url)
				.body(params)
				.retrieve()
				.body(String.class);
	}
	
	/*
	public static <P> String post(RestClient restClient, String url, P params, MessageWriter out)
	{		
		HttpEntity<P> request = new HttpEntity<P>(params);
		ResponseEntity<String> response = (ResponseEntity<String>)restClient.postForEntity(url, request , String.class);
		return response.getBody();
	}
	*/

//	//https://www.baeldung.com/spring-restClient-post-json
//	public static String postJson(RestTemplate restTemplate, String url, String json, MessageWriter out)
//	{
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> request = new HttpEntity<String>(json, headers);
//		return restTemplate.postForObject(url, request , String.class);
//	}
	
	public static String postJson(RestClient restClient, String url, String json, MessageWriter out)
	{
		return restClient.post()
			.uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.body(json)
			.retrieve()
			.body(String.class);
	}
	
	public static BufferedImage getImage(String server, String url, MessageWriter out)
	{
		return ImageHelper.getImage(server+"/"+url, out);
	}
	
	@SuppressWarnings("unused")
	public static boolean downloadFile(RestClient restClient, String url, String filename, MessageWriter out)
	{
		try
		{
			return restClient.get()
				.uri(url)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.exchange((request, response) -> {
				
					if (!response.getStatusCode().is2xxSuccessful())
						return false;
				
					try (InputStream inputStream = response.getBody())
					{
						Files.copy(inputStream,	Paths.get(filename), 	StandardCopyOption.REPLACE_EXISTING);
					}
				
					return true;
				});
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
//	//https://javadeveloperzone.com/spring-boot/spring-boot-restClient-download-file-example/
//	public static boolean downloadFile(RestClient restClient, String url, String filename, MessageWriter out)
//	{
//		try
//		{
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
//			HttpEntity<String> entity = new HttpEntity<>(headers);
//			ResponseEntity<byte[]> response = restClient
//					.exchange(url, HttpMethod.GET, entity, byte[].class);
//			byte[] body=response.getBody();
//			System.out.println("respone body: "+body);
//			if (body==null)
//				return false;
//			Files.write(Paths.get(filename), response.getBody());
//			return true;
//		}
//		catch (Exception e)
//		{
//			throw new CException(e);
//		}
//	}
	
	///////////////////////////////////////////////////////
	
	private static MultiValueMap<String, Object> createMultiValueMap(RestParams params)
	{
		MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
		for (Map.Entry<String, Object> entry : params.getVars().entrySet())
		{
			map.add(entry.getKey(), entry.getValue());
		}
		return map;
	}

	private static void applyHeaders(HttpHeaders target, Map<String, String> headers)
	{
		if (headers != null)
			headers.forEach(target::set);
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
		
//		public HttpHeaders getHttpHeaders()
//		{
//			HttpHeaders httpHeaders = new HttpHeaders();
//			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			for (String name : keySet())
//			{
//				httpHeaders.set(name,  get(name));
//			}
//			return httpHeaders;
//		}
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
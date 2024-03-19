package org.biobrief.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Multimap;

//https://hc.apache.org/httpcomponents-client-ga/
//https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/QuickStart.java
//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html
//https://stackoverflow.com/questions/44927784/cookie-management-using-the-java-apache-httpclient-fluent-api?rq=1
public class HttpHelper
{	
	private HttpHelper(){}
	
	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e80
	//https://www.baeldung.com/convert-input-stream-to-string
	public static String getRequest(String baseurl, Map<String,Object> model, Map<String, String> headers)
	{
		String html="empty";
		try
		{
			String url=appendQueryString(baseurl, model);
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			for (String name : headers.keySet())
			{
				String value=headers.get(name);
				httpget.addHeader(name, value);	
			}
			
			CloseableHttpResponse response = httpclient.execute(httpget);
			try
			{
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					InputStream instream = entity.getContent();
					try
					{
						html = new BufferedReader(new InputStreamReader(instream, StandardCharsets.UTF_8))
							.lines().collect(Collectors.joining("\n"));
						return html;
					}
					finally
					{
						instream.close();
					}
				}
			}
			finally
			{
				response.close();
			}

		}
		catch (Exception e)
		{
			throw new CException(e);
		}
		return html;
	}
	
	public static String getRequest(String baseurl, Map<String,Object> model)
	{
		String url=appendQueryString(baseurl, model);
		//System.out.println("url="+url);
		try
		{	
			return Request.Get(url).execute().returnContent().asString();
		}
		catch (Exception e)
		{
			throw new CException("error handling GET request: "+url, e);
		}
	}
	
	public static String getRequest(String url, Object... args)
	{
		Map<String,Object> params=StringHelper.createMap(args);
		return getRequest(url, params);
	}
	
	//////////////////
	
	public static String postRequest(String url, Map<String,Object> params)
	{
		List<NameValuePair> pairs=createNameValuePairs(params);
		return postRequest(url, pairs);
	}
	
	public static String postRequest(String url, Multimap<String, Object> params)
	{
		List<NameValuePair> pairs=createNameValuePairs(params);
		return postRequest(url, pairs);
	}
		
	public static String postRequest(String url, List<NameValuePair> pairs)
	{
		try
		{
			return Request.Post(url)
					.bodyForm(pairs)
					.execute()
					.returnContent().asString();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String postRequest(String url, Object... args)
	{
		Map<String,Object> params=StringHelper.createMap(args);
		return postRequest(url, params);
	}
	
	///////////////////////////////////////////
	
	private static String appendQueryString(String url, Map<String,Object> model)
	{
		// if there's already a query string, just add an &
		char separator=(url.indexOf('?')==-1) ? '?' : '&';
		return url+separator+StringHelper.createQueryString(model);
	}
	
	public static List<NameValuePair> createNameValuePairs(Map<String,Object> params)
	{
		Form form=Form.form();//.add("username",  "vip").add("password",  "secret").build()
		for (Entry<String, Object> entry : params.entrySet())
		{
			form.add(entry.getKey(), entry.getValue().toString());
		}
		return form.build();
	}
	
	public static List<NameValuePair> createNameValuePairs(Multimap<String, Object> params)
	{
		Form form=Form.form();//.add("username",  "vip").add("password",  "secret").build()
		for (String key : params.keySet())
		{
			for (Object value : params.get(key))
			{
				form.add(key, value.toString());
			}
		}
		return form.build();
	}
		
	public static class CHttpRequest
	{
		protected Integer statusCode;
		
		public String getRequest(String url)
		{
			return getRequest(url,new HashMap<String,Object>());
		}

		public String getRequest(String url, Map<String,Object> model)
		{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try
			{
				HttpGet httpGet = new HttpGet(url);// model
				CloseableHttpResponse response = httpclient.execute(httpGet);
				try
				{
					System.out.println(response.getStatusLine());
					HttpEntity entity1 = response.getEntity();
					EntityUtils.consume(entity1);
				}
				finally
				{
					response.close();
				}
			}
			catch (Exception e)
			{
				throw new CException(e);
			}
			finally
			{
				try
				{
					httpclient.close();
				}
				catch (Exception e)
				{
					
				}
			}
			return "response";
		}
		
		public Integer getStatusCode(){return this.statusCode;}
		public void setStatusCode(Integer statusCode){this.statusCode=statusCode;}
	}

	/////////////////////////////////////////////////////////////////////////////
	
	public static String simplePostRequest(String url)
	{
		return simplePostRequest(url,new HashMap<String,Object>());
	}
	
	public static String simplePostRequest(String path, Map<String,Object> model)
	{
		//System.out.println("posting request: path="+path);
		try
		{
			URL url=new URL(path);
			URLConnection con=url.openConnection();
			
			con.setDoOutput(true);
			OutputStream out=con.getOutputStream();
			OutputStream bout=new BufferedOutputStream(out);
			OutputStreamWriter writer=new OutputStreamWriter(bout);//,"8859_1");
			boolean first=true;
			for (String name : model.keySet())
			{
				String value=(String)model.get(name);
				//System.out.println("name="+name+", value="+value);
				if (!first)
				{
					writer.write("&");
					first=false;
				}
				writer.write(name+"="+value);
			}
			
			writer.flush();
			writer.close();
			
			InputStream stream=new BufferedInputStream(con.getInputStream());
			Reader reader=new BufferedReader(new InputStreamReader(stream));
			
			StringBuilder buffer=new StringBuilder();
			//int c;
			//while((c=reader.read())!=-1)
			for (int c=reader.read(); c!=-1; c=reader.read())
			{
				buffer.append((char)c);
			}
			return buffer.toString();
		}
		catch(MalformedURLException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public static void getFtpFiles(String server, String folder, List<String> filenames, String destination)
	{
		for (String filename : filenames)
		{
			String url=server+folder+filename;
			String outfile=destination+filename;
			getFtpFile(url,outfile);
		}
	}
	
	public static void getFtpFile(String url, String outfile)
	{
		BufferedInputStream in=null;
		BufferedOutputStream out=null;
		try
		{
			in = new BufferedInputStream(new URL(url).openStream());
			out = new BufferedOutputStream(new FileOutputStream(outfile),1024);
			byte[] data = new byte[1024];
			int x=0;
			while((x=in.read(data,0,1024))>=0)
			{
				out.write(data,0,x);
			}
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
		finally
		{
			try
			{
				if (out!=null) out.close();
				if (in!=null) in.close();
			}
			catch (Exception e){}
		}
	}
}
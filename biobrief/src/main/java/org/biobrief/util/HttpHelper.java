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
	
	public static String getRequest(String url, Map<String,Object> model)
	{
		try
		{
			url=appendQueryString(url, model);
			System.out.println("url="+url);
			return Request.Get(url).execute().returnContent().asString();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String getRequest(String url, Object... args)
	{
		Map<String,Object> params=StringHelper.createMap(args);
		return getRequest(url, params);
	}
	
	public static String postRequest(String url, Map<String,Object> params)
	{
		try
		{
			return Request.Post(url)
					.bodyForm(createNameValuePairs(params))
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
	
	private static List<NameValuePair> createNameValuePairs(Map<String,Object> params)
	{
		Form form=Form.form();//.add("username",  "vip").add("password",  "secret").build()
		for (Entry<String, Object> entry : params.entrySet())
		{
			form.add(entry.getKey(), entry.getValue().toString());
		}
		return form.build();
	}
	
	/*
	public static String getRequest(String url, Map<String,Object> model)
	{
		CHttpRequest request=new CHttpRequest();
		return request.getRequest(url, model);
	}
	
	public static String getRequest(String url, Object... args)
	{
		Map<String,Object> params=StringHelper.createMap(args);
		return getRequest(url, params);
	}
	
	public static String postRequest(String url, Map<String,Object> params)
	{
		CPostHttpRequest request=new CPostHttpRequest();
		return request.getRequest(url,params);
	}
	
	public static String postRequest(String url, Object... args)
	{
		Map<String,Object> params=StringHelper.createMap(args);
		CPostHttpRequest request=new CPostHttpRequest();
		return request.getRequest(url,params);
	}
	*/
	
//	public static BufferedImage postToImageGenerator(String url, Map<String,Object> model)
//	{
//		CPostHttpRequest request=new CPostHttpRequest();
//		return request.postToImageGenerator(url,model);
//	}	
//	
//	public static String postRedirectRequest(String url, Map<String,Object> model)
//	{
//		CPostHttpRequest request=new CPostHttpRequest();
//		return request.getRequest(url,model);
//	}
	
//	public static String postMultipartRequest(String url, Map<String,Object> model)
//	{
//		Map<String,Object> files=Collections.emptyMap();//new HashMap<String,Object>();
//		return postMultipartRequest(url,model,files);
//	}
//	
//	public static String postMultipartRequest(String url, Map<String,Object> model, Map<String,Object> files)
//	{
//		PostMethod method=null;
//		try
//		{
//			HttpClient client = new HttpClient();
//			method = new PostMethod(url);        
//			Part[] parts=new Part[model.size()+files.size()];
//			int index=0;
//			for (String name : model.keySet())
//			{
//				String value=(String)model.get(name);
//				parts[index]=new StringPart(name,value);
//				index++;
//			}
//			for (String name : files.keySet())
//			{
//				String filename=(String)files.get(name);
//				File file=new File(filename);
//				parts[index]=new FilePart(name,file);
//				index++;
//			}
//
//			method.setRequestEntity(new MultipartRequestEntity(parts,method.getParams()));
//			method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//
//			// Execute the method.
//			int statusCode = client.executeMethod(method);
//			if (statusCode == HttpStatus.SC_OK)
//			{
//				return CHttpRequest.getResponseBody(method);
//			}
//			else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
//			{
//				Header header=method.getResponseHeader("Location");
//				return header.getValue();
//			}
//			else
//			{
//				System.err.println("Method failed: " + method.getStatusLine());
//				return method.getStatusLine().toString();
//			}
//		}
//		catch (HttpException e)
//		{
//			System.err.println("Fatal protocol violation: " + e.getMessage());
//			e.printStackTrace();
//			throw new CException(e);
//		}
//		catch (IOException e)
//		{
//			System.err.println("Fatal transport error: " + e.getMessage());
//			e.printStackTrace();
//			throw new CException(e);
//		}
//		finally
//		{
//			// Release the connection.
//			method.releaseConnection();
//		}
//	}
		
	public static class CHttpRequest
	{
		protected Integer statusCode;
		//protected String redirect;
		
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
		
//		public String getRequest(String url, Map<String,Object> model)
//		{
//			HttpClient client = new HttpClient();
//			HttpMethod method = createMethod(url,model);
//	        method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//			try
//			{
//				// Execute the method.
//				statusCode = client.executeMethod(method);
//				if (this.statusCode==HttpStatus.SC_OK)
//					return onOk(method);
//				else if (this.statusCode==HttpStatus.SC_MOVED_TEMPORARILY)
//					return onRedirect(method);
//				else return onOther(method);
//			}
//			catch (HttpException e)
//			{
//				throw new CException(e);
//			}
//			catch (IOException e)
//			{
//				throw new CException(e);
//			}
//			finally
//			{
//				method.releaseConnection();
//			}
//		}
		
		public Integer getStatusCode(){return this.statusCode;}
		public void setStatusCode(Integer statusCode){this.statusCode=statusCode;}

//		protected HttpMethod createMethod(String url, Map<String,Object> model)
//		{
//			// if there's already a query string, just add an &
//			char separator=(url.indexOf('?')==-1) ? '?' : '&';
//			url=url+separator+CWebHelper.createQueryString(model);
//			return new GetMethod(url);
//		}

//		protected String onOk(HttpMethod method)
//		{
//			return getResponseBody(method);
//		}
//		
//		protected String onRedirect(HttpMethod method)
//		{
//			System.err.println("OnRedirect: " + method.getStatusLine());
//			Header header=method.getResponseHeader("location");
//			if (header==null)
//				throw new CException("the response is invalid and did not provide the new location for the resource");
//			return header.getValue();
//		}
//		
//		protected String onOther(HttpMethod method)
//		{
//			String response=getResponseBody(method);
//			System.err.println("Method failed: " + method.getStatusLine()+", "+this.statusCode+", response="+response);
//			return response;
//		}
//		
//		protected static String getResponseBody(HttpMethod method)
//		{
//			try
//			{
//				InputStream input = method.getResponseBodyAsStream();
//				return IOUtils.toString(input, "UTF-8");
//			}
//			catch(IOException e)
//			{
//				throw new CException(e);
//			}
//		}
	}
	
//	public static class CPostHttpRequest extends CHttpRequest
//	{
//		@Override
//		protected HttpMethod createMethod(String url, Map<String,Object> model)
//		{
//			PostMethod method=new PostMethod(url);
//			NameValuePair[] data = getNameValuePairs(model);
//			method.setRequestBody(data);
//			return method;
//		}
//		
//		private static NameValuePair[] getNameValuePairs(Map<String,Object> model)
//		{
//			if (model==null)
//				return new NameValuePair[0];
//			NameValuePair[] data = new NameValuePair[model.size()];
//			int index=0;
//			for (String name : model.keySet())
//			{
//				//String value=(String)model.get(name);
//				Object value=model.get(name);
//				data[index]=new BasicNameValuePair(name,value.toString());
//				index++;
//			}
//			return data;
//		}
//		
////		public BufferedImage postToImageGenerator(String url, Map<String,Object> model)
////		{
////			//Image img = Toolkit.getDefaultToolkit().createImage(resp.getData());			
////			HttpClient client = new HttpClient();
////			HttpMethod method = createMethod(url,model);
////	        //method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
////			try
////			{
////				// Execute the method.
////				statusCode = client.executeMethod(method);
////				//String response=getResponseBody(method);
////				if (statusCode==HttpStatus.SC_OK)
////				{
////					//byte[] data=method.getResponseBody();
////					InputStream stream=method.getResponseBodyAsStream();
////					return ImageIO.read(stream);
////				}
////				else throw new CException("returned statusCode: "+this.statusCode);
////			}
////			catch (HttpException e)
////			{
////				throw new CException(e);
////			}
////			catch (IOException e)
////			{
////				throw new CException(e);
////			}
////			finally
////			{
////				method.releaseConnection();
////			}
////		}
//	}
//	
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
	
	/////////////////////////////////////////////////////////////////
	
//	public static String getOriginalFilename(HttpServletRequest request, String name)
//	{		
//		if (!(request instanceof MultipartHttpServletRequest))
//			throw new CException("request is not an instance of MultipartHttpServletRequest");
//	
//		MultipartHttpServletRequest multipart=(MultipartHttpServletRequest)request;
//		CommonsMultipartFile file=(CommonsMultipartFile)multipart.getFileMap().get(name);
//		return file.getOriginalFilename();
//	}
//	
//	public static String stripFiletype(String filename)
//	{
//		int index=filename.lastIndexOf('.');
//		return filename.substring(0,index);
//	}
}
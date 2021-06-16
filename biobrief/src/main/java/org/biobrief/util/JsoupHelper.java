package org.biobrief.util;

import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.Data;

//https://jsoup.org/
//http://scraping.pro/scraping-html-graphic-elements-limits-possibilities/
//https://stackoverflow.com/questions/23251156/download-svg-images-from-website-with-java?rq=1
public class JsoupHelper
{
	public static Document parse(String html)
	{
		return Jsoup.parse(html);
	}
	
	public static Document parseFile(String filename)
	{
		String html=FileHelper.readFile(filename);
		return parse(html);
	}
	
	public static Document parseUrl(String url)
	{
		try
		{
			//return Jsoup.connect(url).get();
			Connection conn=Jsoup.connect(url)
					.userAgent("Mozilla/5.0")
					.timeout(10 * 1000)
					.followRedirects(true);
			return conn.get();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}

	//https://stackoverflow.com/questions/23320498/how-to-post-form-login-using-jsoup
	//https://www.javacodeexamples.com/jsoup-post-form-data-example/822
	public static Document post(String url, List<NameValuePair> values)
	{
		try
		{
			Connection conn=Jsoup.connect(url)
					.userAgent("Mozilla/5.0")
					.timeout(10 * 1000)
					.method(Method.POST)
					.followRedirects(true);
			for (NameValuePair pair : values)
			{
				conn.data(pair.getName(), pair.getValue());
			}					
			Response response=conn.execute();
			Document document = response.parse();
			return document;
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String createKey(String url)
	{
		return url;
	}
	
	public static String createKey(String url, List<NameValuePair> params)
	{
		return url+":"+StringHelper.toString(params);
	}
	
	@Data
	public static class NameValuePair
	{
		protected String name;
		protected String value;
		
		public NameValuePair() {}
		
		public NameValuePair(String name, Object value)
		{
			this.name=name;
			this.value=StringHelper.dflt(value);
			if (name==null)
				throw new CException("NameValuePair: name cannot be null");
			if (value==null)
				throw new CException("NameValuePair: value cannot be null (name="+name+")");
		}
	}
}
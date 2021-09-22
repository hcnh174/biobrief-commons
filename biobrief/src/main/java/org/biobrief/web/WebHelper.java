package org.biobrief.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.biobrief.util.CException;
import org.biobrief.util.ContentType;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public final class WebHelper
{	
	private static final Logger logger=LoggerFactory.getLogger(WebHelper.class);
	
	public static final String JSESSIONID="JSESSIONID";
	public static final String SUCCESS="success";
	public static final String MESSAGE="message";
	public static final String WEBAPP_ATTRIBUTE="webapp";
	public static final String DOCTYPE_ATTRIBUTE="doctype";
	public static final String SETTINGS_ATTRIBUTE="settings";
	public static final String UTILS_ATTRIBUTE="utils";
	public static final String BASEDIR_ATTRIBUTE="baseDir";
	public static final String FOLDER_ATTRIBUTE="folder";
	public static final String FOLDERS_ATTRIBUTE="folders";
	public static final String USER_ATTRIBUTE="user";
	public static final String ERRORS_ATTRIBUTE="errors";
	public static final String LOGITEM_ATTRIBUTE="logitem";
	public static final String JSON_ATTRIBUTE="json";
	public static final String IMAGE_ATTRIBUTE="image";
	public static final String NOLOG_ATTRIBUTE="nolog";
	public static final String CONFIGURATION_ATTRIBUTE="configuration";
	public static final String CONTENT_DISPOSITION_HEADER="Content-Disposition";
	
	private WebHelper(){}
	
	public static String getServerName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException e)
		{
			StringHelper.println(e.getMessage());	
		}
		return null;
	}
	
	public static MessageWriter getWriter(HttpServletResponse response)
	{
		try
		{
			return new MessageWriter(response.getWriter());
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String joinParams(Map<String,Object> params, boolean escape)
	{
		return StringHelper.joinParams(params, escape);
	}
	
//	public static String joinParams(Map<String,Object> params, boolean escape)
//	{
//		StringBuilder buffer=new StringBuilder();
//		String separator="";
//		for (String name : params.keySet())
//		{
//			buffer.append(separator);
//			buffer.append(name+"="+params.get(name).toString());
//			if (escape)
//				separator="&amp;";
//			else separator="&";
//		}
//		return buffer.toString();
//	}
	
	public static String getUrl(HttpServletRequest request)
	{
		StringBuilder buffer=new StringBuilder("");
		if (request.getRequestURL()!=null)
		{
			buffer.append(request.getRequestURI());
			String qs=request.getQueryString();
			if (StringHelper.hasContent(qs))
				buffer.append("?"+qs);
		}
		return buffer.toString();
	}
	
	public static String getHref(String url)
	{
		int index=url.indexOf('?');
		if (index==-1)
			return url;
		else return url.substring(0,index);
	}
	
	public static String getQueryString(HttpServletRequest request)
	{
		String qs=request.getQueryString();
		try
		{
			qs=URLDecoder.decode(qs,Charsets.UTF_8.toString());//"UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new CException(e);
		}
		return qs;
	}
	
	public static List<String> getHeaders(HttpServletRequest request)
	{
		List<String> headers=new ArrayList<String>();
		for (Enumeration<?> e=request.getHeaderNames();e.hasMoreElements();)
		{
			String name=(String)e.nextElement();
			String value=(String)request.getHeader(name);
			headers.add("header "+name+"="+value);
		}
		return headers;
	}
	
	public static Map<String,String> getParameters(HttpServletRequest request)
	{
		Map<String,String> params=new LinkedHashMap<String,String>();
		for (Enumeration<String> enumeration=request.getParameterNames(); enumeration.hasMoreElements();)
		{
			String name=(String)enumeration.nextElement();
			String value=(String)request.getParameter(name);
			params.put(name,value);
		}
		return params;
	}
	
	public static Map<String,String> getParameters(HttpServletRequest request, int maxlength)
	{
		Map<String,String> params=new LinkedHashMap<String,String>();
		for (Enumeration<String> enumeration=request.getParameterNames();enumeration.hasMoreElements();)
		{
			String name=(String)enumeration.nextElement();
			String value=(String)request.getParameter(name);
			if (value.length()>maxlength)
				value=StringHelper.truncate(value,maxlength);
			params.put(name,value);
		}
		return params;
	}
	
	public static String formatParams(Map<String,String> params)
	{
		StringBuilder buffer=new StringBuilder();
		for (Map.Entry<String,String> entry : params.entrySet())
		{
			buffer.append(entry.getKey()+"="+entry.getValue()+"\n");
		}
		return buffer.toString();
	}

	
	public static String getOriginalFilename(HttpServletRequest request, String name)
	{		
		if (!(request instanceof MultipartHttpServletRequest))
			throw new CException("request is not an instance of MultipartHttpServletRequest");
	
		MultipartHttpServletRequest multipart=(MultipartHttpServletRequest)request;
		CommonsMultipartFile file=(CommonsMultipartFile)multipart.getFileMap().get(name);
		return file.getOriginalFilename();
	}
	
	public static boolean isLinkChecker(HttpServletRequest request)
	{
		String user_agent=request.getHeader("User-Agent");
		if (user_agent.indexOf("Xenu")!=-1)
			return true;
		return false;
	}
	
	// returns the context path
	public static String getWebapp(HttpServletRequest request)
	{
		String webapp=request.getContextPath();
		//logger.debug("webapp="+webapp);
		return webapp;
	}
	
	public static Cookie setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path)
	{
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(0);
		cookie.setPath(path); //You need to add this!!!!!
		response.addCookie(cookie);
		displayCookie(cookie);
		return cookie;
	}
	
	public static String getCookie(HttpServletRequest request, String name)
	{
		Cookie[] cookies=request.getCookies();
		if (cookies==null)
			return null;
		for (int i=0;i<cookies.length;i++)
		{
			Cookie cookie=cookies[i];
			displayCookie(cookie);
			if (cookie.getName().equals(name))
				return cookie.getValue();
		}
		return null;
	}
	
	public static void displayCookie(Cookie cookie)
	{
		logger.debug("COOKIE----------------------------------------");
		logger.debug("cookie: "+cookie.getName());
		logger.debug("value: "+cookie.getValue());
		logger.debug("domain: "+cookie.getDomain());
		logger.debug("path: "+cookie.getPath());
	}
	
	public static void removeCookie(HttpServletResponse response, String name)
	{
		Cookie cookie=new Cookie(name,null);
		response.addCookie(cookie);
	}
	
	public static void removeCookies(HttpServletRequest request, HttpServletResponse response)
	{
		Cookie[] cookies=request.getCookies();
		if (cookies==null)
			return;
		for (int i=0;i<cookies.length;i++)
		{
			Cookie oldcookie=cookies[i];
			String name=oldcookie.getName();
			if (JSESSIONID.equals(name))
				continue;
			Cookie cookie=new Cookie(name,null);
			response.addCookie(cookie);
		}
	}
	
	public static Map<String,String> getCookies(HttpServletRequest request)
	{
		Map<String,String> map=new LinkedHashMap<String,String>();
		Cookie[] cookies=request.getCookies();
		if (cookies==null)
			return map;
		for (int i=0;i<cookies.length;i++)
		{
			Cookie cookie=cookies[i];
			map.put(cookie.getName(),cookie.getValue());
		}
		return map;
	}
	
	/////////////////////////////////////////////////////
	
	public static String createQueryString(Map<String,Object> params)
	{
		return StringHelper.createQueryString(params);
	}
	
	public static String getUserAgent(HttpServletRequest request)
	{
		return request.getHeader("User-Agent");
	}
	
	public static boolean isIE(HttpServletRequest request)
	{
		return isIE(getUserAgent(request));
	}
	
	public static boolean isIE(String browser)
	{
		return (browser.toLowerCase().indexOf("msie")!=-1);
	}
	
	public static String getReferer(HttpServletRequest request)
	{
		return request.getHeader("referer");
	}
	
	public static String getIpaddress(HttpServletRequest request)
	{
		return request.getRemoteAddr();
	}
	
	public static String getSessionid(HttpServletRequest request)
	{
		String sessionid="";
		HttpSession session=request.getSession();
		if (session!=null)
			sessionid=session.getId();
		return sessionid;
	}
	
	/////////////////////////////////////////////////////////////////////
	
	public static OutputStream getOutputStream(HttpServletResponse response)
	{
		try
		{
			return response.getOutputStream();
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////

	
	public static Map<String,Object> json(Object... args)
	{
		return StringHelper.createMap(args);
	}

	protected static Map<String,Object> jsonSuccess()
	{
		return json(SUCCESS,true,MESSAGE,"success");
	}
	
	public static Map<String,Object> jsonSuccess(Object...args)
	{
		Map<String,Object> map=StringHelper.createMap(args);
		map.put(SUCCESS,true);
		return json(map);
	}
	
	public static Map<String,Object> jsonSuccessMessage(String message)
	{
		return jsonSuccess(MESSAGE,message);
	}
	
	public static Map<String,Object> jsonFailure(Object...args)
	{
		Map<String,Object> map=StringHelper.createMap(args);
		map.put(SUCCESS,false);
		return json(map);
	}
	
	public static Map<String,Object> jsonFailureMessage(String message)
	{
		return jsonFailure(MESSAGE,message);
	}
	
	public static Map<String,Object> jsonUploadSuccess(Object...args)
	{
		Map<String,Object> map=jsonSuccess(args);
		if (!map.containsKey(MESSAGE))
			map.put(MESSAGE,"success");
		return json(map);
	}
	
	///////////////////////////////////////////////////////
	
	public static PrintWriter getHtmlWriter(HttpServletResponse response)
	{
		try
		{	
			response.setContentType("text/html;charset=UTF-8");
			return response.getWriter();
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static PrintWriter getTextWriter(HttpServletResponse response)
	{
		try
		{	
			response.setContentType("text/plain;charset=UTF-8");
			return response.getWriter();
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String writeText(HttpServletResponse response, String str)
	{
		PrintWriter writer=getTextWriter(response);
		writer.print(str);
		writer.flush();
		return null;
	}
	
	public static String write(HttpServletResponse response, String str)
	{
		try
		{	
			PrintWriter writer=response.getWriter();
			writer.print(str);
			writer.flush();
			return null;
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String writeHtml(HttpServletResponse response, String html)
	{
		PrintWriter writer=getHtmlWriter(response);
		writer.print(html);
		writer.flush();
		return null;
	}
	
	public static String write(HttpServletResponse response, byte[] data)
	{
		try
		{
			ServletOutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			return null;
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String write(HttpServletResponse response, String str, boolean preserve)
	{
		str="<html><body><pre>"+str+"</pre></body></html>";
		response.setContentType(ContentType.HTML.getMimeType());
		return write(response,str);
	}
	
	public static String javascript(HttpServletRequest request, HttpServletResponse response, String js)
	{
		return javascript(request,response,js,false);
	}	
	
	public static String javascript(HttpServletRequest request, HttpServletResponse response, String js, boolean debug)
	{
		//if (debug)
		//	js=JsBeautifier.jsBeautify(js);
		byte[] data=js.getBytes(Charsets.UTF_8);
		String ifNoneMatch = request.getHeader("If-None-Match");
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + "\"";
		if (etag.equals(ifNoneMatch))
		{
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return null;
		}
		response.setContentType(ContentType.JAVASCRIPT.getMimeType());
		//response.setCharacterEncoding(Charsets.UTF_8.displayName());//RouterController.UTF8_CHARSET.name());
		response.setContentLength(data.length);
		response.setHeader("ETag", etag);
		return write(response,data);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/*
	@SuppressWarnings("unchecked")
	public static List<Map<String,String>> parseJsonRecords(String str)
	{
		try
		{
			List<Map<String,String>> records=new ArrayList<Map<String,String>>();
			JSONObject json=new JSONObject(str);
			JSONArray arr=json.getJSONArray("recordsToInsertUpdate");
			for (int index=0;index<arr.length();index++)
			{
				JSONObject obj=arr.getJSONObject(index);
				Map<String,String> record=new LinkedHashMap<String,String>();
				for (Iterator<Object> iter=obj.keys(); iter.hasNext();)
				{
					String key=iter.next().toString();
					String value=obj.getString(key);
					record.put(key,value);
				}
				records.add(record);
			}
			return records;
		}
		catch(JSONException e)
		{
			throw new CException(e);
		}
	}
	*/
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public static void setTextFileDownload(HttpServletResponse response, String filename)
	{
		response.setContentType(ContentType.TXT.getMimeType());
		response.setHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
	}
	
	public static void setSpreadsheetDownload(HttpServletResponse response, String filename)
	{
		//response.setContentType(CWebHelper.ContentType.XLS);
		response.setHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=\""+filename+"\"");
	}
	
	//////////////////////////////////////////////////////////////////
	
	public static String[] parseUrlParams(HttpServletRequest request, String regex)
	{
		String url=request.getServletPath();
		regex=regex.replaceAll("\\*","([-a-zA-Z0-9.]*)");
		logger.debug("regex="+regex);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		boolean result = matcher.find();
		if (!result)
			throw new CException("url does not match pattern: "+url+"["+regex+"]");
		String[] list=new String[matcher.groupCount()];
		for (int index=0;index<list.length;index++)
		{
			list[index]=matcher.group(index+1);
		}
		return list;
	}
	
	/////////////////////////////////////////////////////////////
	
	public static String redirect(HttpServletRequest request, HttpServletResponse response, String url)
	{
		try
		{	
			if (url.substring(0,1).equals("/"))
				url=getWebapp(request)+url;
			Writer writer=response.getWriter();
			StringBuilder buffer=new StringBuilder();
			buffer.append("<html>\n");
			buffer.append("<head>\n");
			buffer.append("<meta http-equiv=\"refresh\" CONTENT=0;URL="+url+">\n");
			buffer.append("</head>\n");
			buffer.append("</html>\n");
			writer.write(buffer.toString());
			writer.flush();
			return null;
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static String parseIdentifier(HttpServletRequest request)
	{
		String url=request.getServletPath();
		int start=url.lastIndexOf('/')+1;
		int end=url.lastIndexOf('.');
		String identifier=url.substring(start,end);
		logger.debug("identifier="+identifier+" ("+url+")");
		return identifier;
	}
	
	public static String parseIdentifier(HttpServletRequest request, String regex)
	{
		String[] params=parseUrlParams(request,regex);
		return params[0];
	}
	
	///////////////////////////////////////////////////////////
	
	public static String getFolder(HttpServletRequest request)
	{
		List<String> folders=getFolders(request,BASEDIR_ATTRIBUTE,FOLDER_ATTRIBUTE);
		return (folders.isEmpty()) ? "" : folders.get(0);
	}
	
	public static List<String> getFolders(HttpServletRequest request)
	{
		return getFolders(request,BASEDIR_ATTRIBUTE,FOLDERS_ATTRIBUTE);
	}
	
	public static List<String> getFolders(HttpServletRequest request, String basedir_attribute, String folder_attribute)
	{
		try
		{
			String baseDir=ServletRequestUtils.getRequiredStringParameter(request,basedir_attribute).trim();
			String str=ServletRequestUtils.getRequiredStringParameter(request,folder_attribute).trim();
			List<String> folders=new ArrayList<String>();
			for (String line : StringHelper.splitLines(str))
			{
				if (line.indexOf('#')==0)
					continue;
				String path=baseDir+line;
				if (FileHelper.isFolder(path))
					folders.add(path);
			}
			return folders;
		}
		catch(ServletException e)
		{
			throw new CException(e);
		}
	}
	
	public static List<String> getFilenames(HttpServletRequest request)
	{
		return getFilenames(request,BASEDIR_ATTRIBUTE,FOLDERS_ATTRIBUTE);
	}
	
	public static List<String> getFilenames(HttpServletRequest request, String basedir_attribute, String attribute)
	{
		try
		{
			String baseDir=ServletRequestUtils.getRequiredStringParameter(request,basedir_attribute).trim();
			String str=ServletRequestUtils.getRequiredStringParameter(request,attribute).trim();
			List<String> filenames=new ArrayList<String>();
			//for (String line : StringHelper.split(str,"\n"))
			for (String line : StringHelper.splitLines(str))
			{
				line=line.trim();
				//if (StringHelper.isEmpty(line))
				//	continue;
				if (line.indexOf('#')==0)
					continue;
				String path=baseDir+line;
				if (!FileHelper.isFolder(path))
					filenames.add(path);
			}
			return filenames;
		}
		catch(ServletException e)
		{
			throw new CException(e);
		}
	}
	
	public static boolean isJson(HttpServletRequest request)
	{
		return (request.getRequestURI().indexOf(".json")!=-1);
	}
	
	private static final String RSS_USER_AGENT="Mozilla 5.0 (Windows; U; "
        + "Windows NT 5.1; en-US; rv:1.8.0.11) ";
	
	public static String readRss(String feed, int num)
	{
		InputStream stream=null;
		try
		{
			feed=appendParam(feed,"num",""+num);
			logger.debug("feed="+feed);
			
			URL url = new URL(feed);
			URLConnection connection = url.openConnection();
			// default Java user agent is blocked by Google reader, so change to something else
			connection.setRequestProperty("User-Agent",RSS_USER_AGENT);
			stream=connection.getInputStream();
			return FileHelper.readInputStream(stream);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
		finally
		{
			FileHelper.closeStream(stream);
		}
	}
	
	public static String readInputStream(HttpServletRequest request)//ServletInputStream stream)
	{
		try
		{
			return FileHelper.readInputStream(request.getInputStream());
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	// adds a param=value to a query string, adding a ? or & as appropriate
	public static String appendParam(String url, String name, String value)
	{
		if (url.indexOf("?")==-1)
			url+="?";
		else url+="&";
		return url+name+"="+value;
	}
	
	public static final String SEARCH_ENGINES="xenu,googlebot,msnbot,webalta crawler,yahoo! slurp,baiduspider,yeti";
	
	public boolean isSearchEngine(String user_agent)
	{
		return isSearchEngine(user_agent,StringHelper.split(SEARCH_ENGINES,","));
	}
	
	public static boolean isSearchEngine(String user_agent, Collection<String> searchEngines)
	{
		if (!StringHelper.hasContent(user_agent)) // if null, it is probably test framework
			return false;
		for (String spider : searchEngines)
		{
			if (user_agent.indexOf(spider)!=-1)
				return true;
		}
		return false;
	}
	
	public static void addIfNotNull(Model model, String name, Object obj)
	{
		if (obj!=null)
			model.addAttribute(name,obj);
	}
	
	/*
	public static Model addError(Model model, String error)
	{
		CErrors errors;
		if (model.containsAttribute(ERRORS_ATTRIBUTE))
			errors=(CErrors)model.asMap().get(ERRORS_ATTRIBUTE);
		else
		{
			errors=new CErrors();
			addErrors(model,errors);
		}
		errors.addError(error);
		return model;
	}
	
	public static Model addErrors(Model model, CErrors errors)
	{
		model.addAttribute(ERRORS_ATTRIBUTE,errors);
		return model;
	}

	public static String scrubHtml(String html)
	{
		CRichTextFilter filter=new CRichTextFilter();
		return filter.filter(html);
	}
	*/
	
	public static List<LabelValue> getLabelValues(List<? extends Object> items)
	{
		List<LabelValue> values=Lists.newArrayList();
		for (Object item : items)
		{
			values.add(new LabelValue(item));
		}
		return values;
	}

	public static class LabelValue
	{
		protected String label;
		protected String value;
		
		public LabelValue(String value, Object label)
		{
			this.value=value;
			this.label=label.toString();
		}
		
		public LabelValue(Object value)
		{
			this(value.toString(),value);
		}
		
		public String getLabel(){return label;}
		public String getValue(){return value;}
	}
	
	public static String getDocType(HttpServletRequest request)
	{
		if (isIE(request))
			return "";
		else return DocType.TRANSITIONAL;
	}
	
	public static class DocType
	{
		public static final String TRANSITIONAL="<!DOCTYPE html PUBLIC\n"
			+ "\t\"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
			+ "\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
	}
	
	////////////////////////////////////////////////
	
//	public static Pageable getPageable(HttpServletRequest request)
//	{
//		System.out.println("received request: "+request.toString());
//		int page=Integer.parseInt(getRequiredParameter(request, "page"));
//		int limit=Integer.parseInt(getRequiredParameter(request, "size"));
//		//if (!StringHelper.hasContent(request.getParameter("sort")))
//		if (!hasParameter(request, "sort"))
//			return PageRequest.of(page, limit);
//		else return PageRequest.of(page, limit, getSort(request));
//	}
//	
////	public static Pageable override(Pageable paging, int size)
////	{
////		return PageRequest.of(0,  size, paging.getSort());
////	}
//	
//	// uses supplied values if page parameter is not present
//	public static Pageable getPageable(HttpServletRequest request, int page, int limit)
//	{
//		if (hasParameter(request, "page"))
//			return getPageable(request);
//		else return PageRequest.of(page, limit);
//	}
	
	public static String getRequiredParameter(HttpServletRequest request, String name)
	{
		String value=request.getParameter(name);
		if (!StringHelper.hasContent(value))
			throw new CException("expected parameter: "+name+": found: ["+value+"]");
		return value;
	}
	
	public static boolean hasParameter(HttpServletRequest request, String name)
	{
		return StringHelper.hasContent(request.getParameter(name));
	}
	
//	private static Sort getSort(HttpServletRequest request)
//	{
//		List<Sort.Order> sorters=Lists.newArrayList();
//		List<String> parts=StringHelper.split(request.getParameter("sort"), ",");
//		String field=parts.get(0);
//		Sort.Direction dir=parts.get(1).equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//		sorters.add(new Sort.Order(dir, field));
//		Sort sort=Sort.by(sorters);
//		//logger.debug("sorts: "+sort.toString());
//		return sort;
//	}
	
	/////////////////////////////////////////////////
	
	public static String getHostName()
	{
		try
		{
			return java.net.InetAddress.getLocalHost().getHostName();
		}
		catch (java.net.UnknownHostException e)
		{
			System.out.println(e);
			return "localhost";
		}
	}
	
	public static InputStream getInputStream(MultipartFile file)
	{
		try
		{
			return file.getInputStream();
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	////////////////////////////////////////////////////
	
//	public static String writeFile(String dir, MultipartFile file)
//	{
//		if (file.isEmpty())
//			throw new CException("Uploaded multipart file was empty");
//		BufferedOutputStream stream=null;
//		try
//		{
//			String filename=dir+"/"+file.getOriginalFilename();
//			stream=new BufferedOutputStream(new FileOutputStream(new File(filename)));
//			FileCopyUtils.copy(file.getInputStream(), stream);
//			return filename;
//		}
//		catch (Exception e)
//		{
//			throw new CException("You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
//		}
//		finally
//		{
//			FileHelper.closeStream(stream);
//		}
//	}
	
	//https://github.com/spring-guides/gs-uploading-files/blob/main/complete/src/main/java/com/example/uploadingfiles/storage/FileSystemStorageService.java
	public static String writeFile(String dir, MultipartFile file)
	{
		if (file.isEmpty())
			throw new CException("Uploaded multipart file was empty");
		String filename=dir+"/"+file.getOriginalFilename();
		if (FileHelper.exists(filename))
			throw new CException("file already exists: "+filename);
		try (InputStream inputStream = file.getInputStream())
		{
			Files.copy(inputStream, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
			return filename;
		}
		catch (IOException e)
		{
			throw new CException("Failed to store file: "+filename, e);
		}
	}
	
	public static List<String> writeFiles(String dir, List<MultipartFile> files)
	{
		List<String> filenames=Lists.newArrayList();
		for (MultipartFile file : files)
		{
			//out.println("uploading file: "+file.getOriginalFilename());
			filenames.add(WebHelper.writeFile(dir, file));
		}
		return filenames;
	}
	
	/////////////////////////////////////////////////

	//https://stackoverflow.com/questions/35680932/download-a-file-from-spring-boot-rest-service
	//https://stackoverflow.com/questions/16601428/how-to-set-content-disposition-and-filename-when-using-filesystemresource-to
	public static ResponseEntity<Resource> download(String filename) throws Exception
	{
		File file=new File(filename);
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		
		ContentDisposition contentDisposition = ContentDisposition.builder("inline")
			.filename(FileHelper.stripPath(filename))
			.build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		
		return ResponseEntity.ok()
			.headers(headers)
			.contentLength(file.length())
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(resource);
	}
}

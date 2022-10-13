package org.biobrief.web;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.biobrief.util.Constants;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractController
{
	@Autowired protected WebProperties properties;
	
	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN);
		dateFormat.setLenient(true);
		binder.registerCustomEditor(LocalDate.class, new CustomDateEditor(dateFormat, true));
	}
	
	protected MessageWriter getWriter(HttpServletResponse response)
	{
		return WebHelper.getWriter(response);
	}
	
	protected Map<String, Object> success(String message)
	{
		return message(true, message);
	}
	
	protected Map<String, Object> success()
	{
		return success("success");
	}
	
	protected Map<String, Object> message(boolean success, String message)
	{
		return StringHelper.createMap("success", success, "message", message);
	}
	
	protected List<String> getPostIds(HttpServletRequest request)
	{
		return StringHelper.split(StringHelper.trim(WebHelper.readInputStream(request)));
	}
	
	protected List<String> uploadFiles(List<MultipartFile> files)
	{
		String dir=createUploadDir();
		return WebHelper.writeFiles(dir, files);
	}
	
	protected String uploadFile(MultipartFile file)
	{
		String dir=createUploadDir();
		return WebHelper.writeFile(dir, file);
	}
	
	private String createUploadDir()
	{
		String dir=properties.getUploadDir()+"/"+StringHelper.generateShortUUID();
		FileHelper.createDirectory(dir);
		return dir; 
	}
}
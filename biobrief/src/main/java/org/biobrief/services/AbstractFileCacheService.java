package org.biobrief.services;

import java.util.Date;

import org.biobrief.util.DateHelper;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestHelper;
import org.biobrief.util.RestHelper.Headers;

import lombok.Data;

@Data
public abstract class AbstractFileCacheService
{
	protected final String cacheDir;
	protected final Long sleeptime;//millis
	protected final Integer maxAge;//days private TimeUnit units=TimeUnit.DAYS;
	protected final String suffix;
	
	public AbstractFileCacheService(String cacheDir, Long sleeptime, Integer maxAge, String suffix)
	{
		this.cacheDir=cacheDir;
		this.sleeptime=sleeptime;
		this.maxAge=maxAge;
		this.suffix=suffix;
	}

	///////////////////////////////////////
	
//	public String get(String key, String url, MessageWriter out)
//	{
//		if (containsKey(key, out))
//			return getValue(key, out);
//		//String json=RestHelper.get(restTemplate, url, out);
//		String value=get(url, out);
//		setValue(key, value, out);
//		sleep(out);
//		return value;
//	}
//	
//	public String get(String key, String url, Headers headers, MessageWriter out)
//	{
//		if (containsKey(key, out))
//			return getValue(key, out);
//		//String json=RestHelper.get(restTemplate, url, headers, out);
//		String value=get(url, headers, out);
//		setValue(key, value, out);
//		sleep(out);
//		return value;
//	}
//	
//	public <P> String post(String key, String url, P params, MessageWriter out)
//	{
//		if (containsKey(key, out))
//			return getValue(key, out);
//		//String json=RestHelper.post(restTemplate, url, params, out);
//		String value=post(url, params, out);
//		setValue(key, value, out);
//		sleep(out);
//		return value;
//	}
//	
//	///////////////////////////////////////////////////////
//	
//	protected abstract String get(String url, MessageWriter out);
//	
//	protected abstract String get(String url, Headers headers, MessageWriter out);
//	
//	protected abstract <P> String post(String url, P params, MessageWriter out);
//

	///////////////////////////////////////
	
	protected boolean containsKey(String key, MessageWriter out)
	{
		String filename=getFilename(key);
		boolean found=FileHelper.exists(filename);
		if (!found)
			return false;
		Date expirationDate=getExpirationDate();
		Date lastModified=FileHelper.getLastModifiedDate(filename);
		System.out.println("TRACE: filename found: filename="+filename+" date="+lastModified+" expires="+expirationDate);
		if (DateHelper.isAfter(lastModified, expirationDate))
			return true;
		out.println("filename found but out of date: filename="+filename+" date="+lastModified+" expires="+expirationDate);
		return false;
	}
	
	protected String getValue(String key, MessageWriter out)
	{
		String filename=getFilename(key);
		out.println("reading filename: "+filename);
		return FileHelper.readFile(filename);
	}
	
	protected void setValue(String key, String value, MessageWriter out)
	{
		String filename=getFilename(key);
		out.println("writing filename: "+filename);
		FileHelper.writeFile(filename, format(value));
	}
	
	protected String format(String value)
	{
		return value;
	}
	
	protected String getFilename(String key)
	{
		return cacheDir+"/"+key+suffix;
	}
	
	protected Date getExpirationDate()
	{
		return DateHelper.addDays(new Date(), maxAge*-1);
	}
	
	protected void sleep(MessageWriter out)
	{
		RestHelper.sleep(sleeptime, out);
	}
}

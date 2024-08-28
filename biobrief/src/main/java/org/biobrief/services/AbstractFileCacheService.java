package org.biobrief.services;

import java.util.Date;

import org.biobrief.util.DateHelper;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RestHelper;

import lombok.Data;

@Data
public abstract class AbstractFileCacheService
{
	protected final String cacheDir;
	protected final Long sleeptime;//millis
	protected final Integer maxAge;//days private TimeUnit units=TimeUnit.DAYS;
	protected final String suffix;
	protected Date lastRequest=null;//simplify by giving a default value
	
	public AbstractFileCacheService(String cacheDir, Long sleeptime, Integer maxAge, String suffix)
	{
		this.cacheDir=cacheDir;
		this.sleeptime=sleeptime;
		this.maxAge=maxAge;
		this.suffix=suffix;
	}

	///////////////////////////////////////
	
//	public boolean containsKey(String key, MessageWriter out)
//	{
//		Date expirationDate=getExpirationDate();
//		return containsKey(key, expirationDate, out);
//	}

	public boolean containsKey(String key, MessageWriter out)
	{
		String filename=getFilename(key);
		boolean found=FileHelper.exists(filename);
		if (!found)
			return false;
		Date expirationDate=getExpirationDate();
		Date lastModified=FileHelper.getLastModifiedDate(filename);
		//System.out.println("TRACE: filename found: filename="+filename+" date="+lastModified+" expires="+expirationDate);
		if (DateHelper.isAfter(lastModified, expirationDate))
			return true;
		out.println("filename found but out of date: filename="+filename+" date="+lastModified+" expires="+expirationDate);
		return false;
	}
	
	protected String getValue(String key, MessageWriter out)
	{
		String filename=getFilename(key);
		//out.println("reading filename: "+filename);
		return FileHelper.readFile(filename);
	}
	
	protected void setValue(String key, String value, MessageWriter out)
	{
		String filename=getFilename(key);
		//out.println("writing filename: "+filename);
		FileHelper.writeFile(filename, format(value));
		this.lastRequest=new Date();
	}
	
	public Date getLastModifiedDate(String key)
	{
		String filename=getFilename(key);
		return FileHelper.getLastModifiedDate(filename);
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
		if (lastRequest==null)
			return;
		Date nextRequest=new Date(lastRequest.getTime()+sleeptime);
		Date now=new Date();
		long waittime=nextRequest.getTime()-now.getTime();
		if (waittime>sleeptime)
			waittime=sleeptime;
		if (waittime<0)
			waittime=0;
		out.println("lastRequest="+lastRequest+" sleeptime="+sleeptime+" waittime="+waittime);
		RestHelper.sleep(waittime, out);
	}
}

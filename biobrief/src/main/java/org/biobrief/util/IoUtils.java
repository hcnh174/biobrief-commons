package org.biobrief.util;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoUtils
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(IoUtils.class);
	
	public static String cleanFile(String filename, Charset in, Charset out, boolean fixQuotes)
	{
		String tempDir=FileHelper.getTempDirectory();
		return cleanFile(filename, tempDir, in, out, fixQuotes);
	}
	
	public static String cleanFile(String filename, String tempDir, Charset in, Charset out, boolean fixQuotes)
	{
		String newfilename=tempDir+"/"+FileHelper.stripPath(FileHelper.stripFiletype(filename))+"-tmp.txt";
		String str=FileHelper.readFile(filename, in);
		//str=StringHelper.replace(str, "■","");
		if (fixQuotes)
		{
			str=StringHelper.replace(str, "\r","");
			str=replaceNewlinesInQuotes(str,"|");
		}		
		FileHelper.writeFile(newfilename, str, false, out);
		return newfilename;
	}

	public static String replaceNewlinesInQuotes(String str, String replace)
	{
		StringBuilder buffer=new StringBuilder();
		int start=str.indexOf("\"");
		int oldend=0;
		while (start!=-1)
		{
			//start=start+1;
			int end=str.indexOf("\"",start+1);
			//log.debug("start="+start+", end="+end);
			String substr=str.substring(start+1,end);
			//log.debug("[1]"+substr);
			substr=replaceNewlines(substr,replace);
			buffer.append(str.substring(oldend,start));
			buffer.append(substr);
			oldend=end+1;
			start=str.indexOf("\"",oldend);
		}
		buffer.append(str.substring(oldend));
		return buffer.toString();
	}
	
	public static String replaceNewlines(String str, String replace)
	{
		if (str.indexOf("\n")!=-1)
			str=str.trim().replaceAll("[\\r\\n]",replace);
		if (str.indexOf("\t")!=-1)
			str=str.trim().replaceAll("\t"," ");
		return str;
	}
}
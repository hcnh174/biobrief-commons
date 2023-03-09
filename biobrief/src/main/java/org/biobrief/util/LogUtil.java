package org.biobrief.util;

import java.time.LocalDateTime;

import org.biobrief.util.PlatformType.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.mail.SimpleMailMessage;

import com.google.common.collect.Lists;

public class LogUtil
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(LogUtil.class);
	
	public static final String TAB="\t";
	public static String TIMESTAMP=LocalDateHelper.getTimestamp();
		
	public static void log(String message, Exception e)
	{
		//log(appendErrorMessage(message, e));
		System.err.println(message);
		String logfile="errors.txt";
		appendFile(logfile, formatMessage(message, e));
	}
	
	public static void log(String message)
	{
		//log.info(message);
		System.err.println(message);
		String logfile="log.txt";
		appendFile(logfile, formatMessage(message));
	}
	
	public static void debug(String message)
	{
		System.err.println(message);
		String debugfile="debug.txt";
		appendFile(debugfile, formatMessage(message));
	}
	
	public static void logMessage(String logfile, String message, Exception e)
	{
		//logMessage(logfile, appendErrorMessage(message, e));
		String errorfile=StringHelper.replace(logfile, "log-", "errors-");
		appendFile(errorfile, formatMessage(message, e));
	}
	
	public static void logMessage(String logfile, String message)
	{
		appendFile(logfile, getDate()+message);
	}
	
	public static void logDateError(String value)
	{
		String filename="date-errors.txt";
		appendFile(filename, value);
	}
	
	///////////////////////////////////////////////
	
	public static void appendFile(String filename, String str)
	{
		FileHelper.appendFile(getLogDir()+"/"+filename, str);
	}
	
	public static String getBaseLogDir()
	{
		String dflt=FileHelper.getBaseDirectory()+"/.temp/logs";
		String dir=RuntimeHelper.getEnvironmentVariable("LOG_DIR", dflt);
		return dir;
	}
	
	public static String getLogDir()
	{
		return getBaseLogDir()+"/"+TIMESTAMP;
	}
	
	public static String getPrivateLogDir()
	{
		if (PlatformType.find().isUnix())
			return "~/.log";
		else return FileHelper.getBaseDirectory()+"/.temp/logs";
	}
	
	public static void updateTimestamp()
	{
		TIMESTAMP=LocalDateHelper.getTimestamp();
	}
	
	private static String getDate()
	{
		return "["+LocalDateHelper.format(LocalDateTime.now(), LocalDateHelper.DATETIME_PATTERN)+"] ";
	}
	
	public static String join(Object... args)
	{
		return StringHelper.join(Lists.newArrayList(args), TAB);
	}
	
	public static String appendErrorMessage(String message, Exception e)
	{
		return message+": "+e.getMessage()+"\n"+StringHelper.getStackTrace(e);
	}
	
	public static String formatMessage(String message, Exception e)
	{
		return formatMessage(appendErrorMessage(message, e));
	}
	
	public static String formatMessage(String message)
	{
		return getDate()+message;
	}
}
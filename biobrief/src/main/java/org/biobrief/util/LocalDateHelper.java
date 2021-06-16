package org.biobrief.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.biobrief.util.DateHelper.Era;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

//https://javarevisited.blogspot.com/2015/03/20-examples-of-date-and-time-api-from-Java8.html
//https://stackoverflow.com/questions/35043788/migrate-from-joda-time-library-to-java-time-java-8
public final class LocalDateHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(LocalDateHelper.class);
	public static final String MMDDYYYY_PATTERN="M'/'d'/'yyyy";//"MM'/'dd'/'yyyy";
	public static final String YYYYMD_PATTERN="yyyy'/'M'/'d";//"yyyy'/'MM'/'dd"
	public static final String YYYYMMDD_PATTERN="yyyy'/'MM'/'dd";//"yyyy'/'MM'/'dd"
	public static final String POSTGRES_YYYYMMDD_PATTERN="yyyy'-'M'-'d";//"yyyy'-'MM'-'dd";
	public static final String EXCEL_PATTERN="EEE MMM dd hh:mm:ss zzz yyyy";//Thu Oct 07 00:00:00 JST 1982
	public static final String DATE_PATTERN=YYYYMD_PATTERN;
	public static final String DATETIME_PATTERN=YYYYMD_PATTERN+"' 'HH':'mm':'ss";
	public static final String TIME_PATTERN="HH':'mm' 'a";
	public static final String HOUR_MINUTE_SECOND_PATTERN="HH':'mm':'ss";
	public static final String TIMESTAMP_PATTERN="yyyy'-'MM'-'dd_HH'-'mm";//"yyyy'-'MM'-'dd_HH'-'mm";
	
	private LocalDateHelper(){}
		
	public static String format(LocalDate date, String pattern)
	{
		if (date==null)
			return "";
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static String format(LocalDateTime date, String pattern)
	{
		if (date==null)
			return "";
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static String format(LocalTime time, String pattern)
	{
		if (time==null)
			return "";
		return time.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static String format(LocalDate date)
	{
		return format(date, DATETIME_PATTERN);
	}
	
	//https://opencast.jira.com/svn/MH/trunk/modules/matterhorn-common/src/main/java/org/opencastproject/util/SolrUtils.java
	//<code>yyyy-MM-dd'T'HH:mm:ss'Z'</code>
	public static String formatUtcDate(LocalDate date)
	{
		if (date==null)
			return null;
		ZonedDateTime utc=ZonedDateTime.of(date, LocalTime.MIDNIGHT, ZoneOffset.UTC);
		return utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	
	public static LocalDate parseDate(String value)
	{
		return parseDate(value, DATETIME_PATTERN);
	}
	
	public static LocalDate parseDate(String value, String pattern)
	{
		return parseDate(value, pattern, true);
	}
		
	public static LocalDate parseDate(String value, List<String> patterns, boolean throwException)
	{
		for (String pattern : patterns)
		{
			LocalDate date=parseDate(value, pattern, false, false);
			if (date!=null)
				return date;
		}
		String message="problem parsing date ["+value+"] with any of these patterns ["+StringHelper.join(patterns,"], [")+"]";
		if (throwException)
			throw new CException(message);
		//else System.err.println(message);
		return null;
	}
	
	public static LocalDate parseDate(String value, String pattern, boolean throwException)
	{
		return parseDate(value, pattern, throwException, true);
	}
	
	public static LocalDate parseDate(String value, String pattern, boolean throwException, boolean normalize)
	{
		try
		{
			if (value==null)
				return null;
			if (normalize)
				value=normalizeDelimiters(value,pattern);
			return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern(pattern));
		}
		catch (DateTimeParseException e)
		{
			if (throwException)
				throw new CException("problem parsing date ["+value+"] with pattern ["+pattern+"]",e);
			//if (StringHelper.hasContent(value))
			//	System.err.println("problem parsing date ["+value+"] with pattern ["+pattern+"]");
			return null;
		}
	}
	
	/////////////////////////////////////////////////////////////
	
	public static LocalDateTime parseDateTime(String value, String pattern)
	{
		return parseDateTime(value, pattern, true);
	}
	
	public static LocalDateTime parseDateTime(String value, String pattern, boolean throwException)
	{
		try
		{
			if (value==null)
				return null;
			//if (normalize)
			//	value=normalizeDelimiters(value,pattern);
			return LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern(pattern));
		}
		catch (DateTimeParseException e)
		{
			if (throwException)
				throw new CException("problem parsing date ["+value+"] with pattern ["+pattern+"]",e);
			return null;
		}
	}
	
	///////////////////////////////////////////////
	
	public static LocalTime parseTime(String value, String pattern)
	{
		return parseTime(value, pattern, true);
	}
	
	public static LocalTime parseTime(String value, String pattern, boolean throwException)
	{
		try
		{
			if (value==null)
				return null;
			return LocalTime.parse(value.trim(), DateTimeFormatter.ofPattern(pattern));
		}
		catch (DateTimeParseException e)
		{
			if (throwException)
				throw new CException("problem parsing time ["+value+"] with pattern ["+pattern+"]",e);
			return null;
		}
	}
	
	///////////////////////////////////////////////
	
//	public static LocalDate parseExcelDate(double value)
//	{
//		//logger.debug("trying to parse excel date: "+value+" --> "+LocalDateUtil.getJavaLocalDate(value));
//		return convert(DateUtil.getJavaDate(value));
//	}
	
	private static String normalizeDelimiters(String value, String pattern)
	{
		if (value.indexOf("/")!=-1 && pattern.indexOf("-")!=-1)
			value=StringHelper.replace(value, "/","-");
		else if (value.indexOf("-")!=-1 && pattern.indexOf("/")!=-1)
			value=StringHelper.replace(value, "-","/");
		return value;
	}

	public static boolean isDate(String value, String pattern)
	{
		try
		{
			LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern(pattern));
			return true;
		}
		catch (DateTimeParseException e)
		{
			return false;
		}
	}
	
//	public static LocalDate setDate(String strdate)
//	{
//		return parseDate(strdate, DATE_PATTERN);
//	}
//
//	public static LocalDateTime setDateTime(String strdate, String strtime)
//	{
//		String value=strdate+"T"+strtime;
//		String pattern=DATE_PATTERN+"'T'"+TIME_PATTERN;
//		return parseDateTime(value, pattern);
//	}
	
//	public static LocalDate setDate(int year, int month, int date)
//	{
//		
//		Calendar cal=new GregorianCalendar();
//		cal.set(Calendar.YEAR,year);
//		cal.set(Calendar.MONTH,month);
//		cal.set(Calendar.DATE,date);
//		return setToMidnight(cal.getTime());
//	}
	
//	public static LocalDate setTime(LocalDate date, String strtime)
//	{
//		LocalDate time=parse(strtime,TIME_PATTERN);
//		return setTime(date,time);
//	}
//	
//	public static LocalDate setTime(String strtime)
//	{
//		LocalDate date=new LocalDate();
//		return setTime(date,strtime);
//	}
	
//	public static LocalDateTime setTime(LocalDate datepart, LocalDate timepart)
//	{
//		Calendar date=new GregorianCalendar();
//		Calendar time=new GregorianCalendar();
//		date.setTime(datepart);
//		time.setTime(timepart);
//		date.set(Calendar.HOUR_OF_DAY,time.get(Calendar.HOUR_OF_DAY));
//		date.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
//		date.set(Calendar.SECOND,time.get(Calendar.SECOND));
//		date.set(Calendar.MILLISECOND,0);
//		return date.getTime();
//	}
//	
//	public static LocalDate addYears(LocalDate date, int years)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.YEAR, years);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate addDays(LocalDate date, int days)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE,days);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate addMinutes(LocalDate date, int minutes)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.MINUTE,minutes);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate addHours(LocalDate date, int hours)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.HOUR,hours);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate addWeeks(LocalDate date, int weeks)
//	{ 
//		return addDays(date,weeks*7);
//	}
//	
//	public static LocalDate addMonths(LocalDate date, int months)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.MONTH,months);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate setToMidnight(LocalDate date)
//	{
//		return setToHour(date,0);
//	}
//	
//	public static LocalDate setToNearestHour(LocalDate date)
//	{
//		Calendar cal=new GregorianCalendar();
//		cal.setTime(date);
//		return setToHour(date,cal.get(Calendar.HOUR_OF_DAY));
//	}
//	
//	public static LocalDate setToHour(LocalDate date, int hour)
//	{
//		if (date==null)
//			throw new CException("date is null: setToHour "+hour);
//		Calendar cal=new GregorianCalendar();
//		cal.setTime(date);		
//		Calendar calendar=new GregorianCalendar();
//		calendar.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE));
//		calendar.set(Calendar.HOUR_OF_DAY,hour);
//		calendar.set(Calendar.MINUTE,0);
//		calendar.set(Calendar.SECOND,0);
//		calendar.set(Calendar.MILLISECOND,0);
//		return calendar.getTime();
//	}
//	
	//http://stackoverflow.com/questions/29201103/how-to-compare-localdate-instances-java-8
	public static boolean datesMatch(LocalDate date1, LocalDate date2)
	{
		if (date1==null || date2==null)
			return false;
		return date1.isEqual(date2);
		//return (setToMidnight(date1).compareTo(setToMidnight(date2))==0);
	}
//	
//	public static boolean before(LocalDate date1, LocalDate date2)
//	{
//		return (setToMidnight(date2).compareTo(setToMidnight(date1))<0);
//	}
//	
//	public static boolean after(LocalDate date1, LocalDate date2)
//	{
//		return (setToMidnight(date2).compareTo(setToMidnight(date1))>0);
//	}
//	
	public static boolean onOrBefore(LocalDate date1, LocalDate date2)
	{
		return date1.isEqual(date2) || date1.isBefore(date2);
				//(setToMidnight(date2).compareTo(setToMidnight(date1))<=0);
	}
	
	public static boolean onOrAfter(LocalDate date1, LocalDate date2)
	{
		return date1.isEqual(date2) || date1.isAfter(date2);
		//return (setToMidnight(date2).compareTo(setToMidnight(date1))>=0);
	}
//	
	public static boolean between(LocalDate min, LocalDate max, LocalDate date)
	{
		return onOrAfter(min,date) && onOrBefore(max,date);
	}

	public static boolean future(LocalDate date)
	{
		return date.isAfter(LocalDate.now());
	}
//	
//	public static LocalDate stripLocalDate(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.set(Calendar.YEAR,1900);
//		calendar.set(Calendar.MONTH,0);
//		calendar.set(Calendar.DATE,1);
//		return calendar.getTime();
//	}
//	
//	public static LocalDate getWeekStartLocalDate(LocalDate date, int startday)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		int curday=calendar.get(Calendar.DAY_OF_WEEK);
//		int days=curday-startday;
//		days=-1*days;
//		return addDays(date,days);
//	}
//	
//	public static LocalDate getFirstDayInMonth(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.set(Calendar.DATE,1);
//		return calendar.getTime();
//	}
//	
//	public static int getYear(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		return calendar.get(Calendar.YEAR);
//	}
//	
//	public static int getMonth(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		return calendar.get(Calendar.MONTH)+1;// hack -- 0-based
//	}
//	
//	public static int getLocalDate(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		return calendar.get(Calendar.DAY_OF_MONTH);
//	}
//	
//	public static int getDay(LocalDate date)
//	{
//		Calendar calendar=new GregorianCalendar();
//		calendar.setTime(date);
//		return calendar.get(Calendar.DAY_OF_WEEK);
//	}
//	
//	// returns duration in minutes
//	public static Integer getDuration(LocalDate date1, LocalDate date2)
//	{
//		if (date1==null || date2==null)
//		{
//			//System.err.println("can't determine duration because of null date. date1="+date1+", date2="+date2);
//			return null;
//		}
//		long diff=date2.getTime()-date1.getTime();
//		return (int)(diff/(60*1000));
//	}
	
	public static Integer getDurationInYears(LocalDate date1, LocalDate date2)
	{
		if (date1==null || date2==null)
		{
			System.err.println("can't determine duration because of null date. date1="+date1+", date2="+date2);
			return null;
		}
		Period period=Period.between(date1,  date2);
		return Math.abs(period.getYears());
	}
	
	//https://docs.oracle.com/javase/8/docs/api/java/time/temporal/ChronoUnit.html
	public static Integer getDurationInWeeks(LocalDate date1, LocalDate date2)
	{
		if (date1==null || date2==null)
			return null;
		Period period=Period.between(date1, date2);
		return MathHelper.parseInt((float)period.getDays()/7.0);
	}
	
	public static Integer getDurationInDays(LocalDate date1, LocalDate date2)
	{
		Period period=Period.between(date1, date2);
		return period.getDays();
	}
//	
//	////////////////////////////////////////////////////
//	
	public static boolean matchYear(LocalDate date1, LocalDate date2)
	{
		return date1.getYear()==date2.getYear();
	}
	
	public static boolean matchYearMonth(LocalDate date1, LocalDate date2)
	{
		if (!matchYear(date1, date2))
			return false;
		return date1.getMonthValue()==date2.getMonthValue();
	}
//	
//	public static boolean matchYearMonthDay(LocalDate date1, LocalDate date2)
//	{
//		if (!matchYearMonth(date1, date2))
//			return false;
//		return LocalLocalDateHelper.getDay(date1)==LocalLocalDateHelper.getDay(date2);
//	}
//	
//	////////////////////////////////////////////////////

	public static String getTimestamp()
	{
		return getTimestamp(LocalDateTime.now());
	}

	public static String getTimestamp(LocalDateTime date)
	{
		return format(date, TIMESTAMP_PATTERN);
	}

//	public static LocalDate parseTimestamp(String str)
//	{
//		return LocalLocalDateHelper.parse(str,TIMESTAMP_PATTERN);
//	}
//	
	public static List<LocalDate> findDatesInRange(Collection<LocalDate> dates, LocalDate min, LocalDate max)
	{
		List<LocalDate> list=Lists.newArrayList();
		for (LocalDate date : dates)
		{
			if (between(min, max, date))
				list.add(date);
		}
		//logger.debug("dates between min="+min+" and max="+max+"\n"+list);
		return list;
	}
	
	public static Integer parseImperialYear(String value)//H21年
	{
		if (!StringHelper.hasContent(value))
			return null;
		value=StringHelper.replace(value,"年","");
		if (value.charAt(0)=='S' || value.charAt(0)=='s')
			return Era.SHOWA.convert(Integer.parseInt(value.substring(1)));
		else if (value.charAt(0)=='H' || value.charAt(0)=='h')
			return Era.HEISEI.convert(Integer.parseInt(value.substring(1)));
		else if (MathHelper.isInteger(value) && Integer.parseInt(value)<100)// hack - arbitrary
			return Era.HEISEI.convert(Integer.parseInt(value));
		return null;
		//throw new CException("cannot parse Imperial year: "+value);
	}
	
	//http://docs.oracle.com/javase/7/docs/technotes/guides/intl/calendar.doc.html
	//http://stackoverflow.com/questions/31511912/how-can-i-convert-japanese-date-to-western-date-in-java
	public static LocalDate parseJapaneseDate(String value)//平成26年03月26日
	{
		try
		{
			DateFormat df=DateFormat.getDateInstance(DateFormat.FULL, new Locale("ja", "JP", "JP"));
			Date date=df.parse(value);
			return asLocalDate(date);
		}
		catch (ParseException e)
		{
			throw new CException("cannot parse Japanese date: "+value);
		}
	}
	
	//http://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate
//	public static LocalDate convert(Date date)
//	{
//		if (date==null)
//			return null;
//		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//	}
//	
//	public static LocalDateTime convertTime(Date date)
//	{
//		if (date==null)
//			return null;
//		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//	}
//	
//	//http://stackoverflow.com/questions/22929237/convert-java-time-localdate-into-java-util-date-type
//	public static Date convert(LocalDate date)
//	{
//		if (date==null)
//			return null;
//		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	}
//	
//	public static Date convert(LocalDateTime date)
//	{
//		if (date==null)
//			return null;
//		return Date.from(date.toInstant(ZoneId.systemDefault()));
//	}
//	
	
	//https://stackoverflow.com/questions/22929237/convert-java-time-localdate-into-java-util-date-type
	public static Date asDate(LocalDate localDate)
	{
		if (localDate==null)
			return null;
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime)
	{
		if (localDateTime==null)
			return null;
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date)
	{
		if (date==null)
			return null;
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date)
	{
		if (date==null)
			return null;
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
//	// if startdate is null, sets start to midnight on current date and enddate to one day from then
//	public static class LocalDateRange
//	{
//		protected LocalDate startdate;
//		protected LocalDate enddate;
//		
//		public LocalDate getStartdate(){return this.startdate;}
//		public LocalDate getEnddate(){return this.enddate;}
//		
//		public LocalDateRange(LocalDate startdate, LocalDate enddate)
//		{
//			this.startdate=startdate;
//			this.enddate=enddate;
//		}
//		
//		public LocalDateRange(String strstartdate, String strenddate)
//		{
//			this(strstartdate,strenddate,DATE_PATTERN);
//		}
//		 
//		public LocalDateRange(String strstartdate, String strenddate, String pattern)
//		{
//			if (strstartdate==null)
//			{
//				this.startdate=setToMidnight(new LocalDate());
//				this.enddate=addDays(this.startdate,1);
//			}
//			else
//			{
//				this.startdate=parse(strstartdate,pattern);
//				this.enddate=parse(strenddate,pattern);
//			}
//		}
//	}
//	
//	public enum Era
//	{
//		SHOWA(1926),
//		HEISEI(1989);
//		
//		private int start;
//		
//		Era(int start)
//		{
//			this.start=start;
//		}
//		
//		public int convert(int offset)
//		{
//			return start+offset-1;
//		}
//	}
}
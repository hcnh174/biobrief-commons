package org.biobrief.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.poi.ss.usermodel.DateUtil;

import com.google.common.collect.Lists;

//https://stackoverflow.com/questions/35043788/migrate-from-joda-time-library-to-java-time-java-8
public final class DateHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(DateHelper.class);
	public static final String MMDDYYYY_PATTERN="MM'/'dd'/'yyyy";
	public static final String YYYYMMDD_PATTERN="yyyy'/'MM'/'dd";
	public static final String POSTGRES_YYYYMMDD_PATTERN="yyyy'-'MM'-'dd";
	public static final String JAPANESE_YYYYMMDD_PATTERN="yyyy'年'MM'月'dd'日'";
	public static final String EXCEL_PATTERN="EEE MMM dd hh:mm:ss zzz yyyy";//Thu Oct 07 00:00:00 JST 1982
	public static final String DATE_PATTERN=YYYYMMDD_PATTERN;
	public static final String DATETIME_PATTERN=YYYYMMDD_PATTERN+"' 'HH':'mm':'ss";
	public static final String TIME_PATTERN="HH':'mm' 'a";
	public static final String TIMESTAMP_PATTERN="yyyy'-'MM'-'dd_HH'-'mm";
	public static final String ISO_PATTERN="yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final Locale LOCALE=Locale.US;
	
	private DateHelper(){}
	
	public static String format(String pattern)
	{
		return format(new Date(),pattern);
	}
	
	// Formatting
	public static String format(Date date, String pattern)
	{
		if (date==null)
			return "";
		DateFormat formatter=createDateFormat(pattern);
		return formatter.format(date);
	}
	
	public static String format(Date date)
	{
		return format(date, DATETIME_PATTERN);
	}
	
	public static DateFormat createDateFormat(String pattern)
	{
		return new SimpleDateFormat(pattern, LOCALE);
	}
	
	//https://opencast.jira.com/svn/MH/trunk/modules/matterhorn-common/src/main/java/org/opencastproject/util/SolrUtils.java
	/**
	 * Return a date format suitable for solr. Format a date as UTC with a granularity of seconds.
	 * <code>yyyy-MM-dd'T'HH:mm:ss'Z'</code>
	 */
	public static String formatUtcDate(Date date)
	{
		if (date==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		return f.format(date);
	}
	
	public static Date parse(String value)
	{
		return parse(value, DATETIME_PATTERN);
	}
	
	public static Date parseDate(String value, String pattern)
	{
		return parse(value, pattern);
	}
	
	public static Date parse(String value, String pattern)
	{
		return parse(value, pattern, true);
	}
		
	public static Date parse(String value, List<String> patterns, boolean throwException)
	{
		for (String pattern : patterns)
		{
			Date date=parse(value, pattern, false, false);
			if (date!=null)
				return date;
		}
		String message="problem parsing date ["+value+"] with any of these patterns ["+StringHelper.join(patterns,"], [")+"]";
		if (throwException)
			throw new CException(message);
		//else System.err.println(message);
		return null;
	}
	
	public static Date parse(String value, String pattern, boolean throwException)
	{
		return parse(value, pattern, throwException, true);
	}
	
	public static Date parse(String value, String pattern, boolean throwException, boolean normalize)
	{
		try
		{
			if (!StringHelper.hasContent(value))
				return null;
			if (normalize)
				value=normalizeDelimiters(value,pattern);
			SimpleDateFormat formatter=new SimpleDateFormat(pattern,LOCALE);
			return formatter.parse(value.trim());
		}
		catch (ParseException e)
		{
			if (throwException)
				throw new CException("problem parsing date ["+value+"] with pattern ["+pattern+"]",e);
			//if (StringHelper.hasContent(value))
			//	System.err.println("problem parsing date ["+value+"] with pattern ["+pattern+"]");
			return null;
		}
	}
	
//	public static Date parseExcelDate(double value)
//	{
//		//logger.debug("trying to parse excel date: "+value+" --> "+DateUtil.getJavaDate(value));
//		return DateUtil.getJavaDate(value);
//	}
	
	private static String normalizeDelimiters(String value, String pattern)
	{
		if (value.indexOf("/")!=-1 && pattern.indexOf("-")!=-1)
			value=StringHelper.replace(value, "/","-");
		else if (value.indexOf("-")!=-1 && pattern.indexOf("/")!=-1)
			value=StringHelper.replace(value, "-","/");
		return value;
	}

//	private static final String YEAR_REGEX="([0-9]{4})?";
//	private static final String YEAR_MONTH_REGEX="([0-9]{4})/([0-9]{1,2})?";
//	private static final String FLEXIBLE_DATE_REGEX="([0-9]{4})[^0-9]([0-9]+)[^0-9]([0-9]+)";
	
//	public static String cleanDate(String origvalue)
//	{
//		String value=origvalue;
//		if ("??".equals(value) || "????".equals(value))
//			return null;
//		
//		// remove extraneous characters
//		if (value.indexOf('?')!=-1)
//			value=value.replace("?","");
//		if (value.indexOf("//")!=-1)
//			value=value.replace("//","/"); // 2005//6/13
//				
//		// try to determine pattern
//		if (value.matches(YEAR_REGEX))
//			return value.substring(0,4)+"/1/1";
//		else if (value.matches(YEAR_MONTH_REGEX)) //1994/10?
//		{	
//			Pattern pat=Pattern.compile(YEAR_MONTH_REGEX);
//			Matcher matcher=pat.matcher(value);
//			matcher.find();
//			String year=String.valueOf(Integer.parseInt(matcher.group(1)));
//			String month=String.valueOf(Integer.parseInt(matcher.group(2)));
//			return year+"/"+month+"/1";
//		}
//		else if (value.matches(FLEXIBLE_DATE_REGEX))
//		{
//			Pattern pat=Pattern.compile(FLEXIBLE_DATE_REGEX);
//			Matcher matcher=pat.matcher(value);
//			matcher.find();
//			String year=String.valueOf(Integer.parseInt(matcher.group(1)));
//			String month=String.valueOf(Integer.parseInt(matcher.group(2)));
//			String date=String.valueOf(Integer.parseInt(matcher.group(3)));
//			return year+"/"+month+"/"+date;
//		}
//		return value;
//	}
	
	public static boolean isDate(String value, String pattern)
	{
		try
		{
			SimpleDateFormat formatter=new SimpleDateFormat(pattern,LOCALE);
			formatter.parse(value);
			return true;
		}
		catch (ParseException e)
		{
			return false;
		}
	}
	
	public static Date setDate(String strdate)
	{
		return parse(strdate,DATE_PATTERN);
	}

	public static Date setDate(String strdate, String strtime)
	{
		return parse(strdate+"T"+strtime, DATE_PATTERN+"'T'"+TIME_PATTERN);
	}
	
	public static Date setDate(int year, int month, int date)
	{
		Calendar cal=new GregorianCalendar();
		cal.set(Calendar.YEAR,year);
		cal.set(Calendar.MONTH,month);
		cal.set(Calendar.DATE,date);
		return setToMidnight(cal.getTime());
	}
	
//	public static Date setTime(Date date, String strtime)
//	{
//		Date time=parse(strtime,TIME_PATTERN);
//		return setTime(date,time);
//	}
//	
//	public static Date setTime(String strtime)
//	{
//		Date date=new Date();
//		return setTime(date,strtime);
//	}
	
	public static Date setTime(Date datepart, Date timepart)
	{
		Calendar date=new GregorianCalendar();
		Calendar time=new GregorianCalendar();
		date.setTime(datepart);
		time.setTime(timepart);
		date.set(Calendar.HOUR_OF_DAY,time.get(Calendar.HOUR_OF_DAY));
		date.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
		date.set(Calendar.SECOND,time.get(Calendar.SECOND));
		date.set(Calendar.MILLISECOND,0);
		return date.getTime();
	}
	
	public static Date addYears(Date date, int years)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}
	
	public static Date addDays(Date date, int days)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,days);
		return calendar.getTime();
	}
	
	public static Date addMinutes(Date date, int minutes)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE,minutes);
		return calendar.getTime();
	}
	
	public static Date addHours(Date date, int hours)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR,hours);
		return calendar.getTime();
	}
	
	public static Date addWeeks(Date date, int weeks)
	{ 
		return addDays(date,weeks*7);
	}
	
	public static Date addMonths(Date date, int months)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,months);
		return calendar.getTime();
	}
	
	public static Date setToMidnight(Date date)
	{
		return setToHour(date, 0);
	}
	
	public static Date setToNearestHour(Date date)
	{
		Calendar cal=new GregorianCalendar();
		cal.setTime(date);
		return setToHour(date,cal.get(Calendar.HOUR_OF_DAY));
	}
	
	public static Date setToHour(Date date, int hour)
	{
		if (date==null)
			throw new CException("date is null: setToHour "+hour);
		Calendar cal=new GregorianCalendar();
		cal.setTime(date);		
		Calendar calendar=new GregorianCalendar();
		calendar.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE));
		calendar.set(Calendar.HOUR_OF_DAY,hour);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}
	
	public static boolean datesMatch(Date date1, Date date2)
	{
		if (date1==null || date2==null)
			return false;
		return (setToMidnight(date1).compareTo(setToMidnight(date2))==0);
	}
	
	// null safe - does not set to midnight - uses built-in after function
	public static boolean isAfter(Date date1, Date date2)
	{
		if (date1==null || date2==null)
			return false;
		return date1.after(date2);
	}
	
	public static boolean isBefore(Date date1, Date date2)
	{
		if (date1==null || date2==null)
			return false;
		return date1.before(date2);
	}
	
	@Deprecated
	public static boolean before(Date date1, Date date2)
	{
		return (setToMidnight(date2).compareTo(setToMidnight(date1))<0);
	}

	@Deprecated
	public static boolean after(Date date1, Date date2)
	{
		return (setToMidnight(date2).compareTo(setToMidnight(date1))>0);
	}
	
	public static boolean onOrBefore(Date date1, Date date2)
	{
		return (setToMidnight(date2).compareTo(setToMidnight(date1))<=0);
	}
	
	public static boolean onOrAfter(Date date1, Date date2)
	{
		return (setToMidnight(date2).compareTo(setToMidnight(date1))>=0);
	}
	
	public static boolean between(Date min, Date max, Date date)
	{
		return DateHelper.onOrAfter(min,date) && DateHelper.onOrBefore(max,date);
	}
	
	public static boolean future(Date date)
	{
		return after(new Date(), date);
	}
	
	public static Date stripDate(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.YEAR,1900);
		calendar.set(Calendar.MONTH,0);
		calendar.set(Calendar.DATE,1);
		return calendar.getTime();
	}
	
	public static Date getWeekStartDate(Date date, int startday)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		int curday=calendar.get(Calendar.DAY_OF_WEEK);
		int days=curday-startday;
		days=-1*days;
		return addDays(date,days);
	}
	
	public static Date getFirstDayInMonth(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		return calendar.getTime();
	}
	
	public static int getYear(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
	
	public static int getMonth(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH)+1;// hack -- 0-based
	}
	
	public static int getDate(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getDayOfWeek(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	// returns duration in minutes
	public static Integer getDuration(Date date1, Date date2)
	{
		if (date1==null || date2==null)
		{
			//System.err.println("can't determine duration because of null date. date1="+date1+", date2="+date2);
			return null;
		}
		long diff=date2.getTime()-date1.getTime();
		return (int)(diff/(60*1000));
	}
	
	public static Integer getDurationInYears(Date date1, Date date2)
	{
		if (date1==null || date2==null)
		{
			System.err.println("can't determine duration because of null date. date1="+date1+", date2="+date2);
			return null;
		}
		Calendar cal1=new GregorianCalendar();
		Calendar cal2=new GregorianCalendar();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int year1=cal1.get(Calendar.YEAR);
		int year2=cal2.get(Calendar.YEAR);
		return Math.abs(year2-year1);		
	}
	
	public static Integer getDurationInWeeks(Date date1, Date date2)
	{
		Integer minutes=getDuration(date1,date2);
		if (minutes==null)
			return null;
		return minutes/(60*24*7);
	}
	
	public static Integer getDurationInDays(Date date1, Date date2)
	{
		Integer minutes=getDuration(date1,date2);
		if (minutes==null)
			return null;
		return minutes/(60*24);
	}
	
	public static int getDaysInMonth(Date date)
	{
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		calendar.add(Calendar.MONTH,1);
		calendar.add(Calendar.DATE,-1);
		return calendar.get(Calendar.DATE);
	}
	
	////////////////////////////////////////////////////
	
	public static boolean matchYear(Date date1, Date date2)
	{
		return DateHelper.getYear(date1)==DateHelper.getYear(date2);
	}
	
	public static boolean matchYearMonth(Date date1, Date date2)
	{
		if (!matchYear(date1, date2))
			return false;
		return DateHelper.getMonth(date1)==DateHelper.getMonth(date2);
	}
	
	public static boolean matchYearMonthDay(Date date1, Date date2)
	{
		if (!matchYearMonth(date1, date2))
			return false;
		return DateHelper.getDate(date1)==DateHelper.getDate(date2);
	}
	
	////////////////////////////////////////////////////
	
	public static String getTimestamp()
	{
		return getTimestamp(new Date());
	}

	public static String getTimestamp(Date date)
	{
		return DateHelper.format(date, TIMESTAMP_PATTERN);
	}
	
	public static Date parseTimestamp(String str)
	{
		return DateHelper.parse(str, TIMESTAMP_PATTERN);
	}
	
	public static List<Date> findDatesInRange(Collection<Date> dates, Date min, Date max)
	{
		List<Date> list=Lists.newArrayList();
		for (Date date : dates)
		{
			if (DateHelper.between(min,max,date))
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
		if (value.charAt(0)=='S')
			return Era.SHOWA.convert(Integer.parseInt(value.substring(1)));
		else if (value.charAt(0)=='H')
			return Era.HEISEI.convert(Integer.parseInt(value.substring(1)));
		else if (MathHelper.isInteger(value) && Integer.parseInt(value)<100)// hack - arbitrary
			return Era.HEISEI.convert(Integer.parseInt(value));
		return null;
		//throw new CException("cannot parse Imperial year: "+value);
	}
	
	//http://docs.oracle.com/javase/7/docs/technotes/guides/intl/calendar.doc.html
	public static Date parseJapaneseDate(String value)//平成26年03月26日
	{
		//LocalDate date=LocalDate.parse(value, DateTimeFormatter);
		//return date.from
		try
		{
			DateFormat df=DateFormat.getDateInstance(DateFormat.FULL, new Locale("ja", "JP", "JP"));
			return df.parse(value);
		}
		catch (ParseException e)
		{
			throw new CException("cannot parse Japanese date: "+value);
		}
	}
	
	//////////////////////////////////////////////////////////////

	public static Date findClosestDate(Date target, Date min, Date max, Collection<Date> dates)
	{
		List<Date> list=DateHelper.findDatesInRange(dates, min, max);
		return findClosestDate(target, list);
	}
	
	public static Date findClosestDate(Date target, Collection<Date> dates)
	{
		Date best=null;
		int mindiff=10000;
		for (Date date : dates)
		{
			int diff=Math.abs(DateHelper.getDurationInDays(target, date));
			//log.debug("diff="+diff);
			if (diff<mindiff)
			{
				mindiff=diff;
				best=date;
				//log.debug("new best="+best);
			}
		}
		return best;
	}
	
	////////////////////////////////////////////////////////
	
	public static Date findDate(String value) //[(MP-7)2009/9/2??]
	{
		Pattern pattern = Pattern.compile("[1-2][0-9][0-9][0-9]/[0-9][0-9]?/[0-9][0-9]?");
		Matcher matcher = pattern.matcher(value);
		if (!matcher.find())
			return null;
		String strdate=matcher.group();
		return parseDate(strdate, YYYYMMDD_PATTERN);
	}
	
	//////////////////////////////////////////////////////
	
	// if startdate is null, sets start to midnight on current date and enddate to one day from then
	public static class DateRange
	{
		protected Date startdate;
		protected Date enddate;
		
		public Date getStartdate(){return this.startdate;}
		public Date getEnddate(){return this.enddate;}
		
		public DateRange(Date startdate, Date enddate)
		{
			this.startdate=startdate;
			this.enddate=enddate;
		}
		
		public DateRange(String strstartdate, String strenddate)
		{
			this(strstartdate,strenddate,DATE_PATTERN);
		}
		 
		public DateRange(String strstartdate, String strenddate, String pattern)
		{
			if (strstartdate==null)
			{
				this.startdate=setToMidnight(new Date());
				this.enddate=addDays(this.startdate,1);
			}
			else
			{
				this.startdate=parse(strstartdate,pattern);
				this.enddate=parse(strenddate,pattern);
			}
		}
	}
	
	public enum Era
	{
		SHOWA(1926),
		HEISEI(1989);
		
		private int start;
		
		Era(int start)
		{
			this.start=start;
		}
		
		public int convert(int offset)
		{
			return start+offset-1;
		}
	}
}
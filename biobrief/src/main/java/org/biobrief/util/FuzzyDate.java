package org.biobrief.util;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.biobrief.util.Constants;
import org.biobrief.util.Constants.DateMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.biobrief.util.DataFrame;
import org.biobrief.util.DateHelper;
import org.biobrief.util.LocalDateHelper;
import org.biobrief.util.StringHelper;
import org.biobrief.util.ExcelHelper;

public class FuzzyDate
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(FuzzyDate.class);
	private static final Set<String> unknowns=loadUnknowns();
	private static final Map<String,String> hacks=loadHacks();

	private static Set<String> loadUnknowns()
	{
		Set<String> set=Sets.newHashSet();
		DataFrame<String> dataframe=DataFrame.parseTabFile("./data/lookup/dates-unknown.txt");
		for (String value : dataframe.getRowNames())
		{
			set.add(value);
		}
		return set;
	}
	
	private static Map<String,String> loadHacks()
	{
		//http://www.sljfaq.org/afaq/dates.html
		Map<String,String> map=Maps.newLinkedHashMap();
		DataFrame<String> dataframe=DataFrame.parseTabFile("./data/lookup/dates-fuzzy.txt");
		for (String value : dataframe.getRowNames())
		{
			map.put(value, dataframe.getStringValue("replace", value));
		}
		return map;
	}
	
	private final LocalDate date;
	private final DateMask dateMask;
	private final String value;
	private final Integer year;
	private final Integer month;
	private final Integer day;
	
	public FuzzyDate(String value, LocalDate date)
	{
		//assert(date!=null);
		this.value=value;
		this.date=date;
		this.year=date.getYear();
		this.month=date.getMonthValue();
		this.day=date.getDayOfMonth();
		this.dateMask=DateMask.NULL;
	}
	
	public FuzzyDate(String value, Integer year)
	{
		//log.debug("adjusted incomplete date: "+year);
		this.year=year;
		this.month=null;
		this.day=null;
		this.value=value;
		this.date=parseLocalDate(year+"/01/01");
		this.dateMask=DateMask.年のみ判明;
	}
	
	public FuzzyDate(String value, Integer year, Integer month)
	{
		System.out.println("adjusted incomplete date ("+value+"): "+year+"/"+month);
		this.value=value;
		this.year=year;
		this.month=month;
		this.day=null;
		this.date=parseLocalDate(year+"/"+StringHelper.padLeft(month, 2)+"/01");
		this.dateMask=DateMask.年月のみ判明;
	}
	
	public FuzzyDate(String value, Integer year, Integer month, Integer day, DateMask dateMask)
	{
		//log.debug("adjusted incomplete date: "+year+"/"+month+"/"+day);
		this.value=value;
		this.year=year;
		this.month=month;
		this.day=day;
		this.date=parseLocalDate(year+"/"+StringHelper.padLeft(month,2)+"/"+day);
		this.dateMask=dateMask;
	}
	
	public FuzzyDate(String value)
	{
		this.value=value;
		this.year=null;
		this.month=null;
		this.day=null;
		this.date=null;
		this.dateMask=DateMask.日付不明;
	}
	
	public boolean isFuzzy()
	{
		return dateMask!=DateMask.NULL;
	}
	
	public boolean hasDate()
	{
		return date!=null;
	}
	
	public LocalDate getDate(){return date;}
	public String getValue(){return value;}
	public DateMask getDateMask(){return dateMask;}
	
	public Integer getYear(){return year;}
	public Integer getMonth(){return month;}
	public Integer getDay(){return day;}
	
	public static boolean hasContent(final String value)
	{
		return StringHelper.hasContent(value) && !isUnknown(value);
	}
	
	public static FuzzyDate parse(final String value)
	{
		try
		{
			return parseDate(value);
		}
		catch(Exception e)
		{
			LogUtil.log("cannot parse date: "+value+": "+e);
			e.printStackTrace();
			return new FuzzyDate(value);
			//throw new CException("cannot parse date: "+value, e);
		}
	}
	
	private static FuzzyDate parseDate(final String origvalue)
	{
		if (!hasContent(origvalue))
			return new FuzzyDate(origvalue);
		if (hacks.containsKey(origvalue))
			return FuzzyDate.parse(hacks.get(origvalue));
		String value=clean(origvalue);
		// orivalue checks first
		//System.out.println("origvalue="+origvalue+", value="+value);
		if (origvalue.matches("[0-9]{4}/\\?\\?/\\?\\?"))//2009/??/??
		{
			//System.out.println("matched year only");
			int year=Integer.parseInt(origvalue.substring(0,4));
			if (okay(year))
				return new FuzzyDate(origvalue,year);
		}
		else if (origvalue.matches("[0-9]{4}/[0-9]{2}/\\?\\?"))//2009/03/??
		{
			//System.out.println("matched year+month only");
			int year=Integer.parseInt(origvalue.substring(0,4));
			int month=Integer.parseInt(origvalue.substring(5,7));
			if (okay(year, month))
				return new FuzzyDate(origvalue, year, month);
		}
		else if (origvalue.matches("\\?\\?\\?\\?/[0-9]{2}/[0-9]{2}"))//????/09/17
		{
			//System.out.println("matched month+date only");
			int month=Integer.parseInt(origvalue.substring(5,7));
			int date=Integer.parseInt(origvalue.substring(8,10));
			if (month>=1 && month<=12 && date>=1 && date<=31)
				return new FuzzyDate(origvalue, null, month, date, DateMask.月日のみ判明);
		}
		else if (origvalue.matches(".{2}[0-9]{1,2}年[0-9]{1,2}月[0-9]{1,2}日"))//平成26年01月06日,平成26年03月7日
		{
			LocalDate date=LocalDateHelper.parseJapaneseDate(origvalue);
			return new FuzzyDate(origvalue, date);
		}
		else if (origvalue.matches("[SH][0-9]{1,2}年?"))//H21年
		{
			Integer year=LocalDateHelper.parseImperialYear(origvalue);
			if (okay(year))
				return new FuzzyDate(origvalue, year);
		}
		else if (origvalue.equals(DateMask.日付不明.name()) || origvalue.matches("\\?\\?\\?\\?/\\?\\?/\\?\\?"))//????/??/??
		{
			//return new FuzzyDate(origvalue, null, null, null, DateMask.日付不明);
			return new FuzzyDate(origvalue);
		}
		
		//// check cleaned values
		else if (value.matches("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}"))
		{
			LocalDate date=LocalDateHelper.parseDate(value,LocalDateHelper.MMDDYYYY_PATTERN);//, false);
			return new FuzzyDate(origvalue, date);
		}
		else if (value.matches("[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}"))
		{
			LocalDate date=parseLocalDate(value);
			return new FuzzyDate(origvalue, date);
		}
		else if (value.matches("[0-9]{4}\\.[0-9]{1,2}\\.[0-9]{1,2}"))//2013.7.31
		{
			LocalDate date=parseLocalDate(value);//, false);
			return new FuzzyDate(origvalue, date);
		}
		else if (value.contains(" JST "))//Wed Oct 21 00:00:00 JST 2009
		{
			LocalDate date=LocalDateHelper.parseDate(value, LocalDateHelper.EXCEL_PATTERN);//, false);
			return new FuzzyDate(origvalue, date);
		}
		else if (value.matches("[0-9]{4}/[0-9]{1,2}/"))//1985/10/
		{
			value=StringHelper.chomp(value);//remove last character
			int index=value.indexOf("/");
			int year=Integer.parseInt(value.substring(0,index));
			int month=Integer.parseInt(value.substring(index+1));
			if (okay(year, month))
				return new FuzzyDate(origvalue, year, month);
		}
		else if (value.matches("[0-9]{4}/[0-9]{1,2}"))//1985/10月
		{
			int index=value.indexOf("/");
			int year=Integer.parseInt(value.substring(0,index));
			int month=Integer.parseInt(value.substring(index+1));
			if (okay(year, month))
				return new FuzzyDate(origvalue, year, month);
		}
		else if (value.matches("[0-9]{4}"))
		{
			int year=Integer.parseInt(value);
			if (okay(year))
				return new FuzzyDate(value,year);
		}
//		else if (value.matches("[0-9]{4}/\\?\\?/\\?\\?"))//2015/??/??
//		{
//			int index=value.indexOf("/");
//			int year=Integer.parseInt(value.substring(0,index));
//			return new FuzzyDate(origvalue, year);
//		}
		else if (value.matches("[0-9]{4}//[0-9]{1,2}"))//2011//31
		{
			int index=value.indexOf("/");
			int year=Integer.parseInt(value.substring(0,index));
			if (okay(year))
				return new FuzzyDate(value,year);
		}
		else if (value.matches("[0-9]{5}"))//42682
		{
			LocalDate date=ExcelHelper.parseExcelDate(Double.parseDouble(value));
			if (okay(date.getYear()))
				return new FuzzyDate(origvalue, date);
		}
		else
		{
			LocalDate date=LocalDateHelper.parseDate(value, Constants.DATE_PATTERN, false);
			if (date!=null)
				return new FuzzyDate(origvalue, date);
			date=LocalDateHelper.parseDate(value, LocalDateHelper.DATETIME_PATTERN, false);
			if (date!=null)
				return new FuzzyDate(origvalue, date);
		}
		logFuzzyDate(origvalue);
		return new FuzzyDate(origvalue);
	}
	
	private static boolean okay(Integer year)
	{
		boolean result=year>1900 && year<2100; // hack - arbitrary
		//System.out.println("checking year: "+year+"="+result);
		return result;
	}
	
	private static boolean okay(Integer year, Integer month)
	{
		boolean result=okay(year) && month>=1 && month<=12;
		//System.out.println("checking month: "+month+"="+result);
		return result;
	}
	
	private static boolean isUnknown(String value)
	{
		return unknowns.contains(value);//.toLowerCase());
	}
	
	private static String clean(String value)
	{
		// clean
		value=StringHelper.trim(value);
		value=StringHelper.replace(value, "/??/??", "");
		value=StringHelper.replace(value, "?", "");
		value=StringHelper.replace(value, "？", "");
		value=StringHelper.replace(value, "～", "");	
		value=StringHelper.replace(value, ".", "");
		value=StringHelper.replace(value, "頃", "");
		value=StringHelper.replace(value, "ごろ", "");
		value=StringHelper.replace(value, "末", "");
		value=StringHelper.replace(value, "年代", "");//1990年代
		value=StringHelper.replace(value, "以前", "");
		value=StringHelper.replace(value, "上旬", "");
		value=StringHelper.replace(value, "中旬", "");
		value=StringHelper.replace(value, "初旬", "");
		value=StringHelper.replace(value, "継続中", "");
		value=StringHelper.replace(value, "：終了予定", "");
		value=StringHelper.replace(value, "予定", "");
		value=StringHelper.replace(value, "現在投与中", "");
		value=StringHelper.replace(value, "投与中", "");
		value=StringHelper.replace(value, " 00:00:00+09", "");
		value=StringHelper.replace(value, " 00:00:00+10", "");
		// reformat
		value=StringHelper.replace(value, "年", "/");//2006年11頃
		value=StringHelper.replace(value, "月", "/");
		if (value.endsWith("/"))
			value=StringHelper.chomp(value);
		return value;
	}

	// provides the simplest format for use in generating date keys
	public String format()
	{
		//System.out.println("dateMask="+dateMask);
		if (!isFuzzy())
			return LocalDateHelper.format(date, DateHelper.YYYYMMDD_PATTERN);
		switch(dateMask)
		{
		case 年のみ判明:
			return year+"/??/??";
		case 年月のみ判明:
			return year+"/"+StringHelper.padLeft(month,2)+"/??";
		case 月日のみ判明:
			return "????/"+StringHelper.padLeft(month,2)+"/"+StringHelper.padLeft(day,2);
		case 日付不明:
			if (StringHelper.hasContent(value))
				return value;// hack?
			else return "????/??/??";
		case NULL:
		default:
			return "????/??/??";
		}
	}
	
	// very strict about year - either ???? or exact match
	public static double getWeight(FuzzyDate date1, FuzzyDate date2)
	{
		if (!matches(date1.getYear(), date2.getYear()))
			return 0;
		double distance=0.6;
		if (matches(date1.getMonth(), date2.getMonth()))
			distance+=0.35;
		if (matches(date1.getDay(), date2.getDay()))
			distance+=0.05;
		return distance;
	}
	
	private static boolean matches(Integer value1, Integer value2)
	{
		return matches(value1, value2, true);
	}
	
	private static boolean matches(Integer value1, Integer value2, boolean strict)
	{
		if (isNull(value1) || isNull(value2))
			return true;
		if (!strict && (value1.equals(1) || value2.equals(1)))
			return true;
		return value1.equals(value2);
	}
	
	private static boolean isNull(Integer value)
	{
		return value==null;
	}
	
	// compares if fuzzy dates match the year, month, etc.
	public boolean matches(LocalDate date)
	{
		if (!isFuzzy())
			return LocalDateHelper.datesMatch(this.date, date);
		switch(dateMask)
		{
		case 年のみ判明:
			return LocalDateHelper.matchYear(this.date, date);
		case 年月のみ判明:
			return LocalDateHelper.matchYearMonth(this.date, date);
		case 日付不明:// unknown
			return true; // matches anything
		default:
			return false;
		}
	}
	
	public boolean matches(Collection<LocalDate> dates)
	{
		for (LocalDate date : dates)
		{
			if (matches(date))
				return true;
		}
		return false;
	}
	
	public static boolean matches(FuzzyDate date1, FuzzyDate date2)
	{
		if (!matches(date1.getYear(), date2.getYear()))
			return false;
		if (!matches(date1.getMonth(), date2.getMonth(), true))//false
			return false;
		if (!matches(date1.getDay(), date2.getDay(), false))
			return false;
		return true;
	}
	
	public static boolean isFuzzyDate(String value)
	{
		return value.matches("[1-2][0-9]{3}/[0-1][0-9]/[0-3][0-9]") || //full date
				value.matches("\\?\\?\\?\\?/[0-1][0-9]/[0-3][0-9]") || // month and day
				value.matches("[1-2][0-9]{3}/[0-1][0-9]/\\?\\?") || // year and month
				value.matches("\\?\\?\\?\\?/\\?\\?/[0-3][0-9]") || //year only
				value.matches("\\?\\?\\?\\?/\\?\\?/\\?\\?"); // no information
	}
	
	public static List<FuzzyDate> parse(Collection<String> values)
	{
		List<FuzzyDate> fuzzydates=Lists.newArrayList();
		for (String value : values)
		{
			fuzzydates.add(FuzzyDate.parse(value));
		}
		return fuzzydates;
	}
	
	// merge dates such as 2016/02/?? and 2016/??/??
	// merge dates such as ????/02/06 and 2016/02/06
	public static List<FuzzyDate> merge(List<FuzzyDate> fuzzydates)
	{
		//System.out.println("before sort: "+StringHelper.join(FuzzyDate.format(fuzzydates)));
		Collections.sort(fuzzydates, new FuzzyDateComparator());
		//System.out.println("after sort: "+StringHelper.join(FuzzyDate.format(fuzzydates)));
		if (fuzzydates.size()<2)
			return fuzzydates;
		List<FuzzyDate> keep=Lists.newArrayList();
		for (FuzzyDate fuzzydate : fuzzydates)
		{
			if (!matches(fuzzydate, keep))
			{
				//System.out.println("adding unique date: "+fuzzydate.format());
				keep.add(fuzzydate);
			}
		}
		return keep;
	}
	
	public static boolean matches(FuzzyDate date, List<FuzzyDate> fuzzydates)
	{
		for (FuzzyDate curdate : fuzzydates)
		{
			if (date==curdate)
				return true;
			if (FuzzyDate.matches(date, curdate))
				return true;
		}
		return false;
	}
	
	public static boolean match(List<FuzzyDate> dates)
	{
		if (dates.size()<2)
			return true;
		FuzzyDate date=dates.get(0);
		return FuzzyDate.matches(date, StringHelper.subList(dates, 1));
	}
	
	////////////////////////////////////////////////////////////	
	
	public static List<String> format(List<FuzzyDate> fuzzydates)
	{
		List<String> values=Lists.newArrayList();
		for (FuzzyDate fuzzydate : fuzzydates)
		{
			values.add(fuzzydate.format());
		}
		return values;
	}
	
	public static LocalDate parseLocalDate(String value)
	{
		List<String> patterns=Lists.newArrayList(LocalDateHelper.YYYYMMDD_PATTERN, LocalDateHelper.YYYYMD_PATTERN);
		return LocalDateHelper.parseDate(value, patterns, true);
	}
	
	public static void logFuzzyDate(String value)
	{
		String filename="fuzzydates.txt";
		LogUtil.logMessage(filename, value);
		//throw new CException("error parsing fuzzy date: "+value);
	}
	
	public String toString()
	{
		return "date="+LocalDateHelper.format(date, Constants.DATE_PATTERN)+
				", value="+value+
				", dateMask="+dateMask.name()+
				", year="+year+
				", month="+month+
				", day="+day;
	}
	
	@SuppressWarnings("serial")
	public static class FuzzyDateComparator implements Comparator<FuzzyDate>, Serializable
	{
		public int compare(FuzzyDate date1, FuzzyDate date2)
		{
			Integer year1=get(date1.getYear());
			Integer year2=get(date2.getYear());
			Integer month1=get(date1.getMonth());
			Integer month2=get(date2.getMonth());
			Integer day1=get(date1.getDay());
			Integer day2=get(date2.getDay());
			
//			System.out.println("year1="+year1);
//			System.out.println("year2="+year2);
//			System.out.println("year2.compareTo(year1)="+year2.compareTo(year1));
//			
//			System.out.println("month1="+month1);			
//			System.out.println("month2="+month2);
//			System.out.println("month2.compareTo(month1)="+month2.compareTo(month1));
//			
//			System.out.println("day1="+day1);
//			System.out.println("day2="+day2);
//			System.out.println("day2.compareTo(day1)="+day2.compareTo(day1));
			
			if (!year1.equals(year2))
				return year2.compareTo(year1);
			if (!month1.equals(month2))
				return month2.compareTo(month1);
			return day2.compareTo(day1);
		}
		
		private Integer get(Integer value)
		{
			if (value==null)
				return 1;
			else return value;
		}
	}
}
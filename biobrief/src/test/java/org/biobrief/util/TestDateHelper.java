package org.biobrief.util;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestDateHelper
public class TestDateHelper
{
	//@Test
	public void findDate()
	{
		Date date=DateHelper.findDate("(MP-7)2009/9/2??");
		System.out.println("extracted date="+date);
		assertThat(date).isNotNull();
	}
	
	//@Test
	public void parseDate()
	{
		String strdate="2020/06/14";
		Date date=DateHelper.parse(strdate, Constants.DATE_PATTERN);
		System.out.println("date="+date);
		//assertThat(date).isNotNull();
	}
	
	//@Test
	public void isAfter()
	{
		Date minDate=DateHelper.parse("2022/06/14", Constants.DATE_PATTERN);
		Date downloadDate=DateHelper.parse("2020/06/14", Constants.DATE_PATTERN);
		System.out.println("is downloadDate ("+downloadDate+") after minDate ("+minDate+")? "+DateHelper.isAfter(downloadDate, minDate));
	}
	
	@Test
	public void parseJapaneseDate()
	{
		String value="令和7年2月7日";
		Date date=DateHelper.parseJapaneseDate(value, true);
		System.out.println("JP date: "+date.toString());
		
		//String value = "令和6年2月27日";
//		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//			.appendPattern("GGGGy年M月d日")
//			.toFormatter(Locale.JAPAN)
//			.withChronology(JapaneseChronology.INSTANCE);
//		
//		JapaneseDate japaneseDate = JapaneseDate.from(formatter.parse(value));
//		LocalDate isoDate = LocalDate.from(japaneseDate);
//		
//		System.out.println(isoDate);  // 2024-02-27
//		
//		Date date = Date.from(isoDate.atStartOfDay(ZoneId.of("Asia/Tokyo")).toInstant());
//		
//		System.out.println("date="+date);  // 2024-02-27
		
//		//String value="令和7年2月7日";
//		String value="成26年03月26日";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("GGGGy年M月d日").withLocale(Locale.JAPAN);
//
//		try
//		{
//			JapaneseDate jdate = JapaneseDate.from(formatter.parse(value));
//			System.out.println("JP date: "+jdate.toString());
//		}
//		catch (DateTimeParseException e)
//		{
//			e.printStackTrace();
//		}

		
		//Date date=DateHelper.parseJapaneseDate("令和7年2月7日", true);
		//System.out.println("JP date: "+date.toString());
	}
}

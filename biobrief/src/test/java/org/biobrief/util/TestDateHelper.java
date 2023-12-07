package org.biobrief.util;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

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
	
	@Test
	public void isAfter()
	{
		Date minDate=DateHelper.parse("2022/06/14", Constants.DATE_PATTERN);
		Date downloadDate=DateHelper.parse("2020/06/14", Constants.DATE_PATTERN);
		System.out.println("is downloadDate ("+downloadDate+") after minDate ("+minDate+")? "+DateHelper.isAfter(downloadDate, minDate));
	}
}

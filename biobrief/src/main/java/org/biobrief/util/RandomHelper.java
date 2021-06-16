package org.biobrief.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public final class RandomHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(RandomHelper.class);
	private static final Integer MAX_ITERATIONS=100;
	
	private RandomHelper(){}
	
	public static int randomInteger(int size)
	{
		if (size<=0)
			throw new CException("randomInteger size is <=0: "+size);
		Random random=new Random();
		return random.nextInt(size);
	}
	
	public static int randomInteger(int min, int max)
	{
		//logger.debug("randomInteger: ("+min+"-"+max+")");
		if (max<min)
		{
			System.err.println("max is less than min: ("+min+"-"+max+")");
			max=min;
		}
		if (min==max)
			max+=1;
		return min+randomInteger(max-min);
	}
	
	public static List<Integer> randomIntegers(int num, int min, int max, boolean unique)
	{
		if (max-min<num)
			num=max-min;
		List<Integer> values=Lists.newArrayList();
		int counter=0;
		while (true)
		{
			if (counter>MAX_ITERATIONS)
				throw new CException("cannot find randomIntegers after 100 iterations: num="+num+", min="+min+", max="+max+", unique="+unique);
			if (values.size()>=num)
				return values;
			int value=randomInteger(min,max);
			if (!unique || !values.contains(value))
				values.add(value);
			counter++;
		}
	}
	
	public static Float randomFloat()
	{
		Random random=new Random();
		return random.nextFloat();
	}
	
	public static Float randomFloat(float max)
	{
		return randomFloat()*max;
	}
//	
//	public static Float randomFloat(float min, float max)
//	{
//		return randomInteger((int)min,(int)max)+randomFloat();
//	}
//	
	public static Float randomFloat(float min, float max)
	{
		//logger.debug("randomFloat: ("+min+"-"+max+")");
		if (min>=0 && max<=1)
			return min+randomFloat(max-min);		
		float offset=0;
		if (min<0)
			offset=Math.abs(min);
		float value=randomInteger((int)(min+offset),(int)(max+offset))+randomFloat();
		value=value-offset;
		//logger.debug("random with negative min ("+min+"-"+max+"). offset="+offset+", value="+value);
		return value;
	}
	
	public static Boolean randomBoolean()
	{
		return randomBoolean(0.5f);
	}
	
	public static Boolean randomBoolean(double prob_true)
	{
		return randomFloat()<=prob_true;
	}
	
	public static String randomText(List<String> items)
	{
		return items.get(RandomHelper.randomInteger(items.size()));
	}
	
	public static String randomText(String ... args)
	{
		List<String> items=Arrays.asList(args);
		return randomText(items);
	}
		
//	public static LocalDate randomDate()
//	{
//		LocalDate date=LocalDateHelper.setDate(1950+RandomHelper.randomInteger(60), RandomHelper.randomInteger(12)+1, RandomHelper.randomInteger(30)+1);
//		return date;
//	}
//	
//	public static LocalDate randomDate(int minyear)
//	{
//		return LocalDateHelper.setDate(minyear+RandomHelper.randomInteger(60), RandomHelper.randomInteger(12)+1, RandomHelper.randomInteger(30)+1);
//	}
//	
//	public static LocalDate randomDate(int minyear, int maxyear)
//	{
//		int diff=maxyear-minyear+1;
//		return LocalDateHelper.setDate(minyear+RandomHelper.randomInteger(diff), RandomHelper.randomInteger(12)+1, RandomHelper.randomInteger(30)+1);
//	}
//	
//	public static LocalDate randomDate(LocalDate mindate)
//	{
//		return LocalDateHelper.addWeeks(mindate, RandomHelper.randomInteger(52*3));//up to three years later
//	}
//	
//	public static LocalDate randomDate(LocalDate mindate, LocalDate maxdate)
//	{
//		//logger.debug("mindate="+mindate+", maxdate="+maxdate);
//		int minutes=LocalDateHelper.getDuration(mindate, maxdate); //in minutes
//		//int weeks=minutes/(60*24*7);
//		int days=minutes/(60*24);
//		if (days==0)
//			days=1;
//		//logger.debug("minutes="+minutes+", weeks="+weeks);
//		//return LocalDateHelper.addWeeks(mindate, RandomHelper.randomInteger(1,weeks));
//		return LocalDateHelper.addDays(mindate, RandomHelper.randomInteger(1,days));
//	}
	
	public static <T> T randomItem(List<T> items)
	{
		return items.get(RandomHelper.randomInteger(items.size()));
	}
	
	public static <T> T randomItem(Collection<T> items)
	{
		return randomItem(Lists.newArrayList(items));
	}
	
	public static <T extends Enum<T>> T randomEnum(T[] values)
	{
		return values[RandomHelper.randomInteger(values.length)];
	} 
	
//	public static String getRandomWord(int min, int max)
//	{
//		int length=min+randomInteger(max);
//		StringBuilder buffer=new StringBuilder();
//		for (int index=0;index<length;index++)
//		{
//			buffer.append(StringHelper.ALPHABET.charAt(randomInteger(26)));
//		}
//		return buffer.toString();
//	}
	
	public static char randomLetter()
	{
		return StringHelper.alphabet.charAt(randomInteger(26));
	}
	
	public static String randomWords(int min, int max)
	{
		int length=min+randomInteger(max);
		List<String> buffer=Lists.newArrayList();
		for (int index=0;index<length;index++)
		{
			buffer.add(randomWord(3,15));
		}
		return StringHelper.join(buffer," ");
	}
	
	public static String randomWord(int min, int max)
	{
		int length=min+randomInteger(max);
		List<String> buffer=Lists.newArrayList();
		for (int index=0;index<length;index++)
		{
			buffer.add(""+randomLetter());
		}
		return StringHelper.join(buffer,"");
	}
}

package org.biobrief.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public final class MathHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(MathHelper.class);
	
	public static final String DEFAULT_PVALUE_FORMAT="0.00E00";
	
	private MathHelper(){}
	
//	public static int randomInteger(int size)
//	{
//		//int rnd=(int)(double)(Math.random()*(double)size);
//		//return rnd;
//		Random random=new Random();
//		return random.nextInt(size);
//	}
	
	public static double calculateDiversity(List<Integer> counts)
	{
		int total=0;
		for (Integer count : counts)
		{
			total+=count;
		}
		
		if (total==0)
			return 0;
		
		double sum=0.0f;
		for (Integer count : counts)
		{
			double p=((double)count/(double)total);
			sum+=p*p;
		}
		return 1/sum;
	}
	
	public static boolean isInteger(String str)
	{
		if (str==null)
			return false;
		str=str.trim();
		if (StringHelper.isEmpty(str))
			return false;
		final char[] numbers = str.toCharArray();
		for (int x = 0; x < numbers.length; x++)
		{
			final char c = numbers[x];
			if ((c >= '0') && (c <= '9'))
				continue;
			return false; // invalid
		}
		return true; // valid
	}
	
	public static Integer parseInt(String str, int dflt)
	{
		Integer num=parseInt(str);
		if (num==null)
			return dflt;
		return num;
	}
	
	public static Integer parseInt(Object obj)
	{
		if (!StringHelper.hasContent(obj))
			return null;
		try
		{
			String str=obj.toString();
			str=StringHelper.replace(str, ",", "");
			if (str.matches("[0-9]+\\.0+")) // remove empty decimal places
				str=str.substring(0,str.indexOf('.'));
			return Integer.valueOf(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public static List<Integer> parseInts(String[] arr)
	{
		List<Integer> values=Lists.newArrayList();
		for (String str : arr)
		{
			values.add(Integer.valueOf(str));
		}
		return values;
	}
	
	public static List<Integer> parseInts(List<String> arr)
	{
		List<Integer> values=Lists.newArrayList();
		for (String str : arr)
		{
			values.add(Integer.valueOf(str));
		}
		return values;
	}
	
	public static Float parseNumberLike(String str)
	{
		if (!StringHelper.hasContent(str) || !isNumberLike(str))
			return null;
		try
		{
			Pattern pattern = Pattern.compile("[0-9.]+");
			Matcher matcher = pattern.matcher(str);
			if (!matcher.find())
				return null;
			return Float.valueOf(matcher.group());
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public static boolean isNumberLike(String str)
	{
		return str.matches(".*[0-9]+.*");
	}
	
	public static boolean isFloat(String str)
	{
		return parseFloat(str)!=null;
	}
	
	public static boolean isDouble(String str)
	{
		return parseDouble(str)!=null;
	}
	
	public static Integer parseInteger(String str)
	{
		if (!StringHelper.hasContent(str))
			return null;
		try
		{
			return Integer.valueOf(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Float parseFloat(String str)
	{
		if (!StringHelper.hasContent(str))
			return null;
		try
		{
			return Float.valueOf(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Float parseFloat(Optional<String> value)
	{
		if (value.isEmpty())
			return null;
		return parseFloat(value.get());
	}
	
	public static Double parseDouble(String str)
	{
		if (!StringHelper.hasContent(str))
			return null;
		try
		{
			return Double.valueOf(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	// tries to parse a boolean field from text
	// considers + equivalent to true and - equivalent to false
	public static Boolean parseBoolean(String str)
	{
		if (!StringHelper.hasContent(str))
			return null;
		if ("+".equals(str))
			return true;
		else if ("-".equals(str))
			return false;
		try
		{
			return Boolean.valueOf(str);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Boolean parseBoolean(String str, Boolean dflt)
	{
		Boolean value=parseBoolean(str);
		if (value==null)
			return dflt;
		return value;
	}
	
	public static List<Integer> getIntersection(List<List<Integer>> lists)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for (List<Integer> list : lists)
		{
			for (Integer id : list)
			{
				if (ids.contains(id))
					continue;
				if (intersects(lists, id))
					ids.add(id);
			}
		}
		return ids;
	}
	
	private static boolean intersects(List<List<Integer>> lists, int id)
	{
		if (lists.isEmpty())
			return false;
		boolean intersects=true;
		for (List<Integer> list : lists)
		{
			if (!list.contains(id))
				intersects=false;
		}
		return intersects;
	}
	
	public static double log2(double num)
	{
		if (num==0)
			throw new CException("can't take log of 0");
		return (Math.log(num)/Math.log(2));
	}
	
	public static int getNumbatches(int total, int batchsize)
	{
		int numbatches=(int)Math.floor((double)total/(double)batchsize);
		if (((double)total)%((double)batchsize)!=0)
			numbatches++;
		//logger.debug("numbatches="+numbatches);
		return numbatches;
	}
	
	public static <T> List<List<T>> getBatches(Collection<T> coll, int batchsize)
	{
		List<T> ids=Lists.newArrayList(coll);
		int iterations=(int)Math.floor((double)ids.size()/(double)batchsize);
		//List<List<T>> batches=new ArrayList<List<String>>();
		List<List<T>> batches=Lists.newArrayList();
		int start=0;
		int end=0;
		for (int batchnumber=0;batchnumber<iterations;batchnumber++)
		{
			start=batchnumber*batchsize;
			end=start+batchsize;
			//logger.debug("batch - from "+start+" to "+end);
			List<T> batch=ids.subList(start,end);
			batches.add(batch);
		}
		if (((double)ids.size())%((double)batchsize)!=0)
			batches.add(ids.subList(start,ids.size()));
		return batches;
	}
	
	public static Float format(Number value, int numdec)
	{
		return Float.valueOf(StringHelper.format(value, numdec));
	}
	
	public static Float format(Float value, String format)
	{
		return Float.valueOf(StringHelper.format(value, format));
	}
	
	public static List<Integer> toIntList(Collection<String> values)
	{
		List<Integer> list=Lists.newArrayList();
		for (String value : values)
		{
			list.add(Integer.valueOf(value));
		}
		return list;
	}
	
	public static int[] toIntArray(Collection<Integer> values)
	{
		int[] arr=new int[values.size()];
		Iterator<Integer> iter=values.iterator();
		for (int index=0; index<values.size(); index++)
		{
			arr[index]=(Integer)iter.next();
		}
		return arr;
	}
	
	public static int[] toIntArray(int...values)
	{
		int[] arr=new int[values.length];
		for (int index=0; index<values.length; index++)
		{
			arr[index]=values[index];
		}
		return arr;
	}
	
	public static String trimLeadingZeros(String value)
	{
		try
		{
			Integer num=Integer.valueOf(value);
			return num.toString();
		}
		catch (NumberFormatException e)
		{
			//System.err.println("could not trim leading zeros: "+value+" "+e.toString());
			return value;
		}
	}
	
	//http://stackoverflow.com/questions/14984664/remove-trailing-zero-in-java
	public static String trimTrailingZeros(String value)
	{
		if (!StringHelper.hasContent(value))
			return value;
		return value.indexOf(".") < 0 ? value : value.replaceAll("0*$", "").replaceAll("\\.$", "");
	}
	
	public static ImmutableTable<String, String, Integer> sortTable(Table<String,String,Integer> table)
	{
		// sort
		//http://stackoverflow.com/questions/11695949/sorting-guava-tables-in-descending-order-based-on-values
		Ordering<Table.Cell<String, String, Integer>> comparator =
				  new Ordering<Table.Cell<String, String, Integer>>() {
				    public int compare(
				        Table.Cell<String, String, Integer> cell1, 
				        Table.Cell<String, String, Integer> cell2) {
				      return cell1.getValue().compareTo(cell2.getValue());
				  }
				};
		// That orders cells in increasing order of value, but we want decreasing order...
		ImmutableTable.Builder<String, String, Integer>
		    sortedBuilder = ImmutableTable.builder(); // preserves insertion order
		for (Table.Cell<String, String, Integer> cell :
		    comparator.reverse().sortedCopy(table.cellSet())) {
		  sortedBuilder.put(cell);
		}
		ImmutableTable<String, String, Integer> sorted=sortedBuilder.build();
		System.out.println("sorted");
		for (Cell<String,String,Integer> cell : sorted.cellSet())
		{
			System.out.println(cell.getRowKey()+"\t"+cell.getColumnKey()+"\t"+cell.getValue());
		}
		return sorted;
	}
	
	//http://stackoverflow.com/questions/5387031/generating-sequence-number-in-java
	//String s = String.format ("%08d", 42);	
	public static List<Integer> seq(Integer start, Integer end, Integer step)
	{
		List<Integer> list=Lists.newArrayList();
		for (int num=start; num<=end; num+=step)
		{
			list.add(num);
		}
		return list;
	}
	
	public static List<Integer> seq(Integer start, Integer end)
	{
		return seq(start, end, 1);
	}
	
	public static Integer max(Collection<Integer> values)
	{
		//assert(values!=null && !values.isEmpty());
		List<Integer> list=Lists.newArrayList(values);
		Collections.sort(list);
		Collections.reverse(list);
		return list.get(0);
	}
	
	public static Integer min(Collection<Integer> values)
	{
		//assert(values!=null && !values.isEmpty());
		List<Integer> list=Lists.newArrayList(values);
		Collections.sort(list);
		return list.get(0);
	}
	
	public static Integer sum(Collection<Integer> values)
	{
		Integer sum=0;
		for (Integer value : values)
		{
			if (value!=null)
				sum+=value;
		}
		return sum;
	}
	
	public static Integer sum(Integer... values)
	{
		return sum(Lists.newArrayList(values));
	}
	
	public static Double s(Collection<Double> values)
	{
		double sum = 0.0;
		for (Double value : values)
		{
			sum += value;
		}
		return sum/values.size();  
	}	
	
	public static Double average(Collection<Double> values)
	{
		double sum = 0.0;
		for (Double value : values)
		{
			sum += value;
		}
		return sum/values.size();  
	}	
	
	//https://ryanharrison.co.uk/2013/10/04/java-calculate-the-harmonic-mean.html
	public static double harmonicMean(Collection<Double> values)
	{  
		double sum = 0.0;
		for (Double value : values)
		{
			sum += 1.0/value; 
		}
		return values.size() / sum; 
	}
	
	public static Float parseScientificNotation(String value)
	{
		value=StringHelper.trim(value);
		value=StringHelper.replace(value, "*10^", "E");
		value=StringHelper.replace(value, "×10^", "E");
		try
		{
			BigDecimal result = new BigDecimal(value);
			return result.floatValue();
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
//	public static String toScientificNotation(String value)
//	{
//		DecimalFormat decimalFormat = new DecimalFormat("0.00E00");
//		return decimalFormat.format(new BigDecimal(value).doubleValue());
//	}
	
	public static String toScientificNotation(String value)
	{
		return toScientificNotation(new BigDecimal(value).doubleValue());
	}
	
	public static String toScientificNotation(Double value)
	{
		return toScientificNotation(value, DEFAULT_PVALUE_FORMAT);
	}
	
	public static String toScientificNotation(Double value, String format)
	{
		DecimalFormat decimalFormat = new DecimalFormat(format);
		return decimalFormat.format(value);
	}
	
	public static String toPercent(Double value, int numdec)
	{
		if (value==null)
			return StringHelper.EMPTY_STRING;
		return format(value*100.0, numdec)+"%";
	}

	public static String toPercent(Float value, int numdec)
	{
		if (value==null)
			return StringHelper.EMPTY_STRING;
		return format(value*100.0, numdec)+"%";
	}
	
	public static Float parseFrequency(String value)
	{
		//System.out.println("trying to parse frequency: value=["+value+"]");
		if (!StringHelper.hasContent(value))
			return null;
		if (value.contains("%"))
			return parseFloat(StringHelper.remove(value, "%").trim())/100.0f;
		return parseFloat(value);
	}
	
	public static Float divideFloat(Integer num1, Integer num2)
	{
		return (float)num1/((float)num2);
	}
	
	public static Double divideDouble(Integer num1, Integer num2)
	{
		return (double)num1/((double)num2);
	}
}

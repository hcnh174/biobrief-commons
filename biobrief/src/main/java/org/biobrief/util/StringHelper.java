fixfpackage org.biobrief.util;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.biobrief.util.Constants.ArraySelect;
import org.biobrief.util.Constants.FirstLast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.ibm.icu.text.Transliterator;

//https://www.baeldung.com/java-string-split-multiple-delimiters
//https://www.baeldung.com/java-remove-start-end-double-quote
public final class StringHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(StringHelper.class);
	
	public static final String EMPTY_STRING="";
	public static final String UNICODE_SPACE=" ";
	public static final String JP_SPACE="　";
	public static final String SPACE=" ";
	public static final String COMMA=",";
	public static final String DASH="-";
	public static final String NEWLINE="\n";
	public static final String TAB="\t";
	public static final String UTF8="UTF-8";
	public static final String SHIFT_JIS="Shift_JIS";
	public static final String UTF16LE="UTF-16LE";
	public static final String CP437="Cp437";//https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
	public static final String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String alphabet="abcdefghijklmnopqrstuvwxyz";
	public static final String DEFAULT_JOIN_DELIMITER=",";
	
	private StringHelper(){}
	
	public static <R,C,V> String toString(Table<R,C,V> table)
	{
		List<List<String>> rows=Lists.newArrayList();
		List<String> header=Lists.newArrayList("key");
		for (C colKey : table.columnKeySet())
		{
			header.add(colKey.toString());
		}
		rows.add(header);
		for (R rowKey : table.rowKeySet())
		{
			List<String> row=Lists.newArrayList();
			row.add(rowKey.toString());
			for (C colKey : table.columnKeySet())
			{
				V value=table.get(rowKey, colKey);
				if (value!=null)
					row.add(value.toString());
				else row.add(EMPTY_STRING);
			}
			rows.add(row);
		}
		StringBuilder buffer=new StringBuilder();
		for (List<String> row : rows)
		{
			buffer.append(StringHelper.join(row, TAB));
			buffer.append(NEWLINE);
		}
		return buffer.toString();
	}
	
	public static <K,V> String toString(Map<K,V> map)
	{
		List<String> list=Lists.newArrayList();
		for (K name : map.keySet())
		{
			V value=map.get(name);
			list.add("["+name+"]="+value);
		}
		return StringHelper.join(list, NEWLINE);
	}
	
	public static <K,V> String toString(Multimap<K,V> multimap)
	{
		List<String> list=Lists.newArrayList();
		for (K name : multimap.keySet())
		{
			Collection<V> values=multimap.get(name);
			for (V value : values)
			{
				list.add("["+name+"]="+value);
			}
		}
		return StringHelper.join(list, NEWLINE);
	}
	
	public static <V> String toString(Collection<V> list)
	{
		List<String> buffer=Lists.newArrayList();
		for (V item : list)
		{
			buffer.add(item.toString());
		}
		return StringHelper.join(buffer, NEWLINE);
	}
	
//	public static <V> String toString(List<V> list)
//	{
//		List<String> buffer=Lists.newArrayList();
//		for (V item : list)
//		{
//			buffer.add(item.toString());
//		}
//		return StringHelper.join(buffer, NEWLINE);
//	}
	
	@SuppressWarnings("rawtypes")
	public static String toString(Enum enm)
	{
		if (enm!=null)
			return enm.toString();
		return EMPTY_STRING;
	}
	
//	public static String toString(double[][] grid)
//	{
//		CTable table=new CTable();
//		for(int i=0; i<grid.length; i++)
//		{
//			CTable.Row row=table.addRow();
//			for(int j=0; j<grid[i].length; j++)
//			{
//				row.add(grid[i][j]);
//			}
//		}
//		return table.toString();
//	}
	
	public static String toString(Object obj)
	{
		return ReflectionToStringBuilder.reflectionToString(obj);
	}
	
	public static String asString(Object obj)
	{
		if (obj!=null)
			return toString(obj);
		return null;
	}
	
	public static ToStringBuilder stringBuilder(Object obj)
	{
		return new ToStringBuilder(obj);
	}
	
	public static EqualsBuilder equalsBuilder()
	{
		return new EqualsBuilder();
	}
	
	public static HashCodeBuilder hashCodeBuilder()
	{
		return new HashCodeBuilder();
	}
	
	public static HashCodeBuilder hashCodeBuilder(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber) 
	{
		return new HashCodeBuilder(initialNonZeroOddNumber,multiplierNonZeroOddNumber);
	}
	
	public static String trimAllWhitespace(String str)
	{
		return StringUtils.trimAllWhitespace(str);
	}
	
	public static List<String> trimAllWhitespace(List<String> values)
	{
		List<String> trimmed=Lists.newArrayList();
		for (String value : values)
		{
			trimmed.add(trimAllWhitespace(value));
		}
		return trimmed;
	}
	
	public static boolean hasWhitespace(String str)
	{
		return StringUtils.containsWhitespace(str);
	}
	
	// removes HTML tags from string
	// probably better to use CRichTextFilter
	public static String stripHtml(String str)
	{
		return str.replaceAll("<\\/?[^>]+>", EMPTY_STRING);
	}
	
	// trims each item in a collection
	// removes empty items
	public static List<String> trim(Collection<String> list)
	{
		List<String> ids=Lists.newArrayList();
		for (String id : list)
		{
			id=trim(id);
			if (!isEmpty(id))
				ids.add(id);
		}
		return ids;
	}
	
	public static String replace(String str, String target, String replace)
	{
		return StringUtils.replace(str, target, replace);
	}
	
	public static String remove(String str, String target)
	{
		return StringUtils.replace(str, target, EMPTY_STRING);
	}
	
	// joins using empty space
	public static String concat(Iterable<? extends Object> collection)
	{
		return join(collection, EMPTY_STRING);
	}
	
	public static String join(Iterable<? extends Object> collection)
	{
		return join(collection, DEFAULT_JOIN_DELIMITER);
	}
	
	public static String join(Iterable<? extends Object> collection, String delimiter)
	{
		return join(collection, delimiter, false);
	}
	
	public static String join(Iterable<? extends Object> collection, String delimiter, boolean skipEmpty)
	{
		if (collection==null || !collection.iterator().hasNext())
			return EMPTY_STRING;
		StringBuilder buffer=new StringBuilder();
		for (Iterator<? extends Object> iter=collection.iterator();iter.hasNext();)
		{
			Object item=iter.next();
			if (item==null)
				continue;
			String value=item.toString();
			if (skipEmpty && !hasContent(value))
				continue;
			buffer.append(value);
			if (iter.hasNext())
				buffer.append(delimiter);
		}
		return buffer.toString();
	}
	
	public static String join(Object[] array)
	{
		return join(array, DEFAULT_JOIN_DELIMITER);
	}
		
	public static String join(Object[] array, String delimiter)
	{
		if (array==null || array.length==0)
			return EMPTY_STRING;
		StringBuilder buffer=new StringBuilder();
		for (int index=0;index<array.length;index++)
		{
			buffer.append(array[index]);
			if (index<array.length-1)
				buffer.append(delimiter);
		}
		return buffer.toString();
	}
	
	public static String join(int[] array, String delimiter)
	{
		if (array==null || array.length==0)
			return EMPTY_STRING;
		StringBuilder buffer=new StringBuilder();
		for (int index=0;index<array.length;index++)
		{
			buffer.append(array[index]);
			if (index<array.length-1)
				buffer.append(delimiter);
		}
		return buffer.toString();
	}
	
	public static String join(double[] array, String delimiter)
	{
		if (array==null || array.length==0)
			return EMPTY_STRING;
		StringBuilder buffer=new StringBuilder();
		for (int index=0;index<array.length;index++)
		{
			buffer.append(array[index]);
			if (index<array.length-1)
				buffer.append(delimiter);
		}
		return buffer.toString();
	}
	
	public static String joinLines(Iterable<? extends Object> collection)
	{
		return join(collection, NEWLINE);
	}
	
	// adds the "pad" character to the right as many times as necessary
	// to make the string the specified length
	// throws exception if truncated strings longer than the specified length
	public static String padRight(String str, char pad, int length)
	{
		int remainder=length-str.length();
		String padded=str+repeatString(String.valueOf(pad),remainder);
		if (padded.length()>length)
			throw new CException("padded string is longer than the specified length: ["+str+"] length="+length);
		return padded;
	}
	
	// adds the "pad" character to the left as many times as necessary
	// to make the string the specified length
	// throws exception if truncated strings longer than the specified length
	public static String padLeft(String str, char pad, int length)
	{
		int remainder=length-str.length();
		String padded=repeatString(String.valueOf(pad),remainder)+str;
		if (padded.length()>length)
			System.err.println("padded string is longer than the specified length: ["+str+"] length="+length);
			//throw new CException("padded string is longer than the specified length: ["+str+"] length="+length);
		return padded;
	}
	
	public static String padLeft(Integer num, int length)
	{
		return String.format("%0"+length+"d", num);
		//return padLeft(num.toString(),'0',length);
	}
	
	// returns a collection instead of string - for use with join
	public static List<String> duplicateString(String str, int numtimes)
	{
		List<String> values=Lists.newArrayList();
		for (int index=0;index<numtimes;index++)
		{
			values.add(str);
		}
		return values;
	}
	
	public static String repeatString(String str, int numtimes)
	{
		if (numtimes<=0)
			return "";
		return str.repeat(numtimes);
	}
	
//	public static String repeatString(String str, int numtimes)
//	{
//		if (numtimes<=0)
//			return "";//throw new CException("numtimes is less than 0: "+numtimes+" for repeating string ["+str+"]");
//		StringBuilder buffer=new StringBuilder(EMPTY_STRING);
//		for (int index=0;index<numtimes;index++)
//		{
//			buffer.append(str);
//		}
//		return buffer.toString();
//	}
	
	public static String repeatString(String str, int numtimes, String delimiter)
	{
		List<String> list=Lists.newArrayList();
		for (int index=0;index<numtimes;index++)
		{
			list.add(str);
		}
		return join(list,delimiter);
	}
	
	public static String reverse(String str)
	{
		StringBuilder buffer=new StringBuilder(str);
		buffer.reverse();
		return buffer.toString();
	}
	
	public static String chunk(String original, int cols, String separator)
	{
		StringBuilder buffer=new StringBuilder();
		chunk(original,cols,separator,buffer);
		return buffer.toString();
	}
	
	public static void chunk(String original, int cols, String separator, StringBuilder buffer)
	{
		int length=original.length();
		int lines=(int)Math.ceil((double)length/(double)cols);
		int position=0;
		for (int index=0;index<lines;index++)
		{
			if (index<lines-1)
			{
				buffer.append(original.substring(position,position+cols));
				buffer.append(separator);
				position+=cols;
			}
			else buffer.append(original.substring(position));
		}
	}
	
	public static List<String> chunk(String original, int cols)
	{
		int length=original.length();
		int numlines=(int)Math.ceil((double)length/(double)cols);
		int position=0;
		List<String> lines=Lists.newArrayList();
		for (int index=0;index<numlines;index++)
		{
			if (index<numlines-1)
			{
				lines.add(original.substring(position,position+cols));
				position+=cols;
			}
			else lines.add(original.substring(position));
		}
		return lines;
	}
	
	public static List<String> split(String raw, String delimiter, boolean clean)
	{
		if (!clean)
			return split(raw,delimiter);
		return clean(split(raw,delimiter));
	}
	
	public static List<String> split(String raw, boolean clean)
	{
		return split(raw, DEFAULT_JOIN_DELIMITER, clean);
	}
	
	public static List<String> split(String raw, String delimiter)
	{
		if (raw==null)
			return Collections.emptyList();
		return Splitter.on(delimiter).splitToList(raw);
	}
	
	public static List<String> split(String raw)
	{
		return split(raw, DEFAULT_JOIN_DELIMITER);
	}
	
	public static List<String> splitTabs(String raw)
	{
		return split(raw, TAB);
	}
	
	public static String[] splitAsArray(String raw, String delimiter)
	{
		Iterable<String> iter=Splitter.on(delimiter).split(raw);
		return Iterables.toArray(iter, String.class);
	}
	
	// splits on newlines, trims, and skips over blank lines
	public static List<String> splitLines(String str)
	{
		return splitLines(str, NEWLINE);
	}
	
	public static List<String> splitLines(String str, String delimiter)
	{
		List<String> lines=Lists.newArrayList();
		for (String line : split(trim(str),delimiter))
		{
			line=trim(line);
			if (isEmpty(line))
				continue;
			lines.add(line);
		}
		return lines;
	}
	
	public static List<Integer> splitInts(String str, String delimiter, boolean clean)
	{
		List<Integer> ints=splitInts(str, delimiter);
		if (!clean)
			return ints;
		Collections.sort(ints);
		Set<Integer> set=Sets.newLinkedHashSet(ints);
		return Lists.newArrayList(set);
	}
	
	public static List<Integer> splitInts(String str, String delimiter)
	{
		List<Integer> ints=Lists.newArrayList();
		if (!hasContent(str))
			return ints;
		str=trim(str);
		for (String item : split(str, delimiter))
		{
			ints.add(Integer.valueOf(trim(item)));
		}
		return ints;
	}
	
	public static List<Float> splitFloats(String str, String delimiter)
	{
		List<Float> floats=Lists.newArrayList();
		for (String item : split(str,delimiter))
		{
			floats.add(Float.valueOf(trim(item)));
		}		
		return floats;
	}
	
	public static List<Double> splitDoubles(String str, String delimiter)
	{
		List<Double> doubles=Lists.newArrayList();
		for (String item : split(str,delimiter))
		{
			doubles.add(Double.valueOf(trim(item)));
		}		
		return doubles;
	}
	
	public static <T> Set<T> removeDuplicates(Collection<T> col)
	{
		return Sets.newLinkedHashSet(col);
	}
	
	public static Collection<Collection<Integer>> split(Collection<Integer> ids, int max)
	{
		Collection<Collection<Integer>> lists=Lists.newArrayList();
		List<Integer> list=Lists.newArrayList();
		lists.add(list);
		if (ids.size()<=max)
		{
			list.addAll(ids);
			return lists;
		}
		for (Integer id : ids)
		{
			if (list.size()>=max)
			{
				list=Lists.newArrayList();
				lists.add(list);
			}
			list.add(id);
		}
		return lists;
	}
	
	// trims each item and removes duplicates and empty lines
	public static List<String> clean(Iterable<String> items)
	{
		List<String> list=Lists.newArrayList();
		for (String item : items)
		{
			item=trim(item);
			if (!isEmpty(item) && !list.contains(item))
				list.add(item);
		}
		return list;
	}
		
	public static List<String> clean(String[] items)
	{
		List<String> list=Lists.newArrayList();
		for (String item : items)
		{
			item=trim(item);
			if (!isEmpty(item) && !list.contains(item))
				list.add(item);
		}
		return list;
	}

	public static List<String> toLower(Iterable<String> items)
	{
		List<String> list=Lists.newArrayList();
		for (String item : items)
		{
			list.add(item.toLowerCase());
		}
		return list;
	}
	
	public static List<String> toUpper(Iterable<String> items)
	{
		List<String> list=Lists.newArrayList();
		for (String item : items)
		{
			list.add(item.toUpperCase());
		}
		return list;
	}
	
	public static List<String> trim(List<String> values)
	{
		List<String> trimmed=Lists.newArrayList();
		for (String value : values)
		{
			trimmed.add(trim(value));
		}
		return trimmed;
	}
	
	public static String[] trim(String[] values)
	{
		for (int index=0;index<values.length;index++)
		{
			values[index]=trim(values[index]);
		}
		return values;
	}
	
	
	
	public static String[] toArray(Collection<String> values)
	{
		String[] arr=new String[values.size()];
		values.toArray(arr);
		return arr;
	}

	public static Object[] toObjectArray(Collection<Object> values)
	{
		Object[] arr=new Object[values.size()];
		values.toArray(arr);
		return arr;
	}
		
	public static String urlEncode(String str)
	{	
		return urlEncode(str,Charsets.US_ASCII);
	}
	
	public static String urlEncode(String str, Charset encoding)
	{
		try
		{
			return URLEncoder.encode(str,encoding.toString());
		}
		catch (UnsupportedEncodingException e)
		{
			throw new CException(e);
		}
	}
	
	public static String urlDecode(String str)
	{	
		return urlDecode(str,Charsets.US_ASCII);
	}
	
	public static String urlDecode(String str, Charset encoding)
	{
		try
		{
			return URLDecoder.decode(str,encoding.toString());
		}
		catch(UnsupportedEncodingException e)
		{
			throw new CException(e);
		}
	}
	
	public static String truncate(String str, int length)
	{
		if (str==null)
			return EMPTY_STRING;
		if (str.length()>=length)
			return str.substring(0,length);
		else return str;
	}
	
	public static String truncateEllipsis(String str, int length)
	{
		return truncate(str,length,"...");
	}
	
	// appends ellipsis or other trailing characters, adjusting the length accordingly
	public static String truncate(String str, int length, String trailing)
	{
		if (str==null)
			return EMPTY_STRING;
		if (str.length()<length)
			return str;
		int adjusted=length-trailing.length();
		return truncate(str,adjusted)+trailing;
	}
		
	public static String generateID()
	{
		try
		{
			// Initialize SecureRandom
			// This is a lengthy operation, to be done only upon
			// initialization of the application
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
	
			// generate a random number
			String randomNum = String.valueOf(prng.nextInt());
	
			// get its digest
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] result =  sha.digest(randomNum.getBytes());
			String id=hexEncode(result);
	
			//vlogger.debug("Random number: " + randomNum);
			////logger.debug("Message digest: " + id);
			return id;
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new CException(e);
		}
	}
	
	//https://www.baeldung.com/java-uuid
	public static String generateUUID()
	{
		UUID uuid=UUID.randomUUID();
		return uuid.toString();
	}
	
	//http://stackoverflow.com/questions/4267475/generating-uuid-but-only-for-8-characters
	public static String generateShortUUID()
	{
		UUID uuid=UUID.randomUUID();
		long l=ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
		return Long.toString(l, Character.MAX_RADIX);
	}
	
	/**
	  * The byte[] returned by MessageDigest does not have a nice
	  * textual representation, so some form of encoding is usually performed.
	  *
	  * This implementation follows the example of David Flanagan's book
	  * "Java In A Nutshell", and converts a byte array into a String
	  * of hex characters.
	  *
	  * Another popular alternative is to use a "Base64" encoding.
	  */
	private static String hexEncode(byte[] input)
	{
		StringBuilder buffer=new StringBuilder();
		char[] digits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		for (int index = 0; index < input.length; ++index)
		{
			byte b = input[index];
			buffer.append(digits[(b&0xf0)>>4]);
			buffer.append(digits[b&0x0f]);
		}
		return buffer.toString();
	}
	
	public static String[] convertToArray(String...args)
	{
		return convertToArray(Lists.newArrayList(args));
	}
	
	public static String[] convertToArray(List<String> list)
	{
		String[] arr=new String[list.size()];
		list.toArray(arr);
		return arr;
	}
	
	public static String formatDecimal(Float value, int numdecimals)
	{
		if (value==null)
			return EMPTY_STRING;
		return formatDecimal((double)value,numdecimals);
	}
	
	public static String formatDecimal(Double value, int numdecimals)
	{
		if (value==null)
			return EMPTY_STRING;
		return String.format("%."+numdecimals+"f",value);
	}
	
	public static String formatScientificNotation(Double value, int numdecimals)
	{
		if (value==null)
			return EMPTY_STRING;
		String pattern="0."+repeatString("#",numdecimals)+"E0";
		DecimalFormat format=new DecimalFormat(pattern);
		String formatted=format.format(value);
		if ("0E0".equals(formatted))
			formatted="0";
		return formatted;
	}
	
	public static void checkIdentifier(String identifier)
	{
		if (!hasContent(identifier))
			throw new CException("identifier is null or empty: ["+identifier+"]");
	}
	
	public static boolean containsHtml(String str)
	{
		String regex="</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";
		return str.matches(regex);
	}
	
	public static boolean containsLinks(String str)
	{
		return str.toLowerCase().indexOf("<a href=")!=-1;
	}
	
	public static boolean isSpam(String str)
	{
		return (containsHtml(str) || containsLinks(str));
	}
	
	public static boolean hasContent(Collection<?> coll)
	{
		return !(coll==null || coll.isEmpty());
	}
	
	public static boolean hasContent(Map<?,?> map)
	{
		return !(map==null || map.isEmpty());
	}
	
	public static boolean hasContent(Optional<?> obj)
	{
		if (!obj.isPresent())
			return false;
		return hasContent(obj.get());
	}
	
	public static boolean hasContent(Object...objs)
	{
		for (Object obj : objs)
		{
			if (!hasContent(obj))
				return false;
		}
		return true;
	}
	
	public static void checkHasContent(Object obj)
	{
		String message="expected content. trimmed value is null or empty string: ["+obj+"]";
		checkHasContent(obj, message);
	}
	
	public static void checkHasContent(Object obj, String message)
	{
		if (!hasContent(obj))
			throw new CException(message);
	}
	
	// slightly optimized to only trim if not null and length>0
	public static boolean hasContent(Object obj)
	{
		if (obj==null)
			return false;
		String value=obj.toString();
		if (value.length()==0)
			return false;
		value=trim(value);
		if (value.length()==0)
			return false;
		return !EMPTY_STRING.equals(value);
	}
	
	public static boolean hasContentAll(Object... objs)
	{
		for (Object obj : objs)
		{
			if (!hasContent(obj))
				return false;
		}
		return true;
	}
	
	public static <T> Optional<T> hasContentOr(T obj)
	{
		if (!hasContent(obj))
			return Optional.empty();
		return Optional.of(obj);
	}
	
	// null safe string comparison
	public static boolean isEqual(Object value1, Object value2)
	{
		System.out.println("isEqual(value1=["+value1+"] value2=["+value2+"])");
		if (!hasContentAll(value1, value2))
			return false;
		return value1.equals(value2);
	}
	
	public static String createQueryString(Map<String, Object> params)
	{
		List<String> pairs=new ArrayList<String>();
		for (Map.Entry<String,Object> entry : params.entrySet())
		{
			pairs.add(entry.getKey()+"="+entry.getValue());
		}
		return StringHelper.join(pairs,"&");
	}
	
	public static String createQueryString(Multimap<String, Object> multimap)
	{
		Map<String, Collection<Object>> map=multimap.asMap();
		List<String> pairs=new ArrayList<String>();
		for (String name : map.keySet())
		{
			for (Object value : map.get(name))
			{
				pairs.add(name+"="+value);
			}
		}
		return StringHelper.join(pairs,"&");
	}
	
	// skips null values
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createMap(Object... args)
	{
		// if the only parameter was already a Map, return as is
		if (args.length==1 && args[0] instanceof Map)
			return (Map<String,Object>)args[0];
		int size=args.length/2;
		if (args.length%2!=0)
			throw new CException("name/value args should be provided as a multiple of 2: "+join(args,","));
		Map<String,Object> map=new LinkedHashMap<String,Object>();
		for (int index=0;index<size;index++)
		{
			Object name=args[index*2];
			if (!(name instanceof String))
				throw new CException("parameter name at position "+index*2+" should be a String: "+join(args,","));	
			Object value=args[index*2+1];
//			if (name==null)
//			{
//				logger.debug("arg name is null for arg "+index+" and value "+value);
//				continue;
//			}
			if (value==null)
			{
				//logger.debug("arg value is null for name "+name);
				continue;
			}
			map.put(name.toString(),value);
		}
		return map;
	}

//	public static String extractBefore(String str, String suffix)
//	{
//		int end=str.indexOf(suffix);
//		if (end==-1)
//			throw new CException("can't find suffix \""+suffix+"\" in string: "+str);
//		return str.substring(start,end);
//	}
	
	public static String extractBetween(String str, String prefix, String suffix)
	{
		int start=str.indexOf(prefix);
		if (start==-1)
			throw new CException("can't find prefix \""+prefix+"\" in string: "+str);
		start+=prefix.length();
		int end=str.indexOf(suffix,start);
		if (end==-1)
			throw new CException("can't find suffix \""+suffix+"\" in string: "+str);
		return str.substring(start,end);
	}

	public static boolean startsWith(List<String> lines, String prefix)
	{
		for (int index=0; index<lines.size(); index++)
		{
			String line=lines.get(index);
			if (line.startsWith(prefix))
				return true;
		}
		return false;
	}
	
	public static List<String> extractLines(List<String> lines, String startprefix, String endprefix)
	{
		Integer startline=null;
		Integer endline=null;
		for (int index=0; index<lines.size(); index++)
		{
			String line=lines.get(index);
			if (line.startsWith(startprefix))
			{
				if (startline==null) // make sure to set the first instance
					startline=index;
			}
			else if (startline!=null && index>startline && line.startsWith(endprefix))
			{
				endline=index;
				break;
			}
		}
		if (startline==null)
			throw new CException("could not find start prefix: "+startprefix);
		if (endline==null)
			throw new CException("could not find end prefix: "+endprefix);
		if (startline.equals(endline))
			throw new CException("start and end line are the same: "+startline+"="+endline+" for start="+startprefix+" end="+endprefix);
		if (startline>endline)
			throw new CException("start>end "+startline+">"+endline+" for start="+startprefix+" end="+endprefix);
		return lines.subList(startline+1, endline);
	}
	
	public static String getText(byte[] bytes)
	{
		if (bytes==null)
			return null;
		StringBuilder buffer=new StringBuilder();
		for (int index=0;index<bytes.length;index++)
		{
			buffer.append((char)bytes[index]);
		}
		return buffer.toString();
	}
	
	public static String escapeSingleQuotes(String value)
	{
		value=replace(value,"'","\'");
		return value;
	}
	
	public static String escapeDoubleQuotes(String value)
	{
		value=replace(value,"\"","\\\"");
		return value;
	}
	
	// https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings
	public static String escapeSingleQuotesBash(String value)
	{
		value=replace(value,"'","'\"'\"'");
		return value;
	}
	
	public static String escapeSql(String value)
	{
		value=replace(value,"'","''");
		value=replace(value, "’", "''");
		return value;
	}
	
	public static List<String> escapeSql(Collection<String> values)
	{
		List<String> newvalues=Lists.newArrayList();
		for (String value : values)
		{
			newvalues.add(escapeSql(value));
		}
		return newvalues;
	}
	
	public static List<String> escapeSql(String[] values)
	{
		List<String> newvalues=Lists.newArrayList();
		for (String value : values)
		{
			newvalues.add(escapeSql(value));
		}
		return newvalues;
	}
	
	public static boolean isEmptyJson(String str)
	{
		return (!hasContent(str) || "null".equals(str) || "{}".equals(str));
	}
	
	/*
	public static String encodeBase64(String unencoded)
	{
		return Base64Encoder.encode(unencoded);
	}
	
	public static String decodeBase64(String encoded)
	{
		return Base64Decoder.decode(encoded);
	}
	*/
	
	public static boolean isEmailAddress(String email)
	{
		return !(!hasContent(email) || email.indexOf('@')==-1 || email.indexOf('.')==-1);
	}
	
	public static String parenthesize(String value)
	{
		return "("+value+")";
	}
	
	public static String quote(String str)
	{
		return doubleQuote(str);
	}
	
	public static String doubleQuote(String str)
	{
		return "\""+str+"\"";
	}
	
	public static List<String> doubleQuote(Collection<String> values)
	{
		List<String> newvalues=Lists.newArrayList();
		for (String value : values)
		{
			newvalues.add(doubleQuote(value));
		}
		return newvalues;
	}
	
	public static String singleQuote(String str)
	{
		return "'"+str+"'";
	}
	
	public static List<String> singleQuote(Collection<String> values)
	{
		List<String> newvalues=Lists.newArrayList();
		for (String value : values)
		{
			newvalues.add(singleQuote(value));
		}
		return newvalues;
	}
		
	public static String unquote(String str)
	{
		if (!hasContent(str))
			return EMPTY_STRING;
		char ch1=str.charAt(0);
		char ch2=str.charAt(str.length()-1);
		if (isQuote(ch1) && isQuote(ch2))  
			return str.substring(1,str.length()-1);
		return str;
	}
	
	public static List<String> unquote(Collection<String> values)
	{
		List<String> newvalues=Lists.newArrayList();
		for (String value : values)
		{
			newvalues.add(unquote(value));
		}
		return newvalues;
	}
	
	private static boolean isQuote(char ch)
	{
		return (ch=='"' || ch=='\'');
	}
	
	public static String sqlQuote(String str)
	{
		return singleQuote(escapeSql(str));
	}
	
	public static boolean isEmpty(String str)
	{
		return str==null || EMPTY_STRING.equals(str);
	}
	
	//public static List<String> prefix(Iterable<?> iter, String token)
	public static List<String> prefix(String token, Iterable<?> iter)
	{
		return wrap(iter, token, EMPTY_STRING);
	}
	
	public static List<String> suffix(Iterable<?> iter, String token)
	{
		return wrap(iter, EMPTY_STRING, token);
	}
	
	public static List<String> wrap(Iterable<?> iter, String token)
	{
		return wrap(iter, token, token);
	}

	public static List<String> wrap(Iterable<?> iter, String before, String after)
	{
		List<String> items=Lists.newArrayList();
		for (Object item : iter)
		{
			items.add(before+item+after);
		}
		return items;
	}
	
	public static List<String> wrapCollection(Collection<?> col, String token)
	{
		return wrapCollection(col,token,token);
	}
	
	public static List<String> wrapCollection(Collection<?> col, String before, String after)
	{
		List<String> items=Lists.newArrayList();
		for (Object item : col)
		{
			items.add(before+item.toString()+after);
		}
		return items;
	}
	
	public static String unbracket(String str)
	{
		return unwrap(str,"[","]");
	}
	
	public static String unparenthesize(String str)
	{
		return unwrap(str,"(",")");
	}
	
	public static String unbrace(String str)
	{
		return unwrap(str,"{","}");
	}
	
	public static String unbacktick(String str)
	{
		return unwrap(str, "`", "`");
	}
	
	//<IFNβ><IFNα>
	public static List<String> splitByAngleBrackets(String str)
	{
		str=StringHelper.unwrap(str, "<", ">");
		return StringHelper.split(str, "><");
	}
	
	public static String unwrap(String str, String prefix, String suffix)
	{
		str=str.trim();
		int start=str.indexOf(prefix);
		if (start==-1)
			throw new CException("cannot find prefix "+prefix+" in string "+str);
		int end=str.lastIndexOf(suffix);
		if (end==-1)
			throw new CException("cannot find suffix "+suffix+" in string "+str);
		return str.substring(start+1,end);
	}
	
	public static String squareWrap(String str)
	{
		return "【"+str+"】";
	}
	
	private static Charset use_encoding=Charsets.ISO_8859_1;
	
	// same as logger.debug, but uses Unicode
		//http://www.velocityreviews.com/forums/t137667-changing-system-out-encoding.html
	public static void print(String str)
	{
		print(str,use_encoding);
	}
	
	public static void print(String str, Charset charset)
	{
		getPrintStream(charset).print(str);
	}
	
	public static void println(String str)
	{
		println(str,use_encoding);
	}
	
	public static void println(String str, Charset charset)
	{
		getPrintStream(charset).println(str);
	}
	
	private static PrintStream getPrintStream(Charset charset)
	{
		try
		{
			return new PrintStream(System.out, true, charset.toString());
		}
		catch (UnsupportedEncodingException e)
		{
			throw new CException(e);
		}
	}

	// uses Springs StringUtils class, which uses Character.isWhitespace and should remove Japanese spaces as well
	//strip(), stripTrailing(), stripLeading()
	//@SuppressWarnings("deprecation")
	public static String trim(String str)
	{
		if (str==null)
			return null;
		//return StringUtils.trimWhitespace(str);
		return str.strip();
	}
	
	//@SuppressWarnings("deprecation")
	public static String trimLeading(String str)
	{
		if (str==null)
			return null;
		//return StringUtils.trimLeadingWhitespace(str);
		return str.stripLeading();
	}
	
	public static String trimTrailing(String str)
	{
		if (str==null)
			return null;
		//return StringUtils.trimTrailingWhitespace(str);
		return str.stripTrailing();
	}
	
	public static int numOccurrences(String str, String target)
	{
		int count=0;
		int start=0;
		while ((start=str.indexOf(target,start))!=-1)
		{
			start+=target.length();
			count++;
		}
		return count;
	}
	
	public static String normalize(String value)
	{
		//return normalize(value, Normalizer.Form.NFKC);
		return normalize(value, Normalizer.Form.NFC);
	}
	
	public static String normalize(String value, String type)
	{
		return normalize(value, getNormalizerForm(type));
	}
	
	public static String normalize(String value, Normalizer.Form form)
	{
		if (value==null)
			return null;
		value=Normalizer.normalize(value, form);
		return value;
	}
	
	private static Normalizer.Form getNormalizerForm(String type)
	{
//		if (type.equals("NFD"))
//			return Normalizer.Form.NFD;
//		if (type.equals("NFC"))
//			return Normalizer.Form.NFC;
//		if (type.equals("NFKD"))
//			return Normalizer.Form.NFKD;
//		if (type.equals("NFKC"))
//			return Normalizer.Form.NFKC;
		return Normalizer.Form.valueOf(type);
		//throw new CException("unrecognized normalizer form: "+type);		
	}
	
	/*
	public static String fixWideChars(String value)
	{
		if (value==null)
			return null;
		value=trim(value);
		value=normalize(value);
		value=fixWideNumbers(value);
		value=fixWideLetters(value);
		value=value.replace("ã€€"," ");
		value=value.replaceAll("  "," ");
		value=value.replace("?","?");
		value=value.replace("ã€œ","~");
		value=value.replace("ã€�",",");
		value=value.replace("ï¼�","/");
		return value;
	}
	
	public static String fixWideLetters(String value)
	{
		if (value==null)
			return null;
		String letters1="ï¼¡ï¼¢ï¼£ï¼¤ï¼¥ï¼¦ï¼§ï¼¨ï¼©ï¼ªï¼«ï¼¬ï¼­ï¼®ï¼¯ï¼°ï¼±ï¼²ï¼³ï¼´ï¼µï¼¶ï¼·ï¼¸ï¼¹ï¼º";
		String letters2="ABCDEFGHIJLKMNOPQRSTUVWXYZ";
		for (int index=0;index<letters1.length();index++)
		{
			String letter1=letters1.substring(index,index+1);
			String letter2=letters2.substring(index,index+1);
			//logger.debug("replacing "+letter1+" with "+letter2);
			value=value.replace(letter1,letter2);
		}
		return value;
	}
	
	public static String fixWideNumbers(String value)
	{
		if (value==null)
			return null;
		value=value.replace("ï¼‘","1");
		value=value.replace("ï¼’","2");
		value=value.replace("ï¼“","3");
		value=value.replace("ï¼”","4");
		value=value.replace("ï¼•","5");
		value=value.replace("ï¼–","6");
		value=value.replace("ï¼—","7");
		value=value.replace("ï¼˜","8");
		value=value.replace("ï¼™","9");
		value=value.replace("ï¼�","0");
		value=value.replace("ï¼Ž",".");
		return value;
	}
	
	public static String normalize(String value)
	{
		if (value==null)
			return null;
		value=Normalizer.normalize(value,Normalizer.Form.NFKC);
		//value=Normalizer.normalize(value,Normalizer.Form.NFD);
		return value;
	}
	*/
	
	public static List<String> getNames(Collection<? extends Enum<?>> items)
	{
		List<String> names=Lists.newArrayList();
		for (Enum<?> item : items)
		{
			names.add(item.name());
		}
		return names;
	}	
	
	/////////////////////////////////////
	
	public static int dflt(Integer value)
	{
		return dflt(value, 0);
	}
	
	public static int dflt(Integer value, int dflt)
	{
		return (value==null) ? dflt : value;
	}	
	
	public static String dflt(String value)
	{
		return dflt(value, EMPTY_STRING);
	}
	
	public static String dflt(String value, String dflt)
	{
		return (value==null) ? dflt : value;
	}
	
	public static String dflt(Object value)
	{
		return dflt(value, EMPTY_STRING);
	}
	
	public static Date dflt(Date value, Date dflt)
	{
		return (value==null) ? dflt : value;
	}
	
	public static Boolean dflt(String value, Boolean dflt)
	{
		return StringHelper.hasContent(value) ? Boolean.valueOf(value) : dflt;
	}
	
	public static String dflt(Object value, String dflt)
	{
		return (value==null) ? dflt : value.toString();
	}
	
	public static String joinNonEmpty(Iterable<String> iter, String delimiter)
	{
		List<String> items=clean(iter);
		return join(items,delimiter);
	}
	
	/// gunk ////////////////////////////////////////////
	
	/*
	private static final Map<String,String> replacements=Collections.synchronizedMap(new LinkedHashMap<String,String>());
	
	static
	{
		replacements.put("%0B","|"); // line break character used for multiple item lists in Filemaker
		replacements.put("%EF%BF%BD","?"); //DB № //%E2%84%96
		//replacements.put("%E2%85%A0","1");//Ⅰ
		//replacements.put("%E2%85%A1","2");//Ⅱ
		//replacements.put("%E2%85%A2","3");//Ⅲ
		//replacements.put("%E2%85%A3","4");//Ⅳ
		//replacements.put("%E9%89%99","鉙");//鉙 \u9259
	}

	
	//http://prefetch.net/projects/postgresql_dtrace/postgrestest/pgjdbc/org/postgresql/core/Utils.java
	public static String removeUnreadableChars(String str)
	{
		return replaceUnreadableChars(str,'?');
	}
	
	public static String replaceUnreadableChars(String str, char ch)
	{
		//if (str.indexOf('\0')!=-1)
		//	logger.debug("found unreadable char: "+str);
		return str.replace('\0',ch);
	}
	
	public static String replaceUnreadableChars(String str, FileHelper.Encoding encoding)
	{
		//logger.debug("replaceUnreadableChars("+str+")");
		String encoded=urlEncode(str,encoding);
		//FileHelper.appendFile("c:/temp/encoded.txt",str,true);
		//FileHelper.appendFile("c:/temp/encoded.txt",encoded,true);
		for (Map.Entry<String,String> entry : replacements.entrySet())
		{
			if (encoded.indexOf(entry.getKey())!=-1)
			{
				//logger.debug("found "+entry.getKey());
				encoded=encoded.replace(entry.getKey(),entry.getValue());
			}
		}
		str=urlDecode(encoded,encoding);
		//str=str.replace('\0','?');
		return str;
	}
	*/
	
//	public static String urlEncode(String str)
//	{	
//		return urlEncode(str,FileHelper.Encoding.US_ASCII);
//	}
//	
//	public static String urlEncode(String str, FileHelper.Encoding encoding)
//	{
//		try
//		{
//			return URLEncoder.encode(str,encoding.toString());
//		}
//		catch (UnsupportedEncodingException e)
//		{
//			throw new CException(e);
//		}
//	}
//	
//	public static String urlDecode(String str)
//	{	
//		return urlDecode(str,FileHelper.Encoding.US_ASCII);
//	}
//	
//	public static String urlDecode(String str, FileHelper.Encoding encoding)
//	{
//		try
//		{
//			return URLDecoder.decode(str,encoding.toString());
//		}
//		catch(UnsupportedEncodingException e)
//		{
//			throw new CException(e);
//		}
//	}
//	
	// from sql underscore to camelCase
	
	public static String toCamelCase(String column)
	{
		String name=CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,column);
		//name=name.replaceAll("([0-9][a-z])", "$1");
		Matcher matcher = Pattern.compile("[0-9][a-z]").matcher(name);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find())
		{
			matcher.appendReplacement(buffer, matcher.group().toUpperCase());
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	public static String toUnderscore(String str)
	{
		String name=CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
		name=name.replaceAll("([0-9])_", "$1");
		return name;
	}
	
	public static List<String> toUnderscore(Collection<String> fields)
	{
		List<String> cols=Lists.newArrayList();
		for (String field : fields)
		{
			cols.add(toUnderscore(field));
		}
		return cols;
	}
	
	public static String toKebobCase(String str)
	{
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
	}

	public static String capitalizeFirstLetter(String name)
	{
		return name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
	public static String uncapitalizeFirstLetter(String name)
	{
		return name.substring(0,1).toLowerCase()+name.substring(1);
	}
	
	//http://stackoverflow.com/questions/4303418/java-unicode-comparison
	public static boolean compare(String str1, String str2)
	{
		Collator usCollator = Collator.getInstance(Locale.US);
		usCollator.setStrength(Collator.PRIMARY);
		return (usCollator.compare(str1, str2) == 0);
	}
//	
//	public static String format(Float value, int numdec)
//	{
//		String format="#."+StringHelper.repeatString("#",numdec);
//		DecimalFormat df = new DecimalFormat(format);
//		return df.format(value);
//	}
//	
	public static String format(Number value, int numdec)
	{
		if (value==null)
			return "";
		String format="#."+StringHelper.repeatString("#",numdec);
		DecimalFormat df = new DecimalFormat(format);
		return df.format(value);
	}
	
	public static String format(Number value, String format)
	{
		if (value==null)
			return "";
		DecimalFormat df = new DecimalFormat(format);
		return df.format(value);
	}
	
//	public static Float format(Number value, String format)
//	{
//		DecimalFormat df = new DecimalFormat(format);
//		return Float.valueOf(df.format(value));
//	}
	
	public static String indent(String str)
	{
		return indent(str, 1);
	}
	
	public static String indent(String str, int level)
	{
		str=StringHelper.trim(str);
		if (!StringHelper.hasContent(str))
			return EMPTY_STRING;
		List<String> list=Lists.newArrayList();
		String tabs=StringHelper.repeatString("\t",level);
		List<String> lines=StringHelper.split(str,"\n");
		for (String line : lines)
		{
			list.add(tabs+line);
		}
		return StringHelper.join(list,"\n");//+"\n";
	}
	
	public static void applyIf(Map<String,Object> config, String name, Object value)
	{
		if (!config.containsKey(name))
			config.put(name,value);
	}
	
	public static void applyIf(Map<String,Object> config, Map<String,Object> params)
	{
		for (String param : params.keySet())
		{
			applyIf(config,param,params.get(param));
		}
	}
	
	public static void apply(Map<String,Object> config, String name, Object value)
	{
		config.put(name,value);
	}
	
	public static void apply(Map<String,Object> config, Map<String,Object> params)
	{
		config.putAll(params);
	}	
	
	public static String createElement(String element, Map<String,String> params)
	{
		List<String> attributes=Lists.newArrayList();
		for (String attribute : params.keySet())
		{
			attributes.add(" "+attribute+"="+StringHelper.doubleQuote(params.get(attribute)));
		}
		StringBuilder buffer=new StringBuilder();
		buffer.append("<").append(element);
		buffer.append(StringHelper.join(attributes, EMPTY_STRING));
		buffer.append("/>");
		return buffer.toString();
	}

	public static void attr(StringBuilder buffer, String name, Object value)
	{
		buffer.append(attr(name, value));
	}
	
	public static String attr(String name, Object value)
	{
		if (value==null)
			return EMPTY_STRING;
		return " "+name+"="+StringHelper.doubleQuote(value.toString());
	}
	
	public static String getStyle(Map<String,String> params)
	{
		List<String> attributes=Lists.newArrayList();
		for (String attribute : params.keySet())
		{
			attributes.add(attribute+": "+params.get(attribute));
		}
		return join(attributes,"; ");
	}
	
	public static String getSystemProperty(String name)
	{
		return System.getProperty(name);
	}
	
	public static String getSystemProperty(String name, String dflt)
	{
		String value=getSystemProperty(name);
		if (!hasContent(value))
			value=dflt;
		return value;
	}
	
	public static String getRequiredSystemProperty(String name)
	{
		String value=getSystemProperty(name);
		if (!hasContent(value))
			throw new CException("no value is set for system property: "+name);
		return value;
	}
	
	public static String getJavaBeanName(String name)
	{
		String str=(name.substring(0,1)).toUpperCase();
		str+=name.substring(1);
		return str;
	}
	
	
	////////////////////////////////////////////
	
	public static boolean isBoolean(String value)
	{
		return parseBoolean(value)!=null;
	}
	
	public static Boolean isTrue(Boolean value)
	{
		return value!=null && value==true;
	}
	
	public static Boolean isTrue(String value)
	{
		Boolean result=parseBoolean(value);
		return isTrue(result);
		//return result!=null && result==true;
	}
	
	public static Boolean parseBoolean(String value)
	{
		if (!StringHelper.hasContent(value))
			return null;
		if (value.equals("有") || value.equalsIgnoreCase("true"))
			return true;
		if (value.equals("無") || value.equalsIgnoreCase("false"))
			return false;
		return null;
	}
	
	///////////////////////////////////
	
	// puts the same value in map for all keys in the list - useful for setting initial values
	//@SuppressWarnings("hiding")
	public static <K,V> void putAll(Map<K,V> map, List<K> keys, V value)
	{
		for (K key : keys)
		{
			map.put(key, value);
		}
	}
	
	// cut off last character
	public static String chomp(String value)
	{
		if (!StringHelper.hasContent(value))
			throw new CException("cannot chomp empty string: "+value);
		return value.substring(0,value.length()-1);
	}
	
	public static String chompFirst(String value)
	{
		if (!StringHelper.hasContent(value))
			throw new CException("cannot chomp empty string: "+value);
		return value.substring(1);
	}
	
	public static String chompLast(String value)
	{
		return chomp(value);
	}
	
	public static String chompBoth(String value)
	{
		if (!StringHelper.hasContent(value))
			throw new CException("cannot chomp empty string: "+value);
		return value.substring(1,value.length()-1);
	}
	
	public static String useJpSpaces(String value)
	{
		value=StringHelper.replace(value,StringHelper.UNICODE_SPACE, StringHelper.JP_SPACE);
		value=StringHelper.replace(value,StringHelper.JP_SPACE+StringHelper.JP_SPACE, StringHelper.JP_SPACE);
		return value;
	}
	
	public static <T> List<T> exclude(List<T> list, T skip)
	{
		return exclude(list, Lists.newArrayList(skip));
	}
	
	public static <T> List<T> exclude(List<T> list, Collection<T> skip)
	{
		List<T> newlist=Lists.newArrayList();
		for (T item : list)
		{
			if (!skip.contains(item))
				newlist.add(item);
		}
		return newlist;
	}
	
	//@SuppressWarnings("hiding")
	public static <K,V> Map<K,V> exclude(Map<K,V> map, Collection<K> skip)
	{
		return Maps.filterKeys(map, new Predicate<K>()
		{
			public boolean apply(K key)
			{
				return !skip.contains(key);
			}
		});
	}
	
	public static List<String> getUniqueValues(Map<String, String> map)
	{
		List<String> list=Lists.newArrayList();
		for (String value : map.values())
		{
			if (!list.contains(value))
				list.add(value);
		}
		return list;
	}
	
	///////////////////////////////////////////////////////////////////
	
	public static String escapeUnicode(String value)
	{
		String rule="[^\\u0000-\\u007F] any-hex";
		return transliterate(rule, value);
	}
	
	public static String convertKanaToRomaji(String value)
	{
		String rule="Latin";
		return transliterate(rule, value);
	}

	public static String convertNameToRomaji(String value)
	{
		String rule="Latin; Title";
		String name=transliterate(rule, value);
		List<String> parts=Lists.newArrayList(name.split("\\s"));
		Collections.reverse(parts);
		return join(parts," ");
	}
	
	public static String romaji(String value)
	{
		//String filename=".temp/tmp/kuromoji.txt";
		//FileHelper.appendFile(filename, "new value: "+value);
		List<String> arr=Lists.newArrayList();
		Tokenizer tokenizer = new Tokenizer();
		for (Token token : tokenizer.tokenize(value))
		{
//			FileHelper.appendFile(filename, "new token: "+token.toString());
//			FileHelper.appendFile(filename, "surface="+token.getSurface());
//			FileHelper.appendFile(filename, "features="+token.getAllFeatures());
//			FileHelper.appendFile(filename, "reading="+token.getReading());
//			FileHelper.appendFile(filename, "pronunciation="+token.getPronunciation());
//			FileHelper.appendFile(filename, "baseform="+token.getBaseForm());
//			FileHelper.appendFile(filename, "conjugation form="+token.getConjugationForm());
//			FileHelper.appendFile(filename, "conjugation type="+token.getConjugationType());
//			FileHelper.appendFile(filename, "transliterate="+StringHelper.transliterate("Latin", token.getReading()));
//			FileHelper.appendFile(filename, "---");
			if (token.isKnown())
				arr.add(transliterate("Latin", token.getReading()));
			else arr.add(transliterate("Katakana-Latin", token.getSurface()));
			//else arr.add(transliterate("[:Katakana:]; NFD; Katakana-Latin; (Lower); NFC; ([:Latin:]);", token.getSurface()));
			//else arr.add(transliterate("[:Katakana:]; NFD; Katakana-Latin; (Lower); NFKC; ([:Latin:]);", token.getSurface()));
		}
		//FileHelper.appendFile(filename, "==========================");
		return concat(arr);
	}
	
	public static String furigana(String value)
	{
		//String filename=".temp/tmp/kuromoji.txt";
		//FileHelper.appendFile(filename, "new value: "+value);
		List<String> arr=Lists.newArrayList();
		Tokenizer tokenizer = new Tokenizer();
		for (Token token : tokenizer.tokenize(value))
		{
//			FileHelper.appendFile(filename, "new token: "+token.toString());
//			FileHelper.appendFile(filename, "surface="+token.getSurface());
//			FileHelper.appendFile(filename, "features="+token.getAllFeatures());
//			FileHelper.appendFile(filename, "reading="+token.getReading());
//			FileHelper.appendFile(filename, "pronunciation="+token.getPronunciation());
//			FileHelper.appendFile(filename, "baseform="+token.getBaseForm());
//			FileHelper.appendFile(filename, "conjugation form="+token.getConjugationForm());
//			FileHelper.appendFile(filename, "conjugation type="+token.getConjugationType());
//			FileHelper.appendFile(filename, "transliterate="+StringHelper.transliterate("Latin", token.getReading()));
//			FileHelper.appendFile(filename, "---");
			if (token.isKnown())
				arr.add(token.getReading());
			else arr.add(token.getSurface());
		}
		//FileHelper.appendFile(filename, "==========================");
		return halfwidth(concat(arr));
	}
	
	public static String halfwidth(String value)
	{
		String rule="Fullwidth-Halfwidth";
		return transliterate(rule, value);
	}
	
	public static String fullwidth(String value)
	{
		String rule="Halfwidth-Fullwidth";
		return transliterate(rule, value);
	}
	
	//http://www.avajava.com/tutorials/lessons/what-are-the-system-transliterators-available-with-icu4j.html
	public static String transliterate(String rules, String value)
	{
		Transliterator transliterator=Transliterator.getInstance(rules);
		return transliterator.transliterate(value);
	}

	// remove spaces, make lowercase, make halfwidth 
	public static String fuzzify(String value)
	{
		value=StringHelper.replace(value, " ", EMPTY_STRING);
		value=StringHelper.replace(value, StringHelper.JP_SPACE, EMPTY_STRING);
		value=StringHelper.halfwidth(value);
		return value;
	}
	
	//https://stackoverflow.com/questions/1760654/java-printstream-to-string
	public static String getUnicodeValue(String value)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			try (PrintStream ps = new PrintStream(baos, true, "UTF-8"))
			{
				for (char c : value.toCharArray())
				{
					ps.printf("\\u%04x \n", (int) c); 
				}
			}
			return new String(baos.toByteArray(), StandardCharsets.UTF_8);
		}
		catch (Exception e)
		{
			throw new CException("cannot get Unicode value for value: "+value);
		}
	}
	
	public static void printUnicode(String value)
	{
		System.out.println("printing unicode values for: ["+value+"]");
		for (char c : value.toCharArray())
		{
			System.out.printf("\\u%04x \n", (int) c); 
		}
	}
	
	////////////////////////////////////////////////////
	
	public static String stripSqlComments(String sql)
	{
		Pattern commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
		return commentPattern.matcher(sql).replaceAll(EMPTY_STRING);
	}
	
	public static String stripXmlComments(String xml)
	{
		Pattern commentPattern = Pattern.compile("(?s)<!--.*?-->", Pattern.DOTALL);//<!--[^-->]+-->
		return commentPattern.matcher(xml).replaceAll(EMPTY_STRING);
	}
	
	public static boolean startsWith(String value, String...prefixes)
	{
		for (String prefix : prefixes)
		{
			if (value.startsWith(prefix))
				return true;
		}
		return false;
	}
	
	public static boolean endsWith(String value, String...suffixes)
	{
		for (String suffix : suffixes)
		{
			if (value.endsWith(suffix))
				return true;
		}
		return false;
	}
	
	public static boolean in(String value, String... values)
	{
		for (String val : values)
		{
			if (value.equals(val))
				return true;
		}
		return false;
	}
	
	public static List<String> asList(String[] items)
	{
		List<String> list=Lists.newArrayList();
		for (String item : items)
		{
			list.add(item);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> asList(T...items)
	{
		List<T> list=Lists.newArrayList();
		for (T item : items)
		{
			if (item!=null)
				list.add(item);
		}
		return list;
	}
	
	public static <T> List<T> asList(Iterable<T> iterable)
	{
		if(iterable instanceof List)
			return (List<T>) iterable;
		ArrayList<T> list=Lists.newArrayList();
		if(iterable==null)
			return list;
		for(T item : iterable)
		{
			list.add(item);
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public static String toString(Enum enm, String display)
	{
		return StringHelper.hasContent(display) ? display : enm.name();
	}
	
	public static String nullIfEmpty(String value)
	{
		return hasContent(value) ? value : null;
	}
	
	public static void divider(String pattern)
	{
		int maxlength=80;
		int num=(int)Math.ceil((float)maxlength/(float)pattern.length());
		String str=repeatString(pattern, num);
		if (str.length()>maxlength)
			str=str.substring(0,maxlength);
		System.out.println(str);
	}
	
	public static List<Double> repeatDouble(double value, int num)
	{
		List<Double> list=Lists.newArrayList();
		for (int i=0; i<num; i++)
		{
			list.add(value);
		}
		return list;
	}
	
	// selects the first non-empty value
	public static String select(String...values)
	{
		for (String value : values)
		{
			if (StringHelper.hasContent(value))
				return value;
		}
		return EMPTY_STRING;
	}
	
	// gets all values after the selected index
	public static <T> List<T> subList(List<T> list, int index)
	{
		if (index>list.size())
			return Lists.newArrayList();
		return list.subList(index, list.size());
	}
	
	// gets first N entries from the list
	public static <T> List<T> selectMax(List<T> list, int max)
	{
		if (list.size()<max)
			max=list.size();
		return list.subList(0, max);
	}
	
	public static <R,C,V> Table<R,C,V> createTable()
	{
		return HashBasedTable.create();
	}
	
	//////////////////////////////////////////////
	
	public static <T> List<T> select(List<T> items, ArraySelect selectType)
	{
		if (selectType==null)
			return items;
		if (items.isEmpty())
			return Lists.newArrayList();
		switch(selectType)
		{
		case FIRST:
			return Lists.newArrayList(Iterators.get(items.iterator(), 0));
		case LAST:
			return Lists.newArrayList(Iterators.getLast(items.iterator()));
		case ALL:
		default:
			return items; 
		}	
	}
	
	///////////////////////////////////////////////////
	
	public static <T> T getOne(Collection<T> collection)
	{
		if (collection.size()!=1)
			throw new CException("expected one item. found "+collection.size()+" items: "+StringHelper.toString(collection));
		return Iterators.get(collection.iterator(), 0);
	}
	
	public static <T> T getFirst(Collection<T> collection)
	{
		if (collection.isEmpty())
			return null;
		return Iterators.get(collection.iterator(), 0);
	}
	
	public static <T> T getLast(Collection<T> collection)
	{
		if (collection.isEmpty())
			return null;
		return Iterators.getLast(collection.iterator());
	}
	
	public static <T> T getFirstLast(Collection<T> collection, FirstLast type)
	{
		if (type==FirstLast.FIRST)
			return getFirst(collection);
		else if (type==FirstLast.LAST)
			return getLast(collection);
		else throw new CException("no handler for FirstLast type: "+type);
	}
	
	public static <T> List<T> removeLastElement(List<T> collection)
	{
		return collection.subList(0, collection.size()-1);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isNull(Optional...values)
	{
		for (Optional value : values)
		{
			if (value==null || !value.isPresent())
				return true;
		}
		return false;
	}
	
	public static boolean isNull(Object...values)
	{
		for (Object value : values)
		{
			if (value==null)
				return true;
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////
	
	// split by lines first and look for complete lines
	public static String extractLines(String str, String firstline, String lastline)
	{
		List<String> lines=StringHelper.splitLines(str);
		int start=lines.indexOf(firstline);
		List<String> sublist=lines.subList(start+1, lines.size());
		int end=sublist.indexOf(lastline);
		List<String> subset=(end==-1) ? sublist : sublist.subList(0, end);
		return StringHelper.join(subset, "\n");
	}
	
	public static String extractLines(String str, String firstline)
	{
		List<String> lines=StringHelper.splitLines(str);
		int start=lines.indexOf(firstline);
		List<String> subset=lines.subList(start+1, lines.size());
		return StringHelper.join(subset, "\n");
	}
	
	// split by lines first and look for complete lines
	public static String extractLinesStartingWith(String str, String firstline, String lastline)
	{
		List<String> lines=StringHelper.splitLines(str);
		int start=findLineStartingWith(lines, firstline);
		List<String> sublist=lines.subList(start+1, lines.size());
		int end=findLineStartingWith(sublist, lastline);
		List<String> subset=(end==-1) ? sublist : sublist.subList(0, end);
		return StringHelper.join(subset, "\n");
	}
	
	// remove all whitespace from first/last line tags and compare against trimmed lines but return original lines
	public static List<String> extractLinesStartingWithIgnoreWhitespace(List<String> lines, String firstline, String lastline)
	{
		List<String> trimmed=trimAllWhitespace(lines);
		firstline=trimAllWhitespace(firstline);
		lastline=trimAllWhitespace(lastline);
		int start=findLineStartingWith(trimmed, firstline);
		if (start==-1)
			throw new CException("could not find start prefix: "+firstline);
		//List<String> sublist=lines.subList(start+1, lines.size());
		int end=findLineStartingWith(trimmed, lastline);
		if (end==-1)
			throw new CException("could not find end prefix: "+lastline);
		if (start==end)
			throw new CException("start and end line are the same: "+start+"="+end+" for start="+firstline+" end="+lastline);
		if (start>end)
			throw new CException("start>end "+start+">"+end+" for start="+firstline+" end="+lastline);		
		//List<String> subset=(end==-1) ? sublist : sublist.subList(0, end);
		System.out.println("start index for "+firstline+"="+start);
		System.out.println("end index for "+lastline+"="+end);
		return lines.subList(start+1, end);
	}
	
	public static int findLineStartingWith(List<String> lines, String prefix)
	{
		for (Integer index=0; index<lines.size(); index++)
		{
			if (lines.get(index).startsWith(prefix))
				return index;
		}
		return -1;
	}
	
	public static int findLineStartingWith(List<String> lines, String prefix, int start)
	{
		for (Integer index=start; index<lines.size(); index++)
		{
			if (lines.get(index).startsWith(prefix))
				return index;
		}
		return -1;
	}
	
	public static String joinParams(Map<String,Object> params)
	{
		return joinParams(params, false);
	}
	
	public static String joinParams(Map<String,Object> params, boolean escape)
	{
		StringBuilder buffer=new StringBuilder();
		String separator="";
		for (String name : params.keySet())
		{
			buffer.append(separator);
			buffer.append(name+"="+params.get(name).toString());
			if (escape)
				separator="&amp;";
			else separator="&";
		}
		return buffer.toString();
	}
	
	//https://www.baeldung.com/java-stacktrace-to-string
	public static String getStackTrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String getDigits(String value)
	{
		return value.replaceAll("\\D+","");
	}
	
	//https://www.regextester.com/97925
	public static boolean matchesKeyword(String keyword, String text)
	{
		return text.matches(".*\\b"+keyword+"\\b.*");
	}
	
	///////////////////////////////////////////////////////////
	
	//https://stackoverflow.com/questions/994331/how-to-unescape-html-character-entities-in-java
	//ttps://stackoverflow.com/questions/11145681/how-to-convert-a-string-with-unicode-encoding-to-a-string-of-letters
	public static String unescapeHtmlEntities(String text)
	{
		boolean strictMode = true;
		return org.jsoup.parser.Parser.unescapeEntities(text, strictMode);
	}
	
	/////////////////////////////////////////////////////////////
	
	public static String substring(String value, int start)
	{
		if (!hasContent(value))
			throw new CException("no content. cannot take substring start="+start);
		if (start==-1)
			throw new CException("cannot take substring value="+value+" start="+start);
		if (start>value.length()-1)
			throw new CException("start index is greater than length of string: value="+value+" start="+start);
		return value.substring(start);
	}
	
	public static String substring(String value, int start, int end)
	{
		if (!hasContent(value))
			throw new CException("no content. cannot take substring start="+start+" end="+end);
		if (start==-1 || end==-1)
			throw new CException("cannot take substring value="+value+" start="+start+" end="+end);
		if (start>value.length()-1)
			throw new CException("start index is greater than length of string: value="+value+" start="+start+" end="+end);
		if (end>value.length()-1)
			throw new CException("end index is greater than length of string: value="+value+" start="+start+" end="+end);
		return value.substring(start, end);
	}
	
	public static int indexOf(String str, String target)
	{
		int index=str.indexOf(target);
		if (index==-1)
			throw new CException("cannot find target="+target);
		return index;
	}
	
	public static int indexOf(String str, String target, int start)
	{
		int index=str.indexOf(target, start);
		if (index==-1)
			throw new CException("cannot find target="+target+" with start index="+start);
		return index;
	}
	
	/////////////////////////////////////////////////////////////
	
	private static String separator(String chr)
	{
		return repeatString(chr, 30);
	}
	
	public static void message(String message, String sep)
	{
		String line=separator(sep);
		System.out.println(line);
		System.out.println(message);
		System.out.println(line);
	}
	
	public static void announce(String message)
	{
		message(message, "-");
	}
	
	public static void banner(String message)
	{
		message(message, "*");
	}
	
	//<a href=\"https://cancer.sanger.ac.uk/cosmic/mutation/overview?id=169110131\">c.1633G>A</a>
	public static String extractHref(String link)
	{
		int start=link.indexOf("\"");
		if (start==-1)
			throw new CException("cannot find start quote: "+link);
		start+=1;
		int end=link.indexOf("\"", start);
		if (end==-1)
			throw new CException("cannot find end quote: "+link);
		String href=link.substring(start, end);
		return href;	
	}
	
	public static String getQueryString(String href)
	{
		int index=href.indexOf("?");
		if (index==-1)
			throw new CException("cannot find question mark: "+href);
		return href.substring(index+1);
	}
	
	public static Map<String, String> getQueryParams(String href)
	{
		String qs=getQueryString(href);
		List<String> pairs=StringHelper.split(qs, "&");
		Map<String, String> params=Maps.newLinkedHashMap();
		for (String pair : pairs)
		{
			int index=pair.indexOf("=");
			if (index==-1)
				throw new CException("cannot find equal sign in pair: "+pair);
			String name=pair.substring(0, index);
			String value=pair.substring(index+1);
			params.put(name, value);
		}
		return params;
	}
	
	//https://stackoverflow.com/questions/258486/calculate-the-display-width-of-a-string-in-java
	@SuppressWarnings("unused")
	public static Double getTextWidth(String text, String fontfamily, Double fontsize)
	{
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		Font font = new Font(fontfamily, Font.PLAIN, (int)Math.round(fontsize));
		double textwidth = font.getStringBounds(text, frc).getWidth();
		double textheight = font.getStringBounds(text, frc).getHeight();
		//System.out.println("text="+text+" textwidth="+textwidth+" textheight="+textheight);
		return textwidth;
	}
	
	// tries to find the first occurrence of the value in the text string. if fails, then it tries the next one
	public static int indexOf(String text, String...values)
	{
		int min=Integer.MAX_VALUE;
		for (String value : values)
		{
			int index=text.indexOf(value);
			if (index!=-1 && index<min)
				min=index;//return index;
		}
		if (min==Integer.MAX_VALUE)
			return -1;
		return min;
	}
	
	public static String clip(String text, int length)
	{
		if (text.length()<=length)
			return text;
		return text.substring(0, length);
	}	
	
	//https://stackoverflow.com/questions/43418812/check-whether-a-string-contains-japanese-chinese-characters
	public static boolean containsJapaneseText(String text)
	{
		Pattern pattern=Pattern.compile("[\u3040-\u30ff\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff\uff66-\uff9f]");
		Matcher matcher=pattern.matcher(text);
		return matcher.find();
	}
	
	// returns true if the value contains one of the terms in the list
	public static boolean contains(String value, List<String> terms)
	{
		for (String term : terms)
		{
			if (value.contains(term))
				return true;
		}
		return false;
	}
	
	//https://stackoverflow.com/questions/6120657/how-to-generate-a-unique-hash-code-for-string-input-in-android
	public static String createHash(String str)
	{
		try
		{
			MessageDigest messageDigest=MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes(),0,str.length());
			return new BigInteger(1,messageDigest.digest()).toString(16);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	//https://stackoverflow.com/questions/6120657/how-to-generate-a-unique-hash-code-for-string-input-in-android
	public static long getUniqueLongFromString (String value)
	{
		return UUID.nameUUIDFromBytes(value.getBytes()).getMostSignificantBits();
	}
	
	// returns a hash using two different methods appended to minimize the chance of collisions
	public static String getHash(String text)
	{
		if (!StringHelper.hasContent(text))
			throw new CException("cannot create hash: text value is null or empty: "+text);
		String key=StringHelper.createHash(text)+"-"+StringHelper.getUniqueLongFromString(text);
		return key;
	}
	
	////////////////////////////////////////////////////
	
	public static boolean isDash(String value)
	{
		value=fixDashes(value);
		return value.equals(DASH);
	}
	
	public static String fixNumbers(String value)
	{
		value=StringHelper.replace(value, "０", "0");
		value=StringHelper.replace(value, "１", "1");
		value=StringHelper.replace(value, "２", "2");
		value=StringHelper.replace(value, "３", "3");
		value=StringHelper.replace(value, "４", "4");
		value=StringHelper.replace(value, "５", "5");
		value=StringHelper.replace(value, "６", "6");
		value=StringHelper.replace(value, "７", "7");
		value=StringHelper.replace(value, "８", "8");
		value=StringHelper.replace(value, "９", "9");
		return value;
	}
	
	public static String fixDashes(String value)
	{
		return fixDashes(value, DASH);
	}
	
	public static String fixDashes(String value, String dash)
	{
		value=StringHelper.replace(value, "‐", dash);
		value=StringHelper.replace(value, "-", dash);
		value=StringHelper.replace(value, "－", dash);
		value=StringHelper.replace(value, "―", dash);
		value=StringHelper.replace(value, "ー", dash);
		return value;
	}
	
	public static String fixSymbols(String value)
	{
		value=StringHelper.replace(value, "＊", "*");
		value=StringHelper.replace(value, "＃", "#");
		return value;
	}
	
	public static String fixParentheses(String value)
	{
		value=StringHelper.replace(value, "（", "(");
		value=StringHelper.replace(value, "）", ")");
		return value;
	}
	
	public static String fixSpaces(String value)
	{
		value=StringHelper.replace(value, "　", " ");
		value=StringHelper.replace(value, " ", " ");
		return value;
	}
	
	public static String fixCommas(String value)
	{
		value=StringHelper.replace(value, "、", ",");
		return value;
	}
	
	public static String stripParentheses(String value)
	{
		value=fixParentheses(value);
		if (!value.contains("("))
			return value;
		//System.out.println("stripping parentheses: "+value);
		int start=StringHelper.indexOf(value, "(");
		int end=StringHelper.indexOf(value, ")", start+1);
		String newvalue="";
		if (start!=0)
			newvalue=value.substring(0, start);
		newvalue+=value.substring(end+1);
		newvalue=newvalue.trim();
		return stripParentheses(newvalue);
	}
	
	//https://www.regextester.com/97925
	public static boolean containsWord(String text, String word)
	{
		return text.matches(".*\\b"+word+"\\b.*");
	}
	
	public static String replaceWord(String text, String target, String replace)
	{
		Pattern pattern=Pattern.compile("\\b"+target+"\\b");
		Matcher matcher=pattern.matcher(text);
		if (!matcher.find())
			return text;
		return matcher.replaceAll(replace);
	}
	
	public static String removeWord(String text, String target)
	{
		return replaceWord(text, target, "");
	}
	
	//https://kodejava.org/how-do-i-check-if-a-string-starts-with-a-pattern/
	public static boolean startsWithRegex(String value, String regex)
	{
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(value);
		return matcher.lookingAt();
	}
	
	//https://www.baeldung.com/java-base64-encode-and-decode
	public static String encodeBase64(String value)
	{
		return Base64.getEncoder().encodeToString(value.getBytes());
	}
	
	public static String decodeBase64(String value)
	{
		return new String(Base64.getDecoder().decode(value));
	}
	
//	public static void banner(String message)
//	{
//		System.out.println("********************************");
//		System.out.println(message);
//		System.out.println("********************************");
//	}
	
		
//	// shortcut to get the first group from a regex match
//	public static String matchGroup(String regex, String value)
//	{
//		if (value.matches(regex))
//			return value;
//		Pattern pattern=Pattern.compile(regex);
//		Matcher matcher=pattern.matcher(value);
//		matcher.
//		return matcher.group(1);
//	}
	
	/////////////////////////////////////
//	
//	// generates IDs
//	public static void main(String[] argv)
//	{
//		int num=Integer.parseInt(argv[0]);
//		for (int index=0;index<num;index++)
//		{
//			logger.debug(generateID());
//		}
//	}
}

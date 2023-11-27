package org.biobrief.util;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

//https://www.baeldung.com/jackson-yaml
//https://eloquent-hodgkin-f52b42.netlify.com/
//https://www.baeldung.com/jackson-object-mapper-tutorial
//https://stackoverflow.com/questions/15931082/how-to-deserialize-a-class-with-overloaded-constructors-using-jsoncreator
public final class YamlHelper
{
	private YamlHelper(){}
	
	public static String toYaml(Object... args)
	{
		try
		{
			Object obj=(args.length>1) ? StringHelper.createMap(args) : args[0];
			ObjectMapper mapper=createObjectMapper();
			return mapper.writeValueAsString(obj);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static ObjectMapper createObjectMapper()
	{
		YAMLFactory factory = new YAMLFactory()
				.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES) //removes quotes from strings
				.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)//gets rid of -- at the start of the file.
				.enable(YAMLGenerator.Feature.INDENT_ARRAYS);// enables indentation.
		ObjectMapper mapper=new ObjectMapper(factory);
		configureMapper(mapper);
		return mapper;
	}
	
	public static void configureMapper(ObjectMapper mapper)
	{
		mapper.setDateFormat(new SimpleDateFormat(LocalDateHelper.YYYYMMDD_PATTERN));
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);

		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
	}

	@SuppressWarnings("rawtypes")
	public static Object readFile(String filename, Class cls)
	{
		FileHelper.checkExists(filename);
		return read(FileHelper.readFile(filename), cls);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object read(String yaml, Class cls)
	{
		try
		{
			ObjectMapper mapper=createObjectMapper();
			return mapper.readValue(yaml, cls);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String toYaml(Object obj)
	{
		try
		{			
			ObjectMapper mapper=createObjectMapper();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			SequenceWriter writer=mapper.writerWithDefaultPrettyPrinter().writeValues(out);
			writer.write(obj);
			return new String(out.toByteArray());
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static void writeFile(Object obj, String filename)
	{
		FileHelper.writeFile(filename, toYaml(obj));
	}
}

package org.biobrief.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public final class MustacheHelper
{	
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(MustacheHelper.class);
	
	public static String render(String template, Object obj)
	{
		try
		{
			MustacheFactory mf = new DefaultMustacheFactory();
			//Mustache mustache = mf.compile("template.mustache");
			Mustache mustache = mf.compile(new StringReader(template), "mytemplate");
			StringWriter writer = new StringWriter();
			//mustache.execute(new PrintWriter(System.out), obj).flush();
			mustache.execute(writer, obj).flush();
			return writer.toString();
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
}

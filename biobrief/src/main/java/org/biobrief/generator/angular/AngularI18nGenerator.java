package org.biobrief.generator.angular;

import java.util.List;
import java.util.Map;

import org.biobrief.generator.I18n;
import org.biobrief.generator.Util;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

//http://j2html.com/examples.html
public class AngularI18nGenerator
{	
	public static void generate(boolean overwrite, MessageWriter writer)
	{
		//String filename=Util.ANGULAR_APP_DIRECTORY+"/service/i18n.service.ts";
		String filename="i18n.service.ts";
		if (!FileHelper.exists(filename))
			throw new CException("cannot find i18n service file: "+filename);
		String js=generateTypescript();
		String str=FileHelper.readFile(filename);
		str=Util.insertText(Util.DECLARATIONS, str, js, true);
//		Util.replaceFile(FileType.ANGULAR_SERVICE, filename, str, overwrite);
	}
	
	private static String generateTypescript()
	{
		List<String> list=Lists.newArrayList();
		Map<String, String> map=I18n.load();
		for (String name : map.keySet())
		{
			String value=StringHelper.singleQuote(map.get(name));
			String function="get "+name+"(){return "+value+";}";
			list.add(function);
		}
		return StringHelper.join(list, "\n");
	}
}

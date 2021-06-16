package org.biobrief.util;

/**
 * User: ikka
 * LocalDate: 27.01.13
 * Time: 5:51
 * 
 * https://github.com/belgampaul/JsBeautifier/blob/master/src/be/belgampaul/tools/javascript/JsBeautify.java
 */
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

//https://github.com/beautify-web/js-beautify
//https://github.com/coveo/nashorn-commonjs-modules
public final class JsBeautifier
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(JsBeautifier.class);
	public static final String JS_BEAUTIFY = "js-beautifier.js";
	
	public static final Integer INDENT=1;

	private JsBeautifier(){}
	
	
	public static String jsBeautify(String jsCode)
	{
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		InputStream resourceAsStream = JsBeautifier.class.getResourceAsStream(JS_BEAUTIFY);
		try
		{
			Reader reader = new InputStreamReader(resourceAsStream);
			cx.evaluateReader(scope, reader, "__beautify.js", 1, null);
			reader.close();
		}
		catch (IOException e)
		{
			throw new CException("Error reading " + "beautify.js");
		}
		scope.put("jsCode", scope, jsCode);
		//String options="{indent_with_tabs: true, brace_style: \"expand\"}";
		String options="{indent_size: 1, indent_char: \"\t\", brace_style: \"expand-strict\"}";
		return (String) cx.evaluateString(scope, "js_beautify(jsCode, "+options+")", "inline", 1, null);
	}
}
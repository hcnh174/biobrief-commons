package org.biobrief.web;

import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;

import org.springframework.stereotype.Component;

import org.biobrief.util.StringHelper;
import org.biobrief.util.FileHelper;
import org.biobrief.util.CException;

@Component
public class FreemarkerService
{
	protected Configuration configuration;
	
	public Configuration getConfiguration(){return this.configuration;}
	public void setConfiguration(Configuration configuration){this.configuration=configuration;}
	
	public FreemarkerService(){}
	
	public FreemarkerService(Configuration configuration)
	{
		this.configuration=configuration;
	}
	
	/**
	 * creates formatted text by merging a hashtable with a FreeMarker template 
	 * 
	 * @param path the path to the template
	 * @param args the model data to be included in the template
	 * @return the template merged with the data model
	 */
	public String format(String path, Object... args)
	{
		try
		{
			Template template=this.configuration.getTemplate(path);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template,StringHelper.createMap(args));
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public void format(Writer out, String path, Object... args)
	{
		try
		{
			Template template=this.configuration.getTemplate(path);
			template.process(StringHelper.createMap(args), out);

		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	//http://stackoverflow.com/questions/357370/load-freemarker-templates-from-database
	public String formatStringTemplate(String str, Object... args)
	{
		try
		{
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			String name = "stringtemplate";
			stringLoader.putTemplate(name,str);
			Configuration cfg = createConfiguration();
			cfg.setTemplateLoader(stringLoader);
			Template template = cfg.getTemplate(name);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template,StringHelper.createMap(args));
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	//http://massapi.com/method/freemarker/template/DefaultObjectWrapperBuilder.build.html
	public void addEnum(Map<String,Object> model, String name, Enumeration enm)
	{
		try
		{
			//BeansWrapper wrapper=BeansWrapper.getDefaultInstance();
			DefaultObjectWrapperBuilder factory = new DefaultObjectWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			BeansWrapper wrapper=factory.build();
			TemplateHashModel enumModels=wrapper.getEnumModels();
			TemplateHashModel enumModel=(TemplateHashModel)enumModels.get(enm.getClass().getName());
			model.put(name,enumModel);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}

	
	private Configuration createConfiguration()
	{
		Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setDefaultEncoding(FileHelper.ENCODING.toString());
		return cfg;
	}
}
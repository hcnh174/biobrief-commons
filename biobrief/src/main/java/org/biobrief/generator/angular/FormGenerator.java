package org.biobrief.generator.angular;

import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.Util;
import org.biobrief.generator.angular.AngularGeneratorParams.FormGeneratorParams;
import org.biobrief.generator.templates.AbstractParams.Mode;
import org.biobrief.generator.templates.ExcelTemplate;
import org.biobrief.generator.templates.Fieldset;
import org.biobrief.generator.templates.Form;
import org.biobrief.generator.templates.FormLayout.FormParams;
import org.biobrief.generator.templates.Fragment;
import org.biobrief.generator.templates.Grid;
import org.biobrief.generator.templates.Grid.GridParams;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.biobrief.util.UnhandledCaseException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

//java -cp biobrief/build/libs/biobrief.jar org.biobrief.generator.angular.FormGenerator C:/workspace/gangenome/data/templates/forms/ekipane-forms.xlsx c:/temp
public class FormGenerator extends AbstractLayoutGenerator
{
	public static enum Suffix{form, fieldset, fragment, tabset, table}
	
	private Set<String> forms=Sets.newLinkedHashSet();// keep track of the form names

	public static void main(String[] argv)
	{
		String template=argv[0];
		String dictDir=argv[1];
		String tempDir=argv[1];
		//String srcDir=argv[2];
		//String outDir=argv[3];
		//RenderMode mode=RenderMode.valueOf(argv[4]);
		
		System.out.println("template="+template);
		System.out.println("dictDir="+dictDir);
		System.out.println("tempDir="+tempDir);
		//System.out.println("srcDir="+srcDir);
		//System.out.println("outDir="+outDir);
		////System.out.println("mode="+mode);
		
		FormGeneratorParams params=new FormGeneratorParams(template, dictDir, tempDir);
		MessageWriter out=new MessageWriter();
		generate(params, out);
	}
	
	public static void generate(FormGeneratorParams params, MessageWriter writer)
	{
		try
		{
			FormGenerator generator=new FormGenerator(params, writer);
			generator.generate(params.getTemplate());
		}
		catch (Exception e)
		{
			throw new CException(e.getMessage()+": filename="+params.getTemplate(), e);
		}
	}
	
	//////////////////////////////////////////////////////
	
	protected FormGenerator(FormGeneratorParams params, MessageWriter writer)
	{
		super(params, writer);
	}
	
	@Override
	protected void generate(Workbook workbook)
	{
		FormBuilder builder=new FormBuilder(this, workbook);
		for (String name : builder.forms.keySet())
		{
			if (forms.contains(name))
				throw new CException("found duplicate form: "+name);
			this.forms.add(name);
		}
		builder.write();//params.getMode());
	}
	
	//////////////////////////////////////////////////////
	
	public class FormBuilder
	{
		//private final FormGenerator generator;
		private final Dictionary dictionary;
		private final Map<String, Object> params;
		private final Map<String, PrimeForm> forms=Maps.newLinkedHashMap();
		private final Map<String, PrimeFieldset> fieldsets=Maps.newLinkedHashMap();
		private final Map<String, PrimeFragment> fragments=Maps.newLinkedHashMap();
		private final Map<String, PrimeTabset> tabsets=Maps.newLinkedHashMap();
		private final Map<String, PrimeTable> tables=Maps.newLinkedHashMap();
		
		//////////////////////////////////////////////////////
		
		protected FormBuilder(FormGenerator generator, Workbook workbook)
		{
			//this.generator=generator;
			this.dictionary=generator.params.getDictionary();
			this.params=loadDefaultParams(workbook);
			for (ExcelTemplate template : getTemplates(workbook))
			{
				load(template, params);
			}
		}
		
		protected boolean load(ExcelTemplate template, Map<String, Object> params)
		{
			Suffix suffix=getSuffix(template.getName());
			switch(suffix)
			{
			case form:
				return addForm(generateForm(template, params));
			case fieldset:
				return addFieldset(generateFieldset(template, params));
			case fragment:
				return addFragment(generateFragment(template, params));
			case tabset:
				return addTabset(generateTabset(template, params));
			case table:
				return addTable(generateTable(template, params));
			default:
				throw new UnhandledCaseException("unrecognized sheet name: ", template.getName());
			}
		}
		
		private Suffix getSuffix(String name)
		{
			int index=name.lastIndexOf("-");
			if (index==-1)
				return null;
			return Suffix.valueOf(name.substring(index+1));
		}
		
		//////////////////////////////////////////////
		
		protected PrimeForm generateForm(ExcelTemplate template, Map<String, Object> params)
		{
			writer.println("generating form: "+template.getName());
			Form form=new Form(template, dictionary, new FormParams(params));
			return new PrimeForm(form);
		}
		
		protected PrimeFieldset generateFieldset(ExcelTemplate template, Map<String, Object> params)
		{
			writer.println("generating fieldset: "+template.getName());
			Fieldset fieldset=new Fieldset(template, dictionary, new FormParams(params));
			return new PrimeFieldset(fieldset);
		}
		
		protected PrimeFragment generateFragment(ExcelTemplate template, Map<String, Object> params)
		{
			writer.println("generating fragment: "+template.getName());
			Fragment fragment=new Fragment(template, dictionary, new FormParams(params));
			return new PrimeFragment(fragment);
		}
		
		protected PrimeTabset generateTabset(ExcelTemplate template, Map<String, Object> params)
		{
			writer.println("generating tabset: "+template.getName());
			Fieldset fieldset=new Fieldset(template, dictionary, new FormParams(params));
			return new PrimeTabset(fieldset);
		}

		protected PrimeTable generateTable(ExcelTemplate template, Map<String, Object> params)
		{
			writer.println("generating table: "+template.getName());
			Grid grid=new Grid(template, new GridParams(params, Mode.print), dictionary);
			return new PrimeTable(grid);
		}
		
		////////////////////////////////////////////////////
		
		protected boolean addForm(PrimeForm form)
		{
			if (this.forms.containsKey(form.getName()))
				throw new CException("duplicate form name "+form.getName());
			this.forms.put(form.getName(), form);
			return true;
		}
		
		protected boolean addFieldset(PrimeFieldset fieldset)
		{
			if (this.fieldsets.containsKey(fieldset.getName()))
				throw new CException("duplicate fieldset name "+fieldset.getName());
			this.fieldsets.put(fieldset.getName(), fieldset);
			return true;
		}
		
		protected boolean addFragment(PrimeFragment fragment)
		{
			if (this.fragments.containsKey(fragment.getName()))
				throw new CException("duplicate fragment name "+fragment.getName());
			this.fragments.put(fragment.getName(), fragment);
			return true;
		}
		
		protected boolean addTabset(PrimeTabset tabset)
		{
			if (this.tabsets.containsKey(tabset.getName()))
				throw new CException("duplicate tabset name "+tabset.getName());
			this.tabsets.put(tabset.getName(), tabset);
			return true;
		}
		
		protected boolean addTable(PrimeTable table)
		{
			if (this.tables.containsKey(table.getName()))
				throw new CException("duplicate table name "+table.getName());
			this.tables.put(table.getName(), table);
			return true;
		}
		
		////////////////////////////////////////////////////////
		
		protected void write()//RenderMode mode)
		{
			for (PrimeForm form : forms.values())
			{
				write(form);
			}
		}
		
//		protected void write(PrimeForm form)
//		{
//			if (generator.params.mode==RenderMode.ANGULAR)
//				writeAngular(form);
//			else if (generator.params.mode==RenderMode.FREEMARKER)
//				writeFreemarker(form);
//			else throw new UnhandledCaseException("no handler for render mode: "+generator.params.mode);
//		}
		
		protected void write(PrimeForm form)
		{
			RenderMode mode=form.getRenderMode();
			if (mode==RenderMode.angular)
				writeAngular(form);
			else if (mode==RenderMode.freemarker)
				writeFreemarker(form);
			else throw new UnhandledCaseException("no handler for render mode: "+mode);
		}
		
		protected void writeAngular(PrimeForm form)
		{
			//String filename=this.generator.params.getOutDir()+"/"+form.getFilename();
			String filename=form.getOutfile();
			String html=render(form);
			if (!FileHelper.exists(filename))
				throw new CException("cannot find form template file: "+filename);
			writer.println("form template file: "+filename);
			String str=FileHelper.readFile(filename);
			str=Util.insertHtml(str, html, true);
			//if (overwrite)
				overwriteFile(filename, str);
			//else writeFile(Util.GENERATED_ANGULAR_DIRECTORY+"/"+form.getFilename(), str);
		}
		
		protected void writeFreemarker(PrimeForm form)
		{
			//String name=StringHelper.remove(form.getName(), "-form");
			String ftl="<#import \"_print.ftl\" as patientdb>\n";
			ftl+="<@patientdb.print>\n";
			ftl+="<#list patients as patient>\n";
			ftl+="<div class=\"break\" id=\"page${patient_index}\">\n";
			ftl+=render(form);
			ftl+="</div>\n";
			ftl+="</#list>\n";
			ftl+="</@patientdb.print>\n";
			
			//String filename=this.generator.params.getOutDir()+"/"+form.getFilename()+".ftl";
			String filename=form.getOutfile();
			writer.println("writng freemarker file: "+filename);
			FileHelper.writeFile(filename, ftl, false);//.temp/generated/print/"+name+".ftl
			//if (generator.params.overwrite)
			//	FileHelper.writeFile("src/main/resources/templates/print/"+name+".ftl", ftl, true);//FileHelper.writeFile("src/main/resources/templates/print.ftl", ftl);
			//else FileHelper.writeFile(".temp/generated/print/"+name+".ftl", ftl, true);
		}
		
		protected String render(PrimeForm form)
		{
			String html=form.render(getParams(form));
			html=replaceFieldsets(html);
			html=replaceFragments(html);
			html=replaceTabsets(html);
			html=replaceTables(html);
			return html;
		}
			
		protected String replaceFieldsets(String html)
		{
			for (PrimeFieldset fieldset : fieldsets.values())
			{
				html=replaceTag(html, fieldset);
			}
			return html;
		}
		
		protected String replaceFragments(String html)
		{
			for (PrimeFragment fragment : fragments.values())
			{
				html=replaceTag(html, fragment);
			}
			return html;
		}
		
		protected String replaceTabsets(String html)
		{
			for (PrimeTabset tabset : tabsets.values())
			{
				html=replaceTag(html, tabset);
			}
			return html;
		}
		
		protected String replaceTables(String html)
		{
			for (PrimeTable table : tables.values())
			{
				html=replaceTag(html, table);
			}
			return html;
		}

		protected String replaceTag(String html, AngularLayout layout)
		{
			String tag="[["+layout.getName()+"]]";
			if (html.contains(tag))
				html=StringHelper.replace(html, tag, "\n"+layout.render(getParams(layout)));
			return html;
		}
		
		protected RenderParams getParams(AngularLayout layout)
		{
			//System.out.println("******************************************");
			//System.out.println("rendering layout: "+layout.getName());
			return new RenderParams(layout.getRenderMode());//generator.params.mode);//, false);//layout.isLight());
		}
	}
}

package org.biobrief.generator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.dictionary.FieldDefinition;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.angular.RenderParams;
import org.biobrief.generator.templates.Style;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class Util
{
	public enum FileType
	{
		CLASS,
		SQL,
		ELASTIC,
		GRAPHQL,
		
		ANGULAR_MODEL,
		ANGULAR_SERVICE,
		
		ELASTIC_MAPPING,
		SOLR_MAPPING,
		
		SH;
	
	}
	
//	public enum FileType
//	{
//		CLASS("classes"),
//		SQL("sql"),
//		ELASTIC("elastic"),
//		GRAPHQL("graphql"),
//		
//		ANGULAR_MODEL(GENERATED_ANGULAR_DIRECTORY, "models"),
//		ANGULAR_SERVICE(GENERATED_ANGULAR_DIRECTORY, "angular/services"),
//		
//		ELASTIC_MAPPING("mappings"),
//		SOLR_MAPPING("solrmappings"),
//		
//		SH("scripts");
//				
//		private String dir;
//		
//		FileType(String basedir, String subdir)
//		{
//			this.dir=basedir+"/"+subdir;
//		}
//		
//		FileType(String subdir)
//		{
//			this(Util.GENERATED_DIRECTORY, subdir);
//		}		
//		
//		public String getDir(){return dir;}
//	}
	
	//public static final String TEMP_DIRECTORY=Constants.TEMP_DIR;
//	//public static final String BACKUP_DIRECTORY=TEMP_DIRECTORY+"/backup";
//	//public static final String GENERATED_DIRECTORY=TEMP_DIRECTORY+"/generated";
//	//public static final String GENERATED_ANGULAR_DIRECTORY="angular/.temp";
//	
////	C:\workspace\hlsg\angular\apps\hlsg\src
//	public static final String ANGULAR_DIRECTORY="angular/apps/hlsg/src";
//	//public static final String ANGULAR_DIRECTORY="angular/src";
//	public static final String ANGULAR_APP_DIRECTORY=ANGULAR_DIRECTORY+"/app";
//	//public static final String ANGULAR_MODEL_DIRECTORY=ANGULAR_APP_DIRECTORY+"/model";
//	
//	// handsontable
//	//public static final String STATIC_DIRECTORY=CoreConstants.APP_DIR+"/src/main/resources/static";
//	//public static final String JAVASCRIPT_DIRECTORY=STATIC_DIRECTORY+"/js";
//	//public static final String GENERATED_JAVASCRIPT_DIRECTORY=GENERATED_DIRECTORY+"/js";
	
	public static final String DECLARATIONS="DECLARATIONS";
	public static final String INIT="INIT";
	public static final String ACCESSORS="ACCESSORS";
	public static final String FIELDS="FIELDS";
	public static final String HTML="HTML";

//	public static void resetClassDirectory()
//	{
//		reset(FileType.CLASS);
//	}
//	
//	public static void resetElasticDirectory()
//	{
//		reset(FileType.ELASTIC);
//	}
//	
//	public static void resetGraphQLDirectory()
//	{
//		reset(FileType.GRAPHQL);
//	}
//
//	public static void resetSqlDirectory()
//	{
//		reset(FileType.SQL);
//	}
//	
//	private static void reset(FileType fileType)
//	{
//		String dir=fileType.getDir();
//		FileHelper.deleteDirectory(dir);
//		FileHelper.createDirectory(dir);
//	}
	
	public static Boolean asBoolean(String value)
	{
		return asBoolean(value, null);
	}
	
	public static Boolean asBoolean(String value, Boolean dflt)
	{
		if (!StringHelper.hasContent(value))
			return dflt;
		return value.equalsIgnoreCase("TRUE");
	}
	
	public static String nullIfEmpty(String value)
	{
		return dfltIfEmpty(value, null);
	}
	
	public static String dfltIfEmpty(String value, String dflt)
	{
		if (StringHelper.hasContent(value))
			return value;
		else return dflt;
	}
	
	///////////////////////////////////////////

	public static Map<String,String> parseParams(String str)
	{
		List<String> items=StringHelper.split(str,",",true);
		Map<String,String> params=Maps.newLinkedHashMap();
		for (String item : items)
		{
			String[] pair=item.split(":");
			String name=pair[0].trim();
			String value=pair[1].trim();
			params.put(name,value);
		}
		return params;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public static String insertHtml(String str, String replacetext, boolean required)
	{
		return insertText("HTML", str, replacetext, "<!--", "-->", required);
	}
	
	public static String insertText(EntityDefinition entityType, String label, String str, String replacetext, boolean required)
	{
		label=entityType.getName().toUpperCase()+"_"+label;
		return insertText(label, str, replacetext, "//", null, required);
	}
	
	public static String insertText(String label, String str, String replacetext, boolean required)
	{
		return insertText(label, str, replacetext, "//", required);
	}
	
	// for commments that do not need an end character
	public static String insertText(String label, String str, String replacetext, String commentChar, boolean required)
	{
		return insertText(label, str, replacetext, commentChar, null, required);
	}
	
	public static String insertText(String label, String str, String replacetext, String startCommentChar, String endCommentChar, boolean required)
	{
		endCommentChar=StringHelper.hasContent(endCommentChar) ? " "+endCommentChar : "";
		String startlabel=startCommentChar+" "+label+"_START"+endCommentChar;
		String endlabel=startCommentChar+" "+label+"_END"+endCommentChar;
		int startindex=str.indexOf(startlabel);
		if (startindex==-1)
		{
			if (required)
				throw new GeneratorException("cannot find start label: "+startlabel);//+"\n"+str.substring(0, 1000));
			else return str;
		}
		String indent=getIndent(str, startindex);
		startindex+=startlabel.length();
		int endindex=str.indexOf(endlabel, startindex);
		if (endindex==-1)
			throw new GeneratorException("cannot find end label: "+endlabel);
		replacetext=StringHelper.join(StringHelper.prefix(indent, StringHelper.split(replacetext, "\n")), "\n");
		replacetext=StringHelper.trimTrailing(replacetext);
		return str.substring(0, startindex)+"\n"+replacetext+"\n"+indent+str.substring(endindex);
	}
	
	public static String getIndent(String str, int index)
	{
		List<String> lines=StringHelper.split(str.substring(0, index), "\n");
		return StringHelper.getLast(lines);		
	}
	
//	public static String replaceFile(FileType type, String filename, String str, boolean overwrite)
//	{
//		return replaceFile(type, filename, str, overwrite, "");
//	}
//	
//	public static String replaceFile(FileType type, String filename, String str, boolean overwrite, GroupDefinition group)
//	{
//		return replaceFile(type, filename, str, overwrite, group.getName());
//	}
//	
//	public static String replaceFile(FileType type, String filename, String str, boolean overwrite, String subDir)
//	{
//		makeBackupFile(filename);
//		String outfile=getOutfile(type, filename, overwrite, subDir);
//		//log.debug("Writing file: "+filename);
//		System.out.println("writing file: "+outfile);
//		FileHelper.writeFile(outfile, str);
//		return outfile;
//	}
//	
//	private static String getOutfile(FileType type, String filename, boolean overwrite, String subDir)
//	{
//		if (overwrite)
//			return filename;
//		String dir=type.getDir();
//		if (StringHelper.hasContent(subDir))
//			dir+="/"+subDir;
//		return dir+"/"+FileHelper.stripPath(filename);
//	}
	
	public static String indent()
	{
		return StringHelper.TAB;
	}
	
//	private static void makeBackupFile(String filename)
//	{
//		//log.debug("Creating backup of file: "+filename);
//		FileHelper.createDirectory(BACKUP_DIRECTORY);
//		String bakfilename=BACKUP_DIRECTORY+"/"+FileHelper.stripPath(filename)+"."+LocalDateHelper.getTimestamp()+".bak";
//		FileHelper.copyFile(filename, bakfilename);
//	}
	
	/////////////////////////////////////////////////////
	
	public static boolean isHtml(String value)
	{
		return value.contains("<") && value.contains(">");
	}
	
	public static String renderPrimeng(Style style)
	{
		List<String> attributes=Lists.newArrayList();
		for (String attribute : style.keySet())
		{
			attributes.add("'"+attribute+"': '"+style.get(attribute)+"'");
		}
		return "{"+StringHelper.join(attributes,", ")+"}";
	}
	
	////////////////////////////////////////////////////
	
	public static String getGridTemplateCols(List<Integer> colwidths)
	{
		List<Integer> percentages=Util.convertToPercentages(colwidths);
		return StringHelper.join(StringHelper.suffix(percentages, "%"), " ");
	}
	
	public static String getGridArea(int row, int col, int rowspan, int colspan)
	{
		return ""+row+" / "+col+" / span "+rowspan+" / span "+colspan;
	}
	
	public static List<Integer> convertToPercentages(List<Integer> widths)
	{
		Integer total=widths.stream().collect(Collectors.summingInt(Integer::intValue));
		List<Integer> percentages=Lists.newArrayList();
		for (int col=0; col<widths.size(); col++)
		{
			int width=widths.get(col);
			int relwidth=Util.convertToPercentage(width, total);
			percentages.add(relwidth);
		}
		Integer sum=percentages.stream().collect(Collectors.summingInt(Integer::intValue));
		//System.out.println("sum: "+sum);
		if (sum!=100)
		{
			int index=percentages.size()-1;
			int oldvalue=percentages.get(index);
			int newvalue=oldvalue-(-1*(100-sum));
			//System.out.println("sum!=100: sum="+sum+" index="+index+" oldvalue="+oldvalue+" newvalue="+newvalue);
			percentages.set(index, newvalue);
		}
		return percentages;
	}
	
	public static Integer convertToPercentage(int width, int totalwidth)
	{
		return (int)Math.round(100.0*(double)width/(double)totalwidth);
	}
	
	public static String getRelativeWidth(int width, int totalwidth)
	{
		return convertToPercentage(width, totalwidth)+"%";
	}
	
	public static String renderHeader(RenderParams params, String title, GeneratorConstants.Icon icon)
	{
		if (params.isFreemarker())
			return renderFreemarkerHeader(title, icon);
		else if (params.isAngular())
			return renderAngularHeader(title, icon);
		else return params.noHandler();
	}
	
	public static String renderAngularHeader(String title, GeneratorConstants.Icon icon)
	{
		if (!StringHelper.hasContent(title))
			return "";
		StringBuilder buffer=new StringBuilder();
		title=renderAngularText(title);
		buffer.append("<p-header>");
		if (icon!=null)
			buffer.append(renderLargeIcon(icon));
		buffer.append(title);
		buffer.append("</p-header>\n");
		return buffer.toString();
	}
	
	public static String renderFreemarkerHeader(String title, GeneratorConstants.Icon icon)
	{
		if (!StringHelper.hasContent(title))
			return "";
		return "<h2>"+renderFreemarkerText(title)+"</h2>\n";
	}
	
	public static String renderText(RenderParams params, String value)
	{
		if (params.getMode()==RenderMode.angular)
			return renderAngularText(value);
		else if (params.getMode()==RenderMode.freemarker)
			return renderFreemarkerText(value);
		else return params.noHandler();
	}
	
	public static String renderAngularText(String value)
	{
		if (!StringHelper.hasContent(value))
			return "";
		if (!isI18n(value))
			return value;
		return "{{"+value+"}}";
	}
	
	public static String renderFreemarkerText(String value)//${hello[key]}
	{
		if (!StringHelper.hasContent(value))
			return "";
		if (!isI18n(value))
			return value;
		return "${i18n['"+value.substring(5)+"']}";
	}
	
	public static boolean isI18n(Object value)
	{
		return value.toString().startsWith("i18n.");
	}
	
	public static String renderIcon(GeneratorConstants.Icon icon)
	{
		return "<i class=\"fa "+icon.getCls()+"\"></i>";
	}
	
	public static String renderLargeIcon(GeneratorConstants.Icon icon)
	{
		return "<i class=\"fa "+icon.getCls()+" fa-lg fa-fw\"></i> ";
	}
	
	public static void checkName(String name)
	{
		if (!StringHelper.hasContent(name))
			throw new CException("no template name was specified: "+name);
	}
	
	public static String renderFreemarkerField(String path, FieldDefinition field)
	{
		switch (field.getFieldType())
		{
		case MASTER:
			if (field.isMulti())
				return "<#list "+path+" as id>${masters[id]}</#list>";//return "${"+path+"?join(', ')}";
			return "<#if "+path+"?has_content>${masters["+path+"]}</#if>";//return "${"+path+"!}";
		case ENUM:
			if (field.isMulti())
				return "${"+path+"?join(', ')}";
			return "${"+path+"!}";
		case ENTITY:
			return "${"+path+"!}";
		case BOOLEAN:
			return "${"+path+"!}";
		case DATE:
			return "<#if "+path+"??>${"+path+"?string[\"yyyy/MM/dd\"]}</#if>";
		case INTEGER:
			return "${"+path+"!}";
		case FLOAT:
			return "<#if "+path+"??>${"+path+"?string[\"0.##\"]}</#if>";//?string["0.##"]
		case STRING:
			if (field.isMulti())
				return "${"+path+"?join(', ')}";
			return "${"+path+"!}";
			//return "${"+path+"!}";
		default:
			throw new CException("no handler for field type: "+field.getFieldType());
		}
	}
	
	// converts NashPatient to nash-patient using prefix field, if set
	public static String getAngularFormName(EntityDefinition type)
	{
		String name=type.getName().toLowerCase();
		return name+"-form";
	}
	
	public static String getAngularModelName(EntityDefinition type)
	{
		String name=type.getName().toLowerCase();
		//if (!type.getPrefix().isPresent())
		return name;
		//String prefix=type.getPrefix().get().toLowerCase();
		//return name.substring(prefix.length());
	}
	
	public static String renderTag(String name, String suffix)
	{
		return StringHelper.toKebobCase(name)+"-"+suffix;
	}
	
	public static String renderNgContent()
	{
		return "<ng-content></ng-content>";
	}
	
//	public static String getProjectDir(GroupDefinition group)
//	{
//		return "../hlsg-"+group.getName();
//	}
//	
//	public static String getPackageDir(GroupDefinition group)
//	{
//		String path=Util.getProjectDir(group);
//		path+="/src/main/java";
//		path+="/org/hlsg/"+group.getName();
//		return path;
//	}
//	
//	public static String getSqlDir(GroupDefinition group)
//	{
//		if (!group.getPersistenceType().isSql())
//			return null;
//		return getProjectDir(group)+"/src/main/sql";
//	}
}

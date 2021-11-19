package org.biobrief.generator.templates;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.dictionary.EntityDefinition;
import org.biobrief.generator.templates.ExcelTemplate.CellData;
import org.biobrief.util.CException;
import org.biobrief.util.CTable;
import org.biobrief.util.ExcelHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class TemplateUtils
{	
	public static final String DEFAULTS_SHEET="defaults";
	public static final String HIDDEN_COLUMN_COLOR="rgba(216,216,216,1.0)";//second down on the left
	
	public static String getTitle(ExcelTemplate template)
	{
		return template.getCell(0,0).getStringValue().trim();// get title from first cell
	}
	
	public static String getGroup(EntityDefinition entityType)
	{
		return entityType.getGroup().getName().toLowerCase();
	}
	
	public static String formatClassName(String name, String suffix)
	{
		String path="";
		if (!name.endsWith(suffix))
			name+=suffix;
		name=StringHelper.replace(name, "-", ".");
		if (name.contains("."))
		{
			int index=name.lastIndexOf(".");
			path=name.substring(0, index)+".";
			name=name.substring(index+1);
		}
		//log.debug("path="+path+" name="+name);
		name=StringHelper.capitalizeFirstLetter(name);
		return path+StringHelper.replace(name, suffix, StringHelper.capitalizeFirstLetter(suffix));
	}

	public static boolean isControl(String value)
	{
		return StringHelper.hasContent(value) && value.contains("$");
	}
	
	public static String getValue(CellData cell)
	{
		return StringHelper.dflt(cell.getValue());
	}
	
	public static String getControlName(String value)
	{
		checkControl(value);
		if (value.contains("${"))
		{
			int start=value.indexOf("${")+2;
			int end=value.indexOf("}", start);
			return value.substring(start, end);
		}
		else return findControlName(value);
	}
	
	private static String findControlName(String rawvalue)
	{
		String value=rawvalue.substring(rawvalue.indexOf("$")+1);
		//System.out.println("rawvalue="+rawvalue+", value="+value);
		Matcher matcher = Pattern.compile("[a-z][a-zA-Z0-9]+").matcher(value);
		if (matcher.find())
			return matcher.group();
		else throw new CException("cannot find control name in string: "+rawvalue);
	}

	public static void checkControl(String body)
	{
		if (!isControl(body))
			throw new CException("control field name does not match ${name} format: "+body);
	}
		
	public static String getRoot(EntityDefinition entityType)
	{
		return entityType.getName().toLowerCase();
	}
	
	public static String getControlPath(String root, String name)
	{
		return root+"."+name;
	}
	
	public static boolean isHidden(CellData td)
	{
		//System.out.println("isHidden: bgColor="+td.bgColor+", HIDDEN_COLUMN_COLOR="+HIDDEN_COLUMN_COLOR);
		return StringHelper.isEqual(td.bgColor, HIDDEN_COLUMN_COLOR);
	}
	
	public static String getHorizontalAlignment(HorizontalAlignment hAlign)
	{
		//System.out.println("halign="+hAlign.name());
		if (hAlign==null)
			return null;
		switch(hAlign)
		{
		case CENTER:
		case CENTER_SELECTION:
			return "center";
		case LEFT:
			return "left";
		case RIGHT:
			return "right";
		case GENERAL:
		case DISTRIBUTED:
		case FILL:
		case JUSTIFY:
			return null;
		default:
			throw new CException("no handler for horizontal alignnment type: "+hAlign);	
		}
	}
	
	public static String getVerticalAlignment(VerticalAlignment vAlign)
	{
		//System.out.println("valign="+vAlign.name());
		switch(vAlign)
		{
		case CENTER:
			return "center";
		case TOP:
			return "top";
		case BOTTOM:
			return "bottom";
		case DISTRIBUTED:
		case JUSTIFY:
			return null;
		default:
			throw new CException("no handler for vertical alignnment type: "+vAlign);
		}
	}
	
	public static String generateId(String prefix)
	{
		return prefix+StringHelper.generateShortUUID();
	}
	
	///////////////////////////////////////////////////////
	
	public static Map<String,Object> parseParams(ExcelTemplate template)
	{
		CellData cell=template.findCell(0,0);
		return parseParams(cell);
	}
	
	public static Map<String,Object> parseParams(CellData cell)
	{
		try
		{
			return parseComment(cell.comment);
		}
		catch (CException e)
		{
			throw new CException("cannot parse comment in template: col="+cell.col+" row="+cell.row+" comment="+cell.comment, e);//name
		}
	}
	
	public static Map<String,Object> parseComment(String comment)
	{
		return parseParams(comment, "\n");
	}
		
	public static Map<String,Object> parseParams(String str, String delimiter)
	{
		Map<String,Object> params=Maps.newLinkedHashMap();
		if (!StringHelper.hasContent(str))
			return params;
		List<String> lines=StringHelper.splitLines(str, delimiter);
		for (int index=0; index<lines.size(); index++)
		{
			String line=lines.get(index);
			line=StringHelper.trim(line);
			if (!line.contains(":"))
				throw new CException("parameter does not contain a colon: line=["+line+"] str=["+str+"]");
			List<String> pair=StringHelper.split(line,":");
			String name=StringHelper.trim(pair.get(0));
			String value=StringHelper.trim(pair.get(1));
			params.put(name,value);
		}
		//log.debug("comments="+StringHelper.toString(params));
		return params;
	}
	
	public static Map<String, Object> loadDefaultParams(Workbook workbook)
	{
		Map<String, Object> params=Maps.newLinkedHashMap();
		Sheet sheet=workbook.getSheet(DEFAULTS_SHEET);
		if (sheet==null)
			throw new CException("no sheet named "+DEFAULTS_SHEET+" found. ");
		ExcelHelper excel=new ExcelHelper();
		CTable table=excel.extractTable(sheet);
		for (CTable.Row row : table.getRows())
		{
			String name=row.getValue(0);
			String value=row.getValue(1);
			if (name.startsWith("#"))
				continue;
			params.put(name,value);
		}
		//System.out.println("DEFAULT PARAMS: "+StringHelper.toString(params));
		return params;
	}
	
	// skips hidden sheets (_) and the default settings sheet
	public static List<ExcelTemplate> getTemplates(Workbook workbook)
	{
		List<ExcelTemplate> templates=Lists.newArrayList();
		for (int sheetnum=0; sheetnum<workbook.getNumberOfSheets(); sheetnum++)
		{
			Sheet sheet=workbook.getSheetAt(sheetnum);
			if (sheet.getSheetName().startsWith("_") || sheet.getSheetName().equals(DEFAULTS_SHEET))
				continue;
			ExcelTemplate template=ExcelTemplateParser.parse(sheet);
			templates.add(template);
		}
		return templates;
	}
	
	public static ExcelTemplate getTemplate(Workbook workbook, String name)
	{
		for (int sheetnum=0; sheetnum<workbook.getNumberOfSheets(); sheetnum++)
		{
			Sheet sheet=workbook.getSheetAt(sheetnum);
			if (sheet.getSheetName().equals(name))
				return ExcelTemplateParser.parse(sheet);
		}
		throw new CException("cannot find worksheet named: "+name);
	}
	
	
	///////////////////////////////////////
	
	private static abstract class AbstractTemplateFragment
	{
		private final String ROWDATA="rowData";
		
		protected final String template;
		protected final boolean wrapbraces;
		
		public AbstractTemplateFragment(String template, boolean wrapbraces)
		{
			//System.out.println("adding template fragment: "+template);
			this.template=template;
			this.wrapbraces=wrapbraces;
		}
		
		protected String formatFields(String value)
		{
			if (value.contains("$") && !value.contains("${"))
				return StringHelper.replace(value, "$", ROWDATA+".");
			if (!value.contains("${"))
				return value;
			int start=value.indexOf("${")+2;
			int end=value.indexOf("}", start);
			String name=value.substring(start, end);
			String target="${"+name+"}";
			String replace=ROWDATA+"."+name;
			if (wrapbraces)
				replace="{{"+replace+"}}";
			String formatted=StringHelper.replace(value, target, replace);
			return formatFields(formatted);
		}
		
		@Override
		public String toString()
		{
			return formatFields(template);
		}
	}
	
	private static class HtmlTemplateFragment extends AbstractTemplateFragment
	{
		public HtmlTemplateFragment(String template)
		{
			super(template, true);
		}
	}
	
	private static class AngularTemplateFragment extends AbstractTemplateFragment
	{
		public AngularTemplateFragment(String template)
		{
			super(template, false);
		}
		
		@Override
		public String toString()
		{
			return "{{"+formatFields(template)+"}}";
		}
	}

	//////////////////////////////////////////////
	
	//<a href="http://www.example.com/${name}">${name}</a>
	//<div class="sequence"><pre>{{formatSequence(${sequence})}}</pre></div>
	public static String formatTemplate(String html)
	{
		List<AbstractTemplateFragment> fragments=Lists.newArrayList();
		int lastindex=0;
		while (true)
		{
			int start=html.indexOf("{{", lastindex);
			if (start==-1)
			{
				fragments.add(new HtmlTemplateFragment(html.substring(lastindex)));
				break;
			}
			int end=html.indexOf("}}", start+2);
			if (end==-1)
				throw new CException("expected }} closing tag: "+html);
			fragments.add(new HtmlTemplateFragment(html.substring(lastindex,start)));
			fragments.add(new AngularTemplateFragment(html.substring(start+2,end)));
			lastindex=end+2;
		}
		return StringHelper.join(fragments, "");
	}
}
package org.biobrief.generator.angular;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.templates.ExcelTemplate;
import org.biobrief.generator.templates.Fieldset;
import org.biobrief.generator.templates.FormLayout.FormParams;
import org.biobrief.generator.templates.TemplateUtils;
import org.biobrief.util.ExcelHelper;
import org.biobrief.util.FileHelper;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestCssGrid
public class TestCssGrid
{
	private ExcelHelper excel=new ExcelHelper();
	private Dictionary dictionary=new Dictionary("../data/dictionary");
	private Workbook workbook=excel.openWorkbook("src/test/resources/forms.xlsx");
	private Map<String, Object> params=TemplateUtils.loadDefaultParams(workbook);
	
	/////////////////////////////////////////////////
	
	@Test
	public void creatFieldset()
	{
		ExcelTemplate template=getTemplate("basicinfo-fieldset");
		Fieldset fieldset=new Fieldset(template, dictionary, new FormParams(params));
		FileHelper.writeFile(".temp/fieldset.html", fieldset.getLayout());
		CssGrid cssgrid=new CssGrid(fieldset);
		FileHelper.writeFile(".temp/cssgrid.html", cssgrid.render());
	}
	
	@Test
	public void createPrimeFieldset()
	{
		ExcelTemplate template=getTemplate("basicinfo-fieldset");
		Fieldset fieldset=new Fieldset(template, dictionary, new FormParams(params));
		PrimeFieldset primefieldset=new PrimeFieldset(fieldset);
		FileHelper.writeFile(".temp/primefieldset.html", primefieldset.render());
	}
	
	///////////////////////////////////////////////////
	
	private ExcelTemplate getTemplate(String name)
	{
		return TemplateUtils.getTemplate(workbook, "basicinfo-fieldset");
	}
	
//	private void log(String message)
//	{
//		FileHelper.appendFile(".temp/logs/css-grid.txt", message);
//	}
}
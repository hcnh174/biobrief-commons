package org.biobrief.generator.angular;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractStringAssert;
import org.biobrief.generator.templates.TemplateUtils;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestTemplateUtils
public class TestTemplateUtils
{
	@Test
	public void testGetControlName()
	{
		String value="<genbank [accession]=\"$accession\"></genbank>";
		String name=TemplateUtils.getControlName(value);
		System.out.println("control name="+name);
		assertThat(name).isEqualTo("accession");
	}
	
	
	
	@Test
	public void testFormatTemplateSimple()
	{
		assertFormatted("${name}", "{{rowData.name}}");
	}
	
	@Test
	public void testFormatTemplateNoBraces()
	{
		assertFormatted("$name", "rowData.name");
	}
	
	@Test
	public void testFormatTemplateMultiple()
	{
		//<a href="http://www.example.com/${name}">${name}</a>
		assertFormatted(
			"<a href=\"http://www.example.com/${name}\">${name}</a>",
			"<a href=\"http://www.example.com/{{rowData.name}}\">{{rowData.name}}</a>");		
	}
	
	@Test
	public void testFormatTemplateMixed()
	{
		//<div class="sequence"><pre>{{formatSequence(${sequence})}}</pre></div>
		assertFormatted(
			"<div class=\"sequence\">${name}<br><pre>{{formatSequence(${sequence})}}</pre></div>", 
			"<div class=\"sequence\">{{rowData.name}}<br><pre>{{formatSequence(rowData.sequence)}}</pre></div>");
	}
	
	@Test
	public void testFormatTag()
	{
		String original="<genbank [accession]=\"$accession\"></genbank>";
		String expected="<genbank [accession]=\"rowData.accession\"></genbank>";
		String formatted=TemplateUtils.formatTemplate(original);
		System.out.println("formatted: "+formatted);
		assertThat(formatted).isEqualTo(expected);
	}
	
	////////////////////////////////
	
	private AbstractStringAssert<?> assertFormatted(String original, String formatted)
	{
		return assertThat(TemplateUtils.formatTemplate(original)).isEqualTo(formatted);
	}
}
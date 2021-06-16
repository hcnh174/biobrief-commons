package org.biobrief.util;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

//gradle --stacktrace --info :biobrief-util:test --tests *TestYamlHelper
public class TestYamlHelper
{
	@Test
	public void toYaml()
	{
		Map<String, Object> values=Maps.newLinkedHashMap();
		values.put("id", "12345'");
		values.put("name", "project1");
		values.put("title", "My project");
		values.put("description", "My project is going to change the world");
		String yaml=YamlHelper.toYaml(values);
		//String expected="{\n\tgroup: 'hirodai',\n\tmode: 'edit',\n\tentity: 'Ultrasound'\n}";
		System.out.println(yaml);
		//System.out.println(expected);
		//assertThat(json).isEqualTo(expected);
	}
}

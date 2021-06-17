package org.biobrief.util;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestEnumHelper
public class TestEnumHelper
{	
	@Test
	public void getEnumList()
	{
		List<Map<String, Object>> enums=EnumHelper.getEnumList(Constants.class);
		System.out.println("enum properties="+JsonHelper.toJson(enums));
	}	
}
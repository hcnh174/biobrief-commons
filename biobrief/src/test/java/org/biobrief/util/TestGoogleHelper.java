package org.biobrief.util;

import org.biobrief.util.DataFrame.StringDataFrame;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestGoogleHelper
public class TestGoogleHelper
{	
	@Test
	public void testSomething() throws Exception
	{
		System.out.println("testing something");
		GoogleHelper.Query query=new GoogleHelper.Query();
		query.setCredentialsFile("c:/workspace/sheets/src/main/resources/credentials.json");
		query.setTokensDir("c:/workspace/sheets/tokens");
		query.setSpreadsheetId("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms");
		query.setRange("Class Data!A2:E");
		StringDataFrame dataframe=GoogleHelper.loadSpreadsheet(query);
		System.out.println("sample data:");
		System.out.println(dataframe.toString());
		//assertThat("google").endsWith("gle");
	}
}

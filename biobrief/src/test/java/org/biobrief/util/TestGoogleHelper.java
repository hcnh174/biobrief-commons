package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

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
		GoogleHelper.loadSpreadsheet(query);
		//assertThat("google").endsWith("gle");
	}
}

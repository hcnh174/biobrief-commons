package org.biobrief.services;

import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.GoogleHelper;
import org.biobrief.util.GoogleHelper.GoogleProperties;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RuntimeHelper;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestGoogleService
public class TestGoogleService
{	
	@Test
	public void loadSpreadsheet() throws Exception
	{
		System.out.println("loading sample worksheet");
		GoogleProperties properties=new GoogleProperties();
		properties.setCredentialsFile(RuntimeHelper.getEnvironmentVariable("GOOGLE_CREDENTIALS_FILE", true));
		properties.setTokensDir(RuntimeHelper.getEnvironmentVariable("GOOGLE_TOKENS_DIR", true));
		
		String spreadsheetId="1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
		String range="Class Data!A2:E";
		GoogleHelper.GoogleSheetsQuery query=new GoogleHelper.GoogleSheetsQuery(spreadsheetId, range);
		
		GoogleService service=new GoogleService(properties);
		MessageWriter out=new MessageWriter();
		StringDataFrame dataframe=service.loadSpreadsheet(query, out);
		System.out.println("sample data:");
		System.out.println(dataframe.toString());
		//assertThat("google").endsWith("gle");
	}
}

package org.biobrief.util;

import org.biobrief.util.GoogleHelper.GoogleProperties;
import org.biobrief.util.GoogleHelper.GoogleSheetsRange;
import org.junit.jupiter.api.Test;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;

//gradle --stacktrace --info test --tests *TestGoogleHelper
public class TestGoogleHelper
{	
	//@Test
	public void testSomething() throws Exception
	{
//		System.out.println("testing something");
//		GoogleHelper.Query query=new GoogleHelper.Query();
////		query.setCredentialsFile("c:/workspace/sheets/src/main/resources/credentials.json");
////		query.setTokensDir("c:/workspace/sheets/tokens");
////		query.setSpreadsheetId("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms");
////		query.setRange("Class Data!A2:E");
//		query.setCredentialsFile(RuntimeHelper.getEnvironmentVariable("GOOGLE_CREDENTIALS_FILE", true));
//		query.setTokensDir(RuntimeHelper.getEnvironmentVariable("GOOGLE_TOKENS_DIR", true));
//		query.setSpreadsheetId("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms");
//		query.setRange("Class Data!A2:E");
//		
//		StringDataFrame dataframe=GoogleHelper.loadSpreadsheet(query);
//		System.out.println("sample data:");
//		System.out.println(dataframe.toString());
//		//assertThat("google").endsWith("gle");
	}
	
	//@Test
	public void clearSheet() throws Exception
	{
		GoogleProperties properties=new GoogleProperties();
		properties.setCredentialsFile(RuntimeHelper.getEnvironmentVariable("GOOGLE_CREDENTIALS_FILE", true));
		properties.setTokensDir(RuntimeHelper.getEnvironmentVariable("GOOGLE_TOKENS_DIR", true));
		
		String spreadsheetId="15JtHO-JzSicjwbBb1IdGpfkl8LdM_rKnukYH990xsoA";// variant counts
		String range="variants by cancer type!A:D";
		ClearValuesRequest requestBody = new ClearValuesRequest();		
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		Sheets.Spreadsheets.Values.Clear request = service.spreadsheets().values().clear(spreadsheetId, range, requestBody);
		ClearValuesResponse response = request.execute();
		System.out.println(response);
		
//		BatchClearValuesRequest requestBody = new BatchClearValuesRequest();
//		requestBody.setRanges(Lists.newArrayList(range));
//		
//		Sheets service=GoogleHelper.openSpreadsheet(properties);
//		Sheets.Spreadsheets.Values.BatchClear request = service.spreadsheets().values().batchClear(spreadsheetId, requestBody);
//		
//		BatchClearValuesResponse response = request.execute();
//		System.out.println(response);
		
////		Cancer type	Gene	Variant	Count
////		前立腺癌	NBN	NBN NBN Amplification	6
//		GoogleService service=new GoogleService(properties);
//		
//		
//		GoogleService service=new GoogleService(properties);
//		MessageWriter out=new MessageWriter();
//		StringDataFrame dataframe=service.loadSpreadsheet(query, out);
//		System.out.println("sample data:");
//		System.out.println(dataframe.toString());
		//assertThat("google").endsWith("gle");
	}
	
	@Test
	public void writeTable() throws Exception
	{
		GoogleProperties properties=new GoogleProperties();
		properties.setCredentialsFile(RuntimeHelper.getEnvironmentVariable("GOOGLE_CREDENTIALS_FILE", true));
		properties.setTokensDir(RuntimeHelper.getEnvironmentVariable("GOOGLE_TOKENS_DIR", true));
		
		GoogleSheetsRange range=new GoogleSheetsRange("15JtHO-JzSicjwbBb1IdGpfkl8LdM_rKnukYH990xsoA", "variants by cancer type!A:D");
		
		CTable table=new CTable();
		table.addHeader("Cancer type");
		table.addHeader("Gene");
		table.addHeader("Variant");
		table.addHeader("Count");
		
		CTable.Row row=table.addRow();
		row.add("前立腺癌");
		row.add("NBN");
		row.add("NBN Amplification");
		row.add(6);
		
		row=table.addRow();
		row.add("前立腺癌");
		row.add("RAD21");
		row.add("RAD21 Amplification");
		row.add(5);
		
		row=table.addRow();
		row.add("肝内胆管癌、肝内転移、リンパ節転移、肺転移");
		row.add("FGFR2");
		row.add("FGFR2 fusion");
		row.add(4);
		
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		GoogleHelper.clearRange(service, range);
		GoogleHelper.writeTable(service, range, table);
		
		 // How the input data should be interpreted.
//		String valueInputOption = "USER_ENTERED"; // RAW
//
//		// How the input data should be inserted.
//		String insertDataOption = "OVERWRITE"; // INSERT
//
//		ValueRange requestBody = new ValueRange();
//		requestBody.setRange(range.getRange());
//		requestBody.setMajorDimension("ROWS");
//		requestBody.setValues(GoogleHelper.getValues(table));
//
//		Sheets service=GoogleHelper.openSpreadsheet(properties);
//		Sheets.Spreadsheets.Values.Append request =
//			service.spreadsheets().values().append(range.getSpreadsheetId(), range.getRange(), requestBody);
//		request.setValueInputOption(valueInputOption);
//		request.setInsertDataOption(insertDataOption);
//
//		AppendValuesResponse response = request.execute();
	}
}

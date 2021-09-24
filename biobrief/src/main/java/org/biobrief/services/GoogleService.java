package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CTable;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.GoogleHelper;
import org.biobrief.util.GoogleHelper.GoogleProperties;
import org.biobrief.util.GoogleHelper.GoogleSheetsRange;
import org.biobrief.util.MessageWriter;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.Sheets;

@Component
public class GoogleService
{
	protected final GoogleProperties properties;
	
	public GoogleService(GoogleProperties properties)
	{
		this.properties=properties;
	}
	
	public StringDataFrame readSpreadsheet(GoogleSheetsRange range, MessageWriter out)
	{
		out.println("opening spreadsheet: spreadsheetId="+range.getSpreadsheetId()+" range="+range.getRange());
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		List<List<Object>> values=GoogleHelper.readSpreadsheet(service, range);
		return GoogleHelper.createDataFrame(values);
	}
	
	public void clearRange(GoogleSheetsRange range, MessageWriter out)
	{
		out.println("clear range: spreadsheetId="+range.getSpreadsheetId()+" range="+range.getRange());
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		GoogleHelper.clearRange(service, range);
	}
	
	public void writeTable(GoogleSheetsRange range, CTable table, MessageWriter out)
	{
		out.println("writing table: spreadsheetId="+range.getSpreadsheetId()+" range="+range.getRange());
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		GoogleHelper.writeTable(service, range, table);
	}
}

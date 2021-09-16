package org.biobrief.services;

import java.util.List;

import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.GoogleHelper;
import org.biobrief.util.GoogleHelper.GoogleProperties;
import org.biobrief.util.GoogleHelper.GoogleSheetsQuery;
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
	
	public StringDataFrame loadSpreadsheet(GoogleProperties properties, GoogleSheetsQuery query)
	{
		Sheets service=GoogleHelper.openSpreadsheet(properties);
		List<List<Object>> values=GoogleHelper.querySpreadsheet(service, query);
		return GoogleHelper.createDataFrame(values);
	}
}

package org.biobrief.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.biobrief.util.DataFrame.StringDataFrame;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;

import lombok.Data;

//https://developers.google.com/sheets/api/quickstart/java
public final class GoogleHelper
{
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	
//	public static StringDataFrame loadSpreadsheet(GoogleProperties properties, GoogleSheetsQuery query)
//	{
//		Sheets service=openSpreadsheet(properties);
//		List<List<Object>> values = querySpreadsheet(service, query);
//		return createDataFrame(values);
//	}
	
	public static StringDataFrame createDataFrame(List<List<Object>> values)
	{
		List<String> colnames=Lists.newArrayList();
		for (Object value : values.get(0))
		{
			colnames.add(value.toString());
		}
		StringDataFrame dataframe=new StringDataFrame();
		for (String colname : colnames)
		{
			dataframe.addColumn(colname);
		}
		int rownum=1;
		for (List<Object> row : StringHelper.subList(values, 1))
		{
			String rowname=""+(rownum++);
			dataframe.addRow(rowname);
			for (int col=0; col<colnames.size(); col++)
			{
				String colname=colnames.get(col);
				Object value=row.get(col);
				dataframe.setValue(colname, rowname, value);
			}
		}
		return dataframe;
	}
	
	public static List<List<Object>> querySpreadsheet(Sheets service, GoogleSheetsQuery query)
	{
		try
		{
			ValueRange response = service.spreadsheets()
					.values().get(query.getSpreadsheetId(), query.getRange()).execute();
			List<List<Object>> values = response.getValues();
			return values;
		}
		catch(IOException e)
		{
			throw new CException();
		}
	}
	
	// Build a new authorized API client service.
	public static Sheets openSpreadsheet(GoogleProperties props)
	{
		try
		{
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Credential credentials=getCredentials(props, HTTP_TRANSPORT);
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
					.setApplicationName(props.getApplicationName())
					.build();
			return service;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final GoogleProperties props, final NetHttpTransport HTTP_TRANSPORT)
			//throws IOException
	{
		try
		{
			InputStream in = FileHelper.openFileInputStream(props.getCredentialsFile());
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, 
					new InputStreamReader(in));
	
			// Build flow and trigger user authorization request.
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(props.getTokensDir())))
					.setAccessType("offline")
					.build();
			LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(props.getPort()).build();
			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	@Data
	public static class GoogleProperties
	{
		protected String applicationName="biobrief";
		protected String credentialsFile;
		protected String tokensDir;
		protected Integer port=9876;//8888
	}
	
	@Data
	public static class GoogleSheetsQuery
	{
		protected String spreadsheetId;
		protected String range;
		
		public GoogleSheetsQuery(String spreadsheetId, String range)
		{
			this.spreadsheetId=spreadsheetId;
			this.range=range;
		}
	}
}
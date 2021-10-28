package org.biobrief.web;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestWebHelper
public class TestWebHelper
{
	@Test
	public void getLastModifiedDate()
	{
		String url="https://upload.umin.ac.jp/ctr_csv/ctr_data_j.csv.gz";
		WebHelper.getLastModifiedDate(url);
	}
}
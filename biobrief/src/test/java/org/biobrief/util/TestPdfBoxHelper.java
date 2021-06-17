package org.biobrief.util;

import org.junit.jupiter.api.Test;

//mvn -Dtest=TestPdfBoxHelper test
//gradle test --tests *TestPdfBoxHelper
public class TestPdfBoxHelper
{
	//@Test
	public void extractTextFromPdf()
	{
		PdfBoxHelper.extractText("c:/temp/test.pdf", "c:/temp/test.txt");
	}
	
	//@Test
	public void extractImagesFromPdf()
	{
		PdfBoxHelper.extractImages("c:/temp/test.pdf", "c:/temp");
	}
}
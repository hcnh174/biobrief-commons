package org.biobrief.util;

import org.junit.jupiter.api.Test;

//mvn -Dtest=TestPdfBoxHelper test
//gradle :biobrief-util:test --tests *TestPdfBoxHelper
public class TestPdfBoxHelper
{
	@Test
	public void extractTextFromPdf()
	{
		PdfBoxHelper.extractText("d:/projects/patientdb.etc/NASH/sambuichiCS20140228.pdf", "d:/temp/sambuichiCS20140228.txt");
	}
	
	@Test
	public void extractImagesFromPdf()
	{
		PdfBoxHelper.extractImages("d:/projects/patientdb.etc/NASH/sambuichiCS20140228.pdf", "d:/temp/sambuichiCS20140228");
	}
}
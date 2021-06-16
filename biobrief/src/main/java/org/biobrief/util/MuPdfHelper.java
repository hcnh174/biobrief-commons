package org.biobrief.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuPdfHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(MuPdfHelper.class);	
	//private static final String MUPDF_DIR="C:/packages/mupdf-1.17.0-windows";
	private static final String MUPDF_DIR="C:/packages/mupdf-1.18.0-windows";
	
	public static int convertXpsToPdf(String infile, String outfile)
	{
		System.out.println("xpsfile="+infile);
		FileHelper.checkExists(infile);
		String command=MUPDF_DIR+"/mutool draw -o "+outfile+" "+infile;
		int exitcode=CCommandLine.execute(command);
		FileHelper.checkExists(outfile);
		return exitcode;
	}
	
	public static int convertPdfToHtml(String infile, String outfile, String password)
	{
		System.out.println("pdffile="+infile);
		FileHelper.checkExists(infile);
		String command=MUPDF_DIR+"/mutool draw -p "+password+" -o "+outfile+" "+infile;
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("mutool draw exited with non-zero exit code: "+exitcode);
		FileHelper.checkExists(outfile);
		return exitcode;
	}
}
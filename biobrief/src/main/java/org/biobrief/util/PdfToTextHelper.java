package org.biobrief.util;

//https://www.xpdfreader.com/pdftotext-man.html
//pdftotext -enc UTF-8 -upw password -layout /mnt/work/pdf/c-cat.pdf /mnt/work/pdf/c-cat.txt
public class PdfToTextHelper
{
	public static int convertPdfToText(String infile, String outfile, String password)
	{
		System.out.println("pdffile="+infile);
		StringHelper.checkHasContent(password, "the supplied password is null or empty: "+password);
		FileHelper.checkExists(infile);
		String command=Constants.SCRIPTS_DIR+"/run_pdftotext.sh --pdffile "+infile+" --password "+password+" --textfile "+outfile;
		return execute(command, outfile);
	}
	
	public static int convertPdfToText(String infile, String outfile)
	{
		System.out.println("pdffile="+infile);
		FileHelper.checkExists(infile);
		String command=Constants.SCRIPTS_DIR+"/run_pdftotext.sh --pdffile "+infile+" --textfile "+outfile;
		return execute(command, outfile);
	}
	
	private static int execute(String command, String outfile)
	{
		System.out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("pdftotext exited with non-zero exit code: "+exitcode);
		FileHelper.checkExists(outfile);
		return exitcode;
	}
}

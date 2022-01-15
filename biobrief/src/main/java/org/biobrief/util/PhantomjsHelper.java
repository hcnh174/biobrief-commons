package org.biobrief.util;

//https://www.xpdfreader.com/pdftotext-man.html
//pdftotext -enc UTF-8 -upw password -layout /mnt/work/pdf/c-cat.pdf /mnt/work/pdf/c-cat.txt
public class PhantomjsHelper
{
	public static int convertHtmlToPdf(String htmlfile, String pdffile)
	{
		System.out.println("htmlfile="+htmlfile);
		FileHelper.checkExists(htmlfile);
		String command=Constants.SCRIPTS_DIR+"/run_phantomjs.sh --htmlfile "+htmlfile+" --pdffile "+pdffile;
		return execute(command, pdffile);
	}
	
	private static int execute(String command, String pdffile)
	{
		System.out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("phantomjs exited with non-zero exit code: "+exitcode);
		FileHelper.checkExists(pdffile);
		return exitcode;
	}
}

package org.biobrief.util;

//run_phantomjs.sh -htmlfile /mnt/out/phantomjs/test.html --pdffile /mnt/out/phantomjs/test.pdf
public class PhantomjsHelper
{
	public static int convertHtmlToPdf(String htmlfile, String pdffile)
	{
		System.out.println("htmlfile="+htmlfile);
		FileHelper.checkExists(htmlfile);
		String command=Constants.SCRIPTS_DIR+"/run_phantomjs.sh --htmlfile "+htmlfile+" --pdffile "+pdffile;
		System.out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("phantomjs exited with non-zero exit code: "+exitcode);
		FileHelper.checkExists(pdffile);
		return exitcode;
	}
}

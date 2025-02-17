package org.biobrief.util;

// run_puppeteer_html_to_pdf.sh --htmlfile /mnt/out/temp/Z401614157127_F1/Z401614157127_F1_trial_report.html --pdffile /mnt/out/temp/Z401614157127_F1/Z401614157127_F1_trial_report.pdf
public class PuppeteerHelper
{
	public static int convertHtmlToPdf(String htmlfile, String pdffile)
	{
		System.out.println("htmlfile="+htmlfile);
		FileHelper.checkExists(htmlfile);
		String command=Constants.SCRIPTS_DIR+"/run_puppeteer_html_to_pdf.sh --htmlfile "+htmlfile+" --pdffile "+pdffile;
		System.out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("_puppeteer exited with non-zero exit code: "+exitcode);
		FileHelper.checkExists(pdffile);
		return exitcode;
	}
}

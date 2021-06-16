package org.biobrief.util;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

//https://stackoverflow.com/questions/4861400/need-to-convert-emf-to-jpeg-png-file-formats-using-java
//https://imagemagick.org/script/download.php
//install legacy utilities
//C:\Program Files\ImageMagick-7.0.10-Q16-HDRI
//String myPath="C:\\Program Files\\ImageMagick-7.0.10-Q16-HDRI";
//https://www.amazon.com/ImageMagick-Tricks-Unleash-friendly-tutorial/dp/1904811868?SubscriptionId=1K6EGPGKK5C3SN1X67R2&tag=2c68ca2-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=1904811868
//https://www.amazon.com/Definitive-Guide-ImageMagick-Michael-Still/dp/1590595904?SubscriptionId=1K6EGPGKK5C3SN1X67R2&tag=2c68ca2-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=1590595904
public final class ImageMagickHelper
{	
	//public static final String IMAGE_MAGICK_HOME="C:\\Program Files\\ImageMagick-7.0.10-Q16-HDRI";
	private static final String IMAGE_MAGICK_HOME=RuntimeHelper.getEnvironmentVariable("IMAGE_MAGICK_HOME", true);
	
	////////////////////////////////////////////////////////////////

	public static String convertEmfToJpg(String infile)
	{
		if (!infile.endsWith(".emf"))
			throw new CException("expected infile to end with .emf extension: "+infile);
		String outfile=StringHelper.replace(infile, ".emf", ".jpg");
		convertEmfToJpg(IMAGE_MAGICK_HOME, infile, outfile);
		return outfile;
	}
	
	public static void convertEmfToJpg(String path, String infile, String outfile) //throws InterruptedException, IOException, IM4JavaException {
	{
		if (!infile.endsWith(".emf"))
			throw new CException("expected infile to end with .emf extension: "+infile);
		if (!outfile.endsWith(".jpg"))
			throw new CException("expected outfile to end with .jpg extension: "+outfile);
		FileHelper.checkExists(infile);
		if (FileHelper.exists(outfile))
			FileHelper.deleteFile(outfile);		
		try
		{
			ProcessStarter.setGlobalSearchPath(path);
			IMOperation img=new IMOperation();
			img.addImage();
			img.addImage();
			ConvertCmd convert=new ConvertCmd();
			convert.run(img,new Object[]{infile, outfile});
		}
		catch(Exception e)
		{
			throw new CException("failed to convert emf file to jpg");
		}
		FileHelper.checkExists(outfile);
	}
}

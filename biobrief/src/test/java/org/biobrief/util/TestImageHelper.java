package org.biobrief.util;

import java.awt.Insets;
import java.awt.image.BufferedImage;

import org.biobrief.util.ImageHelper.ScaleDirection;
import org.junit.jupiter.api.Test;

//gradle :biobrief-util:test --stacktrace --info --tests *TestImageHelper
public class TestImageHelper
{
	//@Test
	//https://www.programcreek.com/java-api-examples/?api=java.awt.image.ImageFilter
	public void cropImage()
	{
		String filename=Constants.TEMP_DIR+"/ppt/images/PP_HU20190034_1.png";
		BufferedImage image=ImageHelper.readImage(filename);
		Insets insets=new Insets(100, 40, 200, 500);
		BufferedImage cropped=ImageHelper.cropImage(image, insets);
		String croppedfilename=Constants.TMP_DIR+"/cropped.png";
		ImageHelper.writeImage(cropped, croppedfilename);
	}
	
//	@Test
//	//https://www.programcreek.com/java-api-examples/?api=java.awt.image.ImageFilter
//	public void cropImage()
//	{
//		String filename=Constants.TEMP_DIR+"/ppt/images/PP_HU20190034_1.png";
//		BufferedImage image=ImageHelper.readImage(filename);
//		int x=50, y=50, width=100, height=100;
//		ImageFilter filter = new CropImageFilter(x, y, width,height);
//		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
//		Image cropped=Toolkit.getDefaultToolkit().createImage(producer);
//		BufferedImage bcropped=ImageHelper.toBufferedImage(cropped);
//		String croppedfilename=Constants.TMP_DIR+"/cropped.png";
//		ImageHelper.writeImage(bcropped, ImageHelper.Format.png, croppedfilename);
//	}
	
	//@Test
	public void createThumbnail()
	{
		String filename=Constants.TEMP_DIR+"/ppt/images/PP_HU20190034_1.png";
		String thumbnail=ImageHelper.createThumbnail(filename, 100, ScaleDirection.HEIGHT);
		System.out.println("created thumbnail: "+thumbnail);
	}
	
	//@Test
	public void convertEmfToJpg()
	{
		//String path="C:\\Program Files\\ImageMagick-7.0.10-Q16-HDRI";
		String infile=Constants.TEMP_DIR+"/expertpanel/C001202899262/PP_C001202899262-EC00045870_3.emf";
		//String outfile=Constants.TEMP_DIR+"/expertpanel/C001202899262/PP_C001202899262-EC00045870_3.jpg";
		//ImageHelper.convertEmfToJpg(path, infile, outfile);
		String outfile=ImageHelper.convertEmfToJpg(infile);
		System.out.println("outfile: "+outfile);
	}

//	@Test
//	public void scaleImage()
//	{
//		String filename=Constants.TEMP_DIR+"/ppt/images/PP_HU20190034_1.png";
//		BufferedImage image=ImageHelper.readImage(filename);
//		BufferedImage thumbnail=ImageHelper.scaleImage(image, 100);
//		String path=FileHelper.stripFilename(filename);
//		String prefix=FileHelper.getRoot(filename);
//		String suffix=FileHelper.getSuffix(filename);
//		String thumbnailfilename=path+"/"+prefix+"_thumbnail"+suffix;
//		ImageHelper.Format format=ImageHelper.Format.find(suffix);
//		ImageHelper.writeImage(thumbnail, format, thumbnailfilename);
//	}
}
package org.biobrief.util;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public final class ImageHelper
{	
	public enum ScaleType{SCALE_TO_FIT, SCALE_TO_FILL};
	public enum ScaleDirection{WIDTH, HEIGHT, MAX};
	
	public static final Integer DEFAULT_THUMBNAIL_SIZE=100;
	public static final ScaleDirection DEFAULT_THUMBNAIL_SCALE_DIRECTION=ScaleDirection.HEIGHT;
	
	public static final String IMAGE_MAGICK_HOME="C:\\Program Files\\ImageMagick-7.0.10-Q16-HDRI";
	
	public enum Format
	{
		jpg,
		jpeg,
		gif,
		png,
		emf;
		
		public static Format find(String filename)
		{
			String suffix=FileHelper.getSuffix(filename);
			String extension=StringHelper.replace(suffix, ".", "").toLowerCase();
			return Format.valueOf(extension);
		}
	};

	private ImageHelper(){}
	
	public static BufferedImage readImage(String filename)
	{
		try
		{
			FileHelper.checkExists(filename);
			return ImageIO.read(new File(filename));
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}

	public static void writeImage(BufferedImage image, String filename)
	{
		writeImage(image, Format.find(filename), filename);
	}
	
	public static void writeImage(BufferedImage image, Format format, String filename)
	{
		try
		{
			ImageIO.write(image, format.name(), new File(filename));
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static void writeImage(BufferedImage image, Format format, OutputStream stream)
	{
		try
		{
			ImageIO.write(image, format.name(), stream);
			stream.flush();
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static void writeImage(byte[] image, OutputStream stream)
	{
		try
		{
			stream.write(image);
			stream.flush();
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	////////////////////////////////////////////////////////////
	
	public static String createThumbnail(String filename)
	{
		return createThumbnail(filename, DEFAULT_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SCALE_DIRECTION);
	}

	public static BufferedImage createThumbnail(BufferedImage image)
	{
		return ImageHelper.scaleImage(image, DEFAULT_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SCALE_DIRECTION);
	}
	
	public static String createThumbnail(String filename, int max, ScaleDirection dir)
	{
		BufferedImage image=ImageHelper.readImage(filename);
		BufferedImage thumbnail=ImageHelper.scaleImage(image, max, dir);
		String path=FileHelper.stripFilename(filename);
		String prefix=FileHelper.getRoot(filename);
		String suffix=FileHelper.getSuffix(filename);
		String thumbnailfilename=path+"/"+prefix+"_thumbnail"+suffix;
		//ImageHelper.Format format=ImageHelper.Format.find(filename);
		ImageHelper.writeImage(thumbnail, thumbnailfilename);
		return thumbnailfilename;
	}
	
	public static BufferedImage cropImage(BufferedImage image, Insets insets)
	{
		int x=insets.left;
		int y=insets.top;
		int width=insets.right-insets.left;
		int height=insets.bottom-insets.top;
		return cropImage(image, x, y, width, height);
	}
	
	public static BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height)
	{
		ImageFilter filter = new CropImageFilter(x, y, width,height);
		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
		Image cropped=Toolkit.getDefaultToolkit().createImage(producer);
		return ImageHelper.toBufferedImage(cropped);
	}
	
	////////////////////////////////////////////////////////	
	
	public static BufferedImage getImage(String path, MessageWriter out)
	{
		try
		{
			out.println("loading image from: "+path);
			URL url=new URL(path);
			return ImageIO.read(url);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static void displayImage(Image image)
	{
		JFrame frame = new JFrame();
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel label = new JLabel(new ImageIcon(image));
	    frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	}
	
	public static BufferedImage createImage(Container container, int width, int height)
	{
		//System.setProperty("java.awt.headless","true");
		container.setSize(new Dimension(width,height));
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		container.paint(graphics);
		graphics.dispose();
		return bufferedImage;
	}
	
	public static BufferedImage createImage(Canvas canvas, int width, int height)
	{
		System.setProperty("java.awt.headless","true");
		canvas.setSize(new Dimension(width,height));
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		canvas.paint(graphics);
		graphics.dispose();
		return bufferedImage;
	}
	
	public static BufferedImage createImage(int width, int height, Color color)
	{
		BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return image;
	}
	
	public static BufferedImage scaleImage(BufferedImage image, int max, ScaleDirection dir)
	{
		if (image==null)
			throw new CException("image is null");
		
		float width=(float)image.getWidth();
		float height=(float)image.getHeight();
		float scale=getScalingFactor(width, height, max, dir);

		float scaledwidth=width/scale;
		float scaledheight=height/scale;
		
		if (scaledwidth==0) scaledwidth=1;
		if (scaledheight==0) scaledheight=1;

		return scaleImage(image, (int)scaledwidth, (int)scaledheight, ScaleType.SCALE_TO_FIT);
	}
	
	private static float getScalingFactor(float width, float height, int max, ScaleDirection dir)
	{
		if (dir==ScaleDirection.HEIGHT)
			return ((float)height)/(float)max;
		else if (dir==ScaleDirection.WIDTH)
			return ((float)width)/(float)max;
		else if (width>height)
			return ((float)width)/(float)max;
		else return ((float)height)/(float)max;
			
	}
	
	public static Dimension scaleImage(int width, int height, int max)
	{
		float scale=1.0f;
		if (width>height)
			scale=((float)width)/(float)max;
		else scale=((float)height)/(float)max;
		int thumb_width=Math.round(width/scale);
		int thumb_height=Math.round(height/scale);		
		return new Dimension(thumb_width,thumb_height);
	}
	
	public static BufferedImage scaleImage(BufferedImage src, int width, int height, ScaleType scaletype)
	{
		if (src.getWidth()==width && src.getHeight()==height)
			return src;
		int type = BufferedImage.TYPE_INT_RGB;
		BufferedImage dest = new BufferedImage(width, height, type);
		Graphics2D g2 = dest.createGraphics();
		if(scaletype == ScaleType.SCALE_TO_FIT)
		{
		    g2.setBackground(UIManager.getColor("Panel.background"));
		    g2.clearRect(0,0,width,height);
		}
		double scale = getScale(src,width,height,scaletype);
		double x = (width - scale*src.getWidth())/2;
		double y = (height - scale*src.getHeight())/2;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.scale(scale, scale);
		g2.drawRenderedImage(src, at);
		g2.dispose();
		return dest;
	}
	
	
	private static double getScale(BufferedImage image, int width, int height, ScaleType scaletype)
	{
		double xScale = (double)width/image.getWidth();
		double yScale = (double)height/image.getHeight();
		return (scaletype == ScaleType.SCALE_TO_FIT) ? Math.min(xScale, yScale) : Math.max(xScale, yScale);
	}
	
	public static BufferedImage scaleImage(BufferedImage src, int width, int height)
	{
		int type = BufferedImage.TYPE_INT_RGB;
		BufferedImage dest = new BufferedImage(width, height, type);
		Graphics2D g2 = dest.createGraphics();
		double xScale = (double)width/src.getWidth();
		double yScale = (double)height/src.getHeight();
		double x = (width - xScale*src.getWidth())/2;
		double y = (height - yScale*src.getHeight())/2;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.scale(xScale, yScale);
		g2.drawRenderedImage(src, at);
		g2.dispose();
		return dest;
	}
	
	public static byte[] createArray(BufferedImage image, Format format)
	{
		try
		{
			ByteArrayOutputStream stream=new ByteArrayOutputStream();
			ImageIO.write(image, format.name(), stream);
			return stream.toByteArray();
		}
		catch(IOException e)
		{
			throw new CException(e);
		}		
	}
	
	//https://stackoverflow.com/questions/4251383/how-to-convert-bufferedimage-to-inputstream/21569243
	public static InputStream createInputStream(BufferedImage image, Format format)
	{
		return new ByteArrayInputStream(createArray(image, format));
	}
	
	/** Tell system to use native look and feel, as in previous
	*  releases. Metal (Java) LAF is the default otherwise. */
	public static void setNativeLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			System.err.println("Error setting native LAF: " + e);
		}
	}
	
	/** A simplified way to see a JPanel or other Container.
	*  Pops up a JFrame with specified Container as the content pane. */
	public static JFrame openInJFrame(Container content, int width, int height, String title, Color bgColor)
	{
		JFrame frame = new JFrame(title);
		frame.setBackground(bgColor);
		content.setBackground(bgColor);
		frame.setSize(width, height);
		frame.setContentPane(content);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				System.exit(0);
			}
		});
		frame.setVisible(true);
		return frame;
	}
	
	/** Uses Color.white as the background color. */
	public static JFrame openInJFrame(Container content, int width, int height, String title)
	{
		return openInJFrame(content, width, height, title, Color.white);
	}
	
	/** Uses Color.white as the background color, and the
	*  name of the Container's class as the JFrame title. */
	public static JFrame openInJFrame(Container content, int width, int height)
	{
		return openInJFrame(content,width,height,content.getClass().getName(),Color.white);
	}
	
	
	//https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
			return (BufferedImage) img;
		
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		// Return the buffered image
		return bimage;
	}
	
	////////////////////////////////////////////////////////////////

	public static String convertEmfToJpg(String infile)
	{
		return ImageMagickHelper.convertEmfToJpg(infile);
	}
}

/*
public static void writeImage(String svg, String filename)
{
	try
	{
		Reader reader=new StringReader(svg);
		OutputStream out=new FileOutputStream(filename);
		BufferedOutputStream bout=new BufferedOutputStream(out);
		PNGTranscoder transcoder=new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(reader);
        TranscoderOutput output=new TranscoderOutput(bout);
        transcoder.transcode(input,output);
	}
	catch (Exception e)
	{
		throw new CException(e);
	}
}

public static byte[] renderSvg(String svg)
{
	try
	{
		Reader reader=new StringReader(svg);
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		PNGTranscoder transcoder=new PNGTranscoder();
		//KEY_FORCE_TRANSPARENT_WHITE;
		//transcoder.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE,true);
        TranscoderInput input = new TranscoderInput(reader);
        TranscoderOutput output=new TranscoderOutput(stream);
        transcoder.transcode(input,output);
        return stream.toByteArray();
	}
	catch (Exception e)
	{
		if (CPlatformType.find().isWindows())
			CFileHelper.writeFile("d:/temp/error.svg",svg);
		throw new CException(e);//svg,
	}
}

public static String createSvg(SVGGraphics2D generator)
{
	 try
    {
	    // Finally, stream out SVG to the standard output using UTF-8 encoding.
	    boolean useCSS = true; // we want to use CSS style attributes
	    ByteArrayOutputStream out=new ByteArrayOutputStream();
	    Writer writer = new OutputStreamWriter(out,"UTF-8");
	    generator.stream(writer,useCSS);
	    return new String(out.toByteArray());
    }
    catch(Exception e)
    {
    	throw new CException(e);
    }
}
	*/

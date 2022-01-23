/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.biobrief.util;

// tools/src/main/org/apache/pdfbox
//https://www.tutorialspoint.com/pdfbox/

import static org.biobrief.util.PdfBoxHelper.MetaUtil.debugLogMetadata;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is the main program that simply parses the pdf document and transforms it
 * into text.
 *
 * @author Ben Litchfield
 */
//https://pdfbox.apache.org/2.0/commandline.html#extracttext
public class PdfBoxHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(PdfBoxHelper.class);
	
	public static void extractText(String infile, String outfile)
	{
		String[] args={"-sort", infile, outfile};
		extractText(args);
	}
	
	public static void extractText(String infile, String outfile, String password)
	{
		String[] args={"-password", password, "-sort", infile, outfile};
		extractText(args);
	}
	
	private static void extractText(String[] args)
	{
		try
		{
			ExtractText extractor = new ExtractText();
			extractor.startExtraction(args);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	//java -jar c:\packages\bin\pdfbox-app-2.0.7.jar ExtractImages sambuichiCS20140228.pdf -prefix sambuichiCS20140228
	public static void extractImages(String infile, String prefix)
	{
		String[] args={infile, "-prefix", prefix};
		extractImages(args);
	}
	
	private static void extractImages(String[] args)
	{
		try
		{
			ExtractImages extractor = new ExtractImages();
			extractor.run(args);
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static class ExtractText 
	{
	    private static final String PASSWORD = "-password";
	    private static final String ENCODING = "-encoding";
	    private static final String CONSOLE = "-console";
	    private static final String START_PAGE = "-startPage";
	    private static final String END_PAGE = "-endPage";
	    private static final String SORT = "-sort";
	    private static final String IGNORE_BEADS = "-ignoreBeads";
	    private static final String DEBUG = "-debug";
	    
	    private static final String STD_ENCODING = "UTF-8";
	
	    /*
	     * debug flag
	     */
	    private boolean debug = false;
	
	    /**
	     * private constructor.
	    */
	    private ExtractText()
	    {
	        //static class
	    }
	
	    /**
	     * Infamous main method.
	     *
	     * @param args Command line arguments, should be one and a reference to a file.
	     *
	     * @throws IOException if there is an error reading the document or extracting the text.
	     */
//	    public static void main( String[] args ) throws IOException
//	    {
//	        // suppress the Dock icon on OS X
//	        System.setProperty("apple.awt.UIElement", "true");
//	
//	        ExtractText extractor = new ExtractText();
//	        extractor.startExtraction(args);
//	    }
	    /**
	     * Starts the text extraction.
	     *  
	     * @param args the commandline arguments.
	     * @throws IOException if there is an error reading the document or extracting the text.
	     */
	    public void startExtraction( String[] args ) throws IOException
	    {
	        boolean toConsole = false;
	        boolean sort = false;
	        boolean separateBeads = true;
	        String password = "";
	        String encoding = STD_ENCODING;
	        String pdfFile = null;
	        String outputFile = null;
	        // Defaults to text files
	        String ext = ".txt";
	        int startPage = 1;
	        int endPage = Integer.MAX_VALUE;
	        for( int i=0; i<args.length; i++ )
	        {
	            if( args[i].equals( PASSWORD ) )
	            {
	                i++;
	                if( i >= args.length )
	                {
	                    usage();
	                }
	                password = args[i];
	            }
	            else if( args[i].equals( ENCODING ) )
	            {
	                i++;
	                if( i >= args.length )
	                {
	                    usage();
	                }
	                encoding = args[i];
	            }
	            else if( args[i].equals( START_PAGE ) )
	            {
	                i++;
	                if( i >= args.length )
	                {
	                    usage();
	                }
	                startPage = Integer.parseInt( args[i] );
	            }
	            else if( args[i].equals( SORT ) )
	            {
	                sort = true;
	            }
	            else if( args[i].equals( IGNORE_BEADS ) )
	            {
	                separateBeads = false;
	            }
	            else if( args[i].equals( DEBUG ) )
	            {
	                debug = true;
	            }
	            else if( args[i].equals( END_PAGE ) )
	            {
	                i++;
	                if( i >= args.length )
	                {
	                    usage();
	                }
	                endPage = Integer.parseInt( args[i] );
	            }
	            else if( args[i].equals( CONSOLE ) )
	            {
	                toConsole = true;
	            }
	            else
	            {
	                if( pdfFile == null )
	                {
	                    pdfFile = args[i];
	                }
	                else
	                {
	                    outputFile = args[i];
	                }
	            }
	        }
	
	        if( pdfFile == null )
	        {
	            usage();
	        }
	        else
	        {
	
	            Writer output = null;
	            PDDocument document = null;
	            try
	            {
	                long startTime = startProcessing("Loading PDF "+pdfFile);
	                if( outputFile == null && pdfFile.length() >4 )
	                {
	                    outputFile = new File( pdfFile.substring( 0, pdfFile.length() -4 ) + ext ).getAbsolutePath();
	                }
	                document = PDDocument.load(new File( pdfFile ), password);
	                
	                AccessPermission ap = document.getCurrentAccessPermission();
	                if( ! ap.canExtractContent() )
	                {
	                    throw new IOException( "You do not have permission to extract text" );
	                }
	                
	                stopProcessing("Time for loading: ", startTime);
	
	                if( toConsole )
	                {
	                    output = new OutputStreamWriter( System.out, encoding );
	                }
	                else
	                {
//	                    if (toHTML && !STD_ENCODING.equals(encoding))
//	                    {
//	                        encoding = STD_ENCODING;
//	                        System.out.println("The encoding parameter is ignored when writing html output.");
//	                    }
	                    output = new OutputStreamWriter( new FileOutputStream( outputFile ), encoding );
	                }
	
	                PDFTextStripper stripper = new PDFTextStripper();
	                stripper.setSortByPosition( sort );
	                stripper.setShouldSeparateByBeads( separateBeads );
	                stripper.setStartPage( startPage );
	                stripper.setEndPage( endPage );
	
	                startTime = startProcessing("Starting text extraction");
	                if (debug) 
	                {
	                    System.err.println("Writing to "+outputFile);
	                }
	                
	                // Extract text for main document:
	                stripper.writeText( document, output );
	                
	                // ... also for any embedded PDFs:
	                PDDocumentCatalog catalog = document.getDocumentCatalog();
	                PDDocumentNameDictionary names = catalog.getNames();    
	                if (names != null)
	                {
	                    PDEmbeddedFilesNameTreeNode embeddedFiles = names.getEmbeddedFiles();
	                    if (embeddedFiles != null)
	                    {
	                        Map<String, PDComplexFileSpecification> embeddedFileNames = embeddedFiles.getNames();
	                        if (embeddedFileNames != null)
	                        {
	                            for (Map.Entry<String, PDComplexFileSpecification> ent : embeddedFileNames.entrySet()) 
	                            {
	                                if (debug)
	                                {
	                                    System.err.println("Processing embedded file " + ent.getKey() + ":");
	                                }
	                                PDComplexFileSpecification spec = ent.getValue();
	                                PDEmbeddedFile file = spec.getEmbeddedFile();
	                                if (file != null && "application/pdf".equals(file.getSubtype()))
	                                {
	                                    if (debug)
	                                    {
	                                        System.err.println("  is PDF (size=" + file.getSize() + ")");
	                                    }
	                                    InputStream fis = file.createInputStream();
	                                    PDDocument subDoc = null;
	                                    try 
	                                    {
	                                        subDoc = PDDocument.load(fis);
	                                    } 
	                                    finally 
	                                    {
	                                        fis.close();
	                                    }
	                                    try 
	                                    {
	                                        stripper.writeText( subDoc, output );
	                                    } 
	                                    finally 
	                                    {
	                                        IOUtils.closeQuietly(subDoc);                                       
	                                    }
	                                }
	                            } 
	                        }
	                    }
	                }
	                stopProcessing("Time for extraction: ", startTime);
	            }
	            finally
	            {
	                IOUtils.closeQuietly(output);
	                IOUtils.closeQuietly(document);
	            }
	        }
	    }
	
	    private long startProcessing(String message) 
	    {
	        if (debug) 
	        {
	            System.err.println(message);
	        }
	        return System.currentTimeMillis();
	    }
	    
	    private void stopProcessing(String message, long startTime) 
	    {
	        if (debug)
	        {
	            long stopTime = System.currentTimeMillis();
	            float elapsedTime = ((float)(stopTime - startTime))/1000;
	            System.err.println(message + elapsedTime + " seconds");
	        }
	    }
	
	    /**
	     * This will print the usage requirements and exit.
	     */
	    private static void usage()
	    {
	        String message = "Usage: java -jar pdfbox-app-x.y.z.jar ExtractText [options] <inputfile> [output-text-file]\n"
	            + "\nOptions:\n"
	            + "  -password  <password>        : Password to decrypt document\n"
	            + "  -encoding  <output encoding> : UTF-8 (default) or ISO-8859-1, UTF-16BE, UTF-16LE, etc.\n"
	            + "  -console                     : Send text to console instead of file\n"
	            + "  -html                        : Output in HTML format instead of raw text\n"
	            + "  -sort                        : Sort the text before writing\n"
	            + "  -ignoreBeads                 : Disables the separation by beads\n"
	            + "  -debug                       : Enables debug output about the time consumption of every stage\n"
	            + "  -startPage <number>          : The first page to start extraction(1 based)\n"
	            + "  -endPage <number>            : The last page to extract(inclusive)\n"
	            + "  <inputfile>                  : The PDF document to use\n"
	            + "  [output-text-file]           : The file to write the text to";
	        
	        System.err.println(message);
	        //System.exit( 1 );
	    }
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Extracts the images from a PDF file.
	 *
	 * @author Ben Litchfield
	 */
	public static class ExtractImages
	{
	    private static final String PASSWORD = "-password";
	    private static final String PREFIX = "-prefix";
	    private static final String DIRECTJPEG = "-directJPEG";

	    private static final List<String> JPEG = Arrays.asList(
	            COSName.DCT_DECODE.getName(),
	            COSName.DCT_DECODE_ABBREVIATION.getName());

	    private boolean directJPEG;
	    private String prefix;

	    private final Set<COSStream> seen = new HashSet<COSStream>();
	    private int imageCounter = 1;

	    private ExtractImages()
	    {
	    }

//	    public static void main(String[] args) throws IOException
//	    {
//	        // suppress the Dock icon on OS X
//	        System.setProperty("apple.awt.UIElement", "true");
//
//	        ExtractImages extractor = new ExtractImages();
//	        extractor.run(args);
//	    }

	    private void run(String[] args) throws IOException
	    {
	        if (args.length < 1 || args.length > 4)
	        {
	            usage();
	        }
	        else
	        {
	            String pdfFile = null;
	            String password = "";
	            for(int i = 0; i < args.length; i++)
	            {
	                switch (args[i])
	                {
	                    case PASSWORD:
	                        i++;
	                        if (i >= args.length)
	                        {
	                            usage();
	                        }
	                        password = args[i];
	                        break;
	                    case PREFIX:
	                        i++;
	                        if (i >= args.length)
	                        {
	                            usage();
	                        }
	                        prefix = args[i];
	                        break;
	                    case DIRECTJPEG:
	                        directJPEG = true;
	                        break;
	                    default:
	                        if (pdfFile == null)
	                        {
	                            pdfFile = args[i];
	                        }
	                        break;
	                }
	            }
	            if (pdfFile == null)
	            {
	                usage();
	            }
	            else
	            {
	                if (prefix == null && pdfFile.length() >4)
	                {
	                    prefix = pdfFile.substring(0, pdfFile.length() -4);
	                }

	                extract(pdfFile, password);
	            }
	        }
	    }

	    /**
	     * Print the usage requirements and exit.
	     */
	    private static void usage()
	    {
	        String message = "Usage: java " + ExtractImages.class.getName() + " [options] <inputfile>\n"
	                + "\nOptions:\n"
	                + "  -password <password>   : Password to decrypt document\n"
	                + "  -prefix <image-prefix> : Image prefix (default to pdf name)\n"
	                + "  -directJPEG            : Forces the direct extraction of JPEG/JPX images "
	                + "                           regardless of colorspace or masking\n"
	                + "  <inputfile>            : The PDF document to use\n";
	        
	        System.err.println(message);
	        System.exit(1);
	    }

	    private void extract(String pdfFile, String password) throws IOException
	    {
	        try (PDDocument document = PDDocument.load(new File(pdfFile), password))
	        {
	            AccessPermission ap = document.getCurrentAccessPermission();
	            if (!ap.canExtractContent())
	            {
	                throw new IOException("You do not have permission to extract images");
	            }

	            for (int i = 0; i < document.getNumberOfPages(); i++) // todo: ITERATOR would be much better
	            {
	                PDPage page = document.getPage(i);
	                ImageGraphicsEngine extractor = new ImageGraphicsEngine(page);
	                extractor.run();
	            }
	        }
	    }

	    private class ImageGraphicsEngine extends PDFGraphicsStreamEngine
	    {
	        protected ImageGraphicsEngine(PDPage page) throws IOException
	        {
	            super(page);
	        }

	        public void run() throws IOException
	        {
	            PDPage page = getPage();
	            processPage(page);
	            PDResources res = page.getResources();
	            for (COSName name : res.getExtGStateNames())
	            {
	                PDSoftMask softMask = res.getExtGState(name).getSoftMask();
	                if (softMask != null)
	                {
	                    PDTransparencyGroup group = softMask.getGroup();
	                    if (group != null)
	                    {
	                        processSoftMask(group);
	                    }
	                }
	            }
	        }

	        @Override
	        public void drawImage(PDImage pdImage) throws IOException
	        {
	            if (pdImage instanceof PDImageXObject)
	            {
	                PDImageXObject xobject = (PDImageXObject)pdImage;
	                if (seen.contains(xobject.getCOSObject()))
	                {
	                    // skip duplicate image
	                    return;
	                }
	                seen.add(xobject.getCOSObject());
	            }

	            // save image
	            String name = prefix + "-" + imageCounter;
	            imageCounter++;

	            System.out.println("Writing image: " + name);
	            write2file(pdImage, name, directJPEG);
	        }

	        @Override
	        public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3)
	                throws IOException
	        {

	        }

	        @Override
	        public void clip(int windingRule) throws IOException
	        {

	        }

	        @Override
	        public void moveTo(float x, float y) throws IOException
	        {

	        }

	        @Override
	        public void lineTo(float x, float y) throws IOException
	        {

	        }

	        @Override
	        public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)
	                throws IOException
	        {

	        }

	        @Override
	        public Point2D getCurrentPoint() throws IOException
	        {
	            return new Point2D.Float(0, 0);
	        }

	        @Override
	        public void closePath() throws IOException
	        {

	        }

	        @Override
	        public void endPath() throws IOException
	        {

	        }

	        @Override
	        public void strokePath() throws IOException
	        {

	        }

	        @Override
	        public void fillPath(int windingRule) throws IOException
	        {

	        }

	        @Override
	        public void fillAndStrokePath(int windingRule) throws IOException
	        {

	        }

	        @Override
	        public void shadingFill(COSName shadingName) throws IOException
	        {

	        }
	    }

	    private boolean hasMasks(PDImage pdImage) throws IOException
	    {
	        if (pdImage instanceof PDImageXObject)
	        {
	            PDImageXObject ximg = (PDImageXObject) pdImage;
	            return ximg.getMask() != null || ximg.getSoftMask() != null;
	        }
	        return false;
	    }

	    /**
	     * Writes the image to a file with the filename prefix + an appropriate suffix, like "Image.jpg".
	     * The suffix is automatically set depending on the image compression in the PDF.
	     * @param pdImage the image.
	     * @param prefix the filename prefix.
	     * @param directJPEG if true, force saving JPEG/JPX streams as they are in the PDF file. 
	     * @throws IOException When something is wrong with the corresponding file.
	     */
	    private void write2file(PDImage pdImage, String filename, boolean directJPEG) throws IOException
	    {
	        String suffix = pdImage.getSuffix();
	        if (suffix == null || "jb2".equals(suffix))
	        {
	            suffix = "png";
	        }
	        else if ("jpx".equals(suffix))
	        {
	            // use jp2 suffix for file because jpx not known by windows
	            suffix = "jp2";
	        }

	        try (FileOutputStream out = new FileOutputStream(filename + "." + suffix))
	        {
	            BufferedImage image = pdImage.getImage();
	            if (image != null)
	            {
	                if ("jpg".equals(suffix))
	                {
	                    String colorSpaceName = pdImage.getColorSpace().getName();
	                    if (directJPEG || 
	                            !hasMasks(pdImage) && 
	                                     (PDDeviceGray.INSTANCE.getName().equals(colorSpaceName) ||
	                                      PDDeviceRGB.INSTANCE.getName().equals(colorSpaceName)))
	                    {
	                        // RGB or Gray colorspace: get and write the unmodified JPEG stream
	                        InputStream data = pdImage.createInputStream(JPEG);
	                        IOUtils.copy(data, out);
	                        IOUtils.closeQuietly(data);
	                    }
	                    else
	                    {
	                        // for CMYK and other "unusual" colorspaces, the JPEG will be converted
	                        ImageIOUtil.writeImage(image, suffix, out);
	                    }
	                }
	                else if ("jp2".equals(suffix))
	                {
	                    String colorSpaceName = pdImage.getColorSpace().getName();
	                    if (directJPEG || 
	                            !hasMasks(pdImage) && 
	                                     (PDDeviceGray.INSTANCE.getName().equals(colorSpaceName) ||
	                                      PDDeviceRGB.INSTANCE.getName().equals(colorSpaceName)))
	                    {
	                        // RGB or Gray colorspace: get and write the unmodified JPEG2000 stream
	                        InputStream data = pdImage.createInputStream(
	                                Arrays.asList(COSName.JPX_DECODE.getName()));
	                        IOUtils.copy(data, out);
	                        IOUtils.closeQuietly(data);
	                    }
	                    else
	                    {                        
	                        // for CMYK and other "unusual" colorspaces, the image will be converted
	                        ImageIOUtil.writeImage(image, "jpeg2000", out);
	                    }
	                }
	                else 
	                {
	                    ImageIOUtil.writeImage(image, suffix, out);
	                }
	            }
	            out.flush();
	        }
	    }
	}
	
	//////////////////////////////////////////////////////////////////
	
	/**
	 * Handles some ImageIO operations.
	 */
	public static class ImageIOUtil
	{
	    /**
	     * Log instance
	     */
	    private static final Log LOG = LogFactory.getLog(ImageIOUtil.class);

	    private ImageIOUtil()
	    {
	    }

	    /**
	     * Writes a buffered image to a file using the given image format. See     
	     * {@link #writeImage(BufferedImage image, String formatName, 
	     * OutputStream output, int dpi, float quality)} for more details.
	     *
	     * @param image the image to be written
	     * @param filename used to construct the filename for the individual image.
	     * Its suffix will be used as the image format.
	     * @param dpi the resolution in dpi (dots per inch) to be used in metadata
	     * @return true if the image file was produced, false if there was an error.
	     * @throws IOException if an I/O error occurs
	     */
	    public static boolean writeImage(BufferedImage image, String filename,
	            int dpi) throws IOException
	    {
	        File file = new File(filename);
	        try
	        (FileOutputStream output = new FileOutputStream(file)) {
	            String formatName = filename.substring(filename.lastIndexOf('.') + 1);
	            return writeImage(image, formatName, output, dpi);
	        }
	    }

	    /**
	     * Writes a buffered image to a file using the given image format. See      
	     * {@link #writeImage(BufferedImage image, String formatName, 
	     * OutputStream output, int dpi, float quality)} for more details.
	     *
	     * @param image the image to be written
	     * @param formatName the target format (ex. "png") which is also the suffix
	     * for the filename
	     * @param filename used to construct the filename for the individual image.
	     * The formatName parameter will be used as the suffix.
	     * @param dpi the resolution in dpi (dots per inch) to be used in metadata
	     * @return true if the image file was produced, false if there was an error.
	     * @throws IOException if an I/O error occurs
	     * @deprecated use
	     * {@link #writeImage(BufferedImage image, String filename, int dpi)}, which
	     * uses the full filename instead of just the prefix.
	     */
	    @Deprecated
	    public static boolean writeImage(BufferedImage image, String formatName, String filename,
	            int dpi) throws IOException
	    {
	        File file = new File(filename + "." + formatName);
	        try
	        (FileOutputStream output = new FileOutputStream(file)) {
	            return writeImage(image, formatName, output, dpi);
	        }
	    }

	    /**
	     * Writes a buffered image to a file using the given image format. See      
	     * {@link #writeImage(BufferedImage image, String formatName, 
	     * OutputStream output, int dpi, float quality)} for more details.
	     *
	     * @param image the image to be written
	     * @param formatName the target format (ex. "png")
	     * @param output the output stream to be used for writing
	     * @return true if the image file was produced, false if there was an error.
	     * @throws IOException if an I/O error occurs
	     */
	    public static boolean writeImage(BufferedImage image, String formatName, OutputStream output)
	            throws IOException
	    {
	        return writeImage(image, formatName, output, 72);
	    }

	    /**
	     * Writes a buffered image to a file using the given image format. See      
	     * {@link #writeImage(BufferedImage image, String formatName, 
	     * OutputStream output, int dpi, float quality)} for more details.
	     *
	     * @param image the image to be written
	     * @param formatName the target format (ex. "png")
	     * @param output the output stream to be used for writing
	     * @param dpi the resolution in dpi (dots per inch) to be used in metadata
	     * @return true if the image file was produced, false if there was an error.
	     * @throws IOException if an I/O error occurs
	     */
	    public static boolean writeImage(BufferedImage image, String formatName, OutputStream output,
	            int dpi) throws IOException
	    {
	        return writeImage(image, formatName, output, dpi, 1.0f);
	    }

	    /**
	     * Writes a buffered image to a file using the given image format.
	     * Compression is fixed for PNG, GIF, BMP and WBMP, dependent of the quality
	     * parameter for JPG, and dependent of bit count for TIFF (a bitonal image
	     * will be compressed with CCITT G4, a color image with LZW). Creating a
	     * TIFF image is only supported if the jai_imageio library is in the class
	     * path.
	     *
	     * @param image the image to be written
	     * @param formatName the target format (ex. "png")
	     * @param output the output stream to be used for writing
	     * @param dpi the resolution in dpi (dots per inch) to be used in metadata
	     * @param quality quality to be used when compressing the image (0 &lt;
	     * quality &lt; 1.0f)
	     * @return true if the image file was produced, false if there was an error.
	     * @throws IOException if an I/O error occurs
	     */
	    public static boolean writeImage(BufferedImage image, String formatName, OutputStream output,
	            int dpi, float quality) throws IOException
	    {
	        ImageOutputStream imageOutput = null;
	        ImageWriter writer = null;
	        try
	        {
	            // find suitable image writer
	            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
	            ImageWriteParam param = null;
	            IIOMetadata metadata = null;
	            // Loop until we get the best driver, i.e. one that supports
	            // setting dpi in the standard metadata format; however we'd also 
	            // accept a driver that can't, if a better one can't be found
	            while (writers.hasNext())
	            {
	                if (writer != null)
	                {
	                    writer.dispose();
	                }
	                writer = writers.next();
	                if (writer != null)
	                {
	                    param = writer.getDefaultWriteParam();
	                    metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
	                    if (metadata != null
	                            && !metadata.isReadOnly()
	                            && metadata.isStandardMetadataFormatSupported())
	                    {
	                        break;
	                    }
	                }
	            }
	            if (writer == null)
	            {
	                LOG.error("No ImageWriter found for '" + formatName + "' format");
	                StringBuilder sb = new StringBuilder();
	                String[] writerFormatNames = ImageIO.getWriterFormatNames();
	                for (String fmt : writerFormatNames)
	                {
	                    sb.append(fmt);
	                    sb.append(' ');
	                }
	                LOG.error("Supported formats: " + sb);
	                return false;
	            }

	            // compression
	            if (param != null && param.canWriteCompressed())
	            {
	                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	                if (formatName.toLowerCase().startsWith("tif"))
	                {
	                    // TIFF compression
	                    TIFFUtil.setCompressionType(param, image);
	                }
	                else
	                {
	                    param.setCompressionType(param.getCompressionTypes()[0]);
	                    param.setCompressionQuality(quality);
	                }
	            }

	            if (formatName.toLowerCase().startsWith("tif"))
	            {
	                // TIFF metadata
	                TIFFUtil.updateMetadata(metadata, image, dpi);
	            }
	            else if ("jpeg".equals(formatName.toLowerCase())
	                    || "jpg".equals(formatName.toLowerCase()))
	            {
	                // This segment must be run before other meta operations,
	                // or else "IIOInvalidTreeException: Invalid node: app0JFIF"
	                // The other (general) "meta" methods may not be used, because
	                // this will break the reading of the meta data in tests
	                JPEGUtil.updateMetadata(metadata, dpi);
	            }
	            else
	            {
	                // write metadata is possible
	                if (metadata != null
	                        && !metadata.isReadOnly()
	                        && metadata.isStandardMetadataFormatSupported())
	                {
	                    setDPI(metadata, dpi, formatName);
	                }
	            }

	            // write
	            imageOutput = ImageIO.createImageOutputStream(output);
	            writer.setOutput(imageOutput);
	            writer.write(null, new IIOImage(image, null, metadata), param);
	        }
	        finally
	        {
	            if (writer != null)
	            {
	                writer.dispose();
	            }
	            if (imageOutput != null)
	            {
	                imageOutput.close();
	            }
	        }
	        return true;
	    }

	    /**
	     * Gets the named child node, or creates and attaches it.
	     *
	     * @param parentNode the parent node
	     * @param name name of the child node
	     *
	     * @return the existing or just created child node
	     */
	    private static IIOMetadataNode getOrCreateChildNode(IIOMetadataNode parentNode, String name)
	    {
	        NodeList nodeList = parentNode.getElementsByTagName(name);
	        if (nodeList.getLength() > 0)
	        {
	            return (IIOMetadataNode) nodeList.item(0);
	        }
	        IIOMetadataNode childNode = new IIOMetadataNode(name);
	        parentNode.appendChild(childNode);
	        return childNode;
	    }

	    // sets the DPI metadata
	    private static void setDPI(IIOMetadata metadata, int dpi, String formatName)
	            throws IIOInvalidTreeException
	    {
	        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(MetaUtil.STANDARD_METADATA_FORMAT);

	        IIOMetadataNode dimension = getOrCreateChildNode(root, "Dimension");

	        // PNG writer doesn't conform to the spec which is
	        // "The width of a pixel, in millimeters"
	        // but instead counts the pixels per millimeter
	        float res = "PNG".equals(formatName.toUpperCase())
	                    ? dpi / 25.4f
	                    : 25.4f / dpi;

	        IIOMetadataNode child;

	        child = getOrCreateChildNode(dimension, "HorizontalPixelSize");
	        child.setAttribute("value", Double.toString(res));

	        child = getOrCreateChildNode(dimension, "VerticalPixelSize");
	        child.setAttribute("value", Double.toString(res));

	        metadata.mergeTree(MetaUtil.STANDARD_METADATA_FORMAT, root);
	    }
	}
	
	///////////////////////////////////////////////////////////
	
	public static class MetaUtil
	{
	    private static final Log LOG = LogFactory.getLog(MetaUtil.class);

	    static final String SUN_TIFF_FORMAT = "com_sun_media_imageio_plugins_tiff_image_1.0";
	    static final String JPEG_NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";
	    static final String STANDARD_METADATA_FORMAT = "javax_imageio_1.0";
	    
	    private MetaUtil()
	    {
	    }    

	    // logs metadata as an XML tree if debug is enabled
	    static void debugLogMetadata(IIOMetadata metadata, String format)
	    {
	        if (!LOG.isDebugEnabled())
	        {
	            return;
	        }

	        // see http://docs.oracle.com/javase/7/docs/api/javax/imageio/
	        //     metadata/doc-files/standard_metadata.html
	        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(format);
	        try
	        {
	            StringWriter xmlStringWriter = new StringWriter();
	            StreamResult streamResult = new StreamResult(xmlStringWriter);
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            // see http://stackoverflow.com/a/1264872/535646
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	            DOMSource domSource = new DOMSource(root);
	            transformer.transform(domSource, streamResult);
	            LOG.debug("\n" + xmlStringWriter);
	        }
	        catch (IllegalArgumentException | TransformerException ex)
	        {
	            LOG.error(ex, ex);
	        }
	    }

	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static class JPEGUtil
	{
	    private JPEGUtil()
	    {
	    }
	    
	    /**
	     * Set dpi in a JPEG file
	     *
	     * @param metadata the meta data
	     * @param dpi the dpi
	     *
	     * @throws IIOInvalidTreeException if something goes wrong
	     */
	    static void updateMetadata(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException
	    {
	        MetaUtil.debugLogMetadata(metadata, MetaUtil.JPEG_NATIVE_FORMAT);

	        // https://svn.apache.org/viewvc/xmlgraphics/commons/trunk/src/java/org/apache/xmlgraphics/image/writer/imageio/ImageIOJPEGImageWriter.java
	        // http://docs.oracle.com/javase/6/docs/api/javax/imageio/metadata/doc-files/jpeg_metadata.html
	        Element root = (Element) metadata.getAsTree(MetaUtil.JPEG_NATIVE_FORMAT);
	        NodeList jvarNodeList = root.getElementsByTagName("JPEGvariety");
	        Element jvarChild;
	        if (jvarNodeList.getLength() == 0)
	        {
	            jvarChild = new IIOMetadataNode("JPEGvariety");
	            root.appendChild(jvarChild);
	        }
	        else
	        {
	            jvarChild = (Element) jvarNodeList.item(0);
	        }

	        NodeList jfifNodeList = jvarChild.getElementsByTagName("app0JFIF");
	        Element jfifChild;
	        if (jfifNodeList.getLength() == 0)
	        {
	            jfifChild = new IIOMetadataNode("app0JFIF");
	            jvarChild.appendChild(jfifChild);
	        }
	        else
	        {
	            jfifChild = (Element) jfifNodeList.item(0);
	        }
	        if (jfifChild.getAttribute("majorVersion").isEmpty())
	        {
	            jfifChild.setAttribute("majorVersion", "1");
	        }
	        if (jfifChild.getAttribute("minorVersion").isEmpty())
	        {
	            jfifChild.setAttribute("minorVersion", "2");
	        }
	        jfifChild.setAttribute("resUnits", "1"); // inch
	        jfifChild.setAttribute("Xdensity", Integer.toString(dpi));
	        jfifChild.setAttribute("Ydensity", Integer.toString(dpi));
	        if (jfifChild.getAttribute("thumbWidth").isEmpty())
	        {
	            jfifChild.setAttribute("thumbWidth", "0");
	        }
	        if (jfifChild.getAttribute("thumbHeight").isEmpty())
	        {
	            jfifChild.setAttribute("thumbHeight", "0");
	        }
	        
	        // mergeTree doesn't work for ARGB
	        metadata.setFromTree(MetaUtil.JPEG_NATIVE_FORMAT, root); 
	    }
	}
	
	////////////////////////////////////////////////////////////////////
	
	public static class TIFFUtil
	{
	    private static final Log LOG = LogFactory.getLog(TIFFUtil.class);

	    private TIFFUtil()
	    {
	    }    

	    /**
	     * Sets the ImageIO parameter compression type based on the given image.
	     * @param image buffered image used to decide compression type
	     * @param param ImageIO write parameter to update
	     */
	    public static void setCompressionType(ImageWriteParam param, BufferedImage image)
	    {
	        // avoid error: first compression type is RLE, not optimal and incorrect for color images
	        // TODO expose this choice to the user?
	        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY &&
	            image.getColorModel().getPixelSize() == 1)
	        {
	            param.setCompressionType("CCITT T.6");
	        }
	        else
	        {
	            param.setCompressionType("LZW");
	        }
	    }

	    /**
	     * Updates the given ImageIO metadata with Sun's custom TIFF tags, as described in
	     * the <a href="https://svn.apache.org/repos/asf/xmlgraphics/commons/tags/commons-1_3_1/src/java/org/apache/xmlgraphics/image/writer/imageio/ImageIOTIFFImageWriter.java">org.apache.xmlgraphics.image.writer.imageio.ImageIOTIFFImageWriter
	     * sources</a>, 
	     * the <a href="http://download.java.net/media/jai-imageio/javadoc/1.0_01/com/sun/media/imageio/plugins/tiff/package-summary.html">com.sun.media.imageio.plugins.tiff
	     * package javadoc</a>
	     * and the <a href="http://partners.adobe.com/public/developer/tiff/index.html">TIFF
	     * specification</a>.
	     *
	     * @param image buffered image which will be written
	     * @param metadata ImageIO metadata
	     * @param dpi image dots per inch
	     * @throws IIOInvalidTreeException if something goes wrong
	     */
	    static void updateMetadata(IIOMetadata metadata, BufferedImage image, int dpi)
	            throws IIOInvalidTreeException
	    {
	        String metaDataFormat = metadata.getNativeMetadataFormatName();
	        if (metaDataFormat == null)
	        {
	            LOG.debug("TIFF image writer doesn't support any data format");
	            return;
	        }

	        debugLogMetadata(metadata, metaDataFormat);

	        IIOMetadataNode root = new IIOMetadataNode(metaDataFormat);
	        IIOMetadataNode ifd;
	        if (root.getElementsByTagName("TIFFIFD").getLength() == 0)
	        {
	            ifd = new IIOMetadataNode("TIFFIFD");
	            root.appendChild(ifd);
	        }
	        else
	        {
	            ifd = (IIOMetadataNode)root.getElementsByTagName("TIFFIFD").item(0);
	        }

	        // standard metadata does not work, so we set the DPI manually
	        ifd.appendChild(createRationalField(282, "XResolution", dpi, 1));
	        ifd.appendChild(createRationalField(283, "YResolution", dpi, 1));
	        ifd.appendChild(createShortField(296, "ResolutionUnit", 2)); // Inch

	        ifd.appendChild(createLongField(278, "RowsPerStrip", image.getHeight()));
	        ifd.appendChild(createAsciiField(305, "Software", "PDFBOX"));

	        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY && 
	                image.getColorModel().getPixelSize() == 1)
	        {
	            // set PhotometricInterpretation WhiteIsZero
	            // because of bug in Windows XP preview
	            ifd.appendChild(createShortField(262, "PhotometricInterpretation", 0));
	        }
	        
	        metadata.mergeTree(metaDataFormat, root);
	        
	        debugLogMetadata(metadata, metaDataFormat);
	    }

	    private static IIOMetadataNode createShortField(int tiffTagNumber, String name, int val)
	    {
	        IIOMetadataNode field, arrayNode, valueNode;
	        field = new IIOMetadataNode("TIFFField");
	        field.setAttribute("number", Integer.toString(tiffTagNumber));
	        field.setAttribute("name", name);
	        arrayNode = new IIOMetadataNode("TIFFShorts");
	        field.appendChild(arrayNode);
	        valueNode = new IIOMetadataNode("TIFFShort");
	        arrayNode.appendChild(valueNode);
	        valueNode.setAttribute("value", Integer.toString(val));
	        return field;
	    }

	    private static IIOMetadataNode createAsciiField(int number, String name, String val)
	    {
	        IIOMetadataNode field, arrayNode, valueNode;
	        field = new IIOMetadataNode("TIFFField");
	        field.setAttribute("number", Integer.toString(number));
	        field.setAttribute("name", name);
	        arrayNode = new IIOMetadataNode("TIFFAsciis");
	        field.appendChild(arrayNode);
	        valueNode = new IIOMetadataNode("TIFFAscii");
	        arrayNode.appendChild(valueNode);
	        valueNode.setAttribute("value", val);
	        return field;
	    }

	    private static IIOMetadataNode createLongField(int number, String name, long val)
	    {
	        IIOMetadataNode field, arrayNode, valueNode;
	        field = new IIOMetadataNode("TIFFField");
	        field.setAttribute("number", Integer.toString(number));
	        field.setAttribute("name", name);
	        arrayNode = new IIOMetadataNode("TIFFLongs");
	        field.appendChild(arrayNode);
	        valueNode = new IIOMetadataNode("TIFFLong");
	        arrayNode.appendChild(valueNode);
	        valueNode.setAttribute("value", Long.toString(val));
	        return field;
	    }

	    private static IIOMetadataNode createRationalField(int number, String name, int numerator,
	                                                       int denominator)
	    {
	        IIOMetadataNode field, arrayNode, valueNode;
	        field = new IIOMetadataNode("TIFFField");
	        field.setAttribute("number", Integer.toString(number));
	        field.setAttribute("name", name);
	        arrayNode = new IIOMetadataNode("TIFFRationals");
	        field.appendChild(arrayNode);
	        valueNode = new IIOMetadataNode("TIFFRational");
	        arrayNode.appendChild(valueNode);
	        valueNode.setAttribute("value", numerator + "/" + denominator);
	        return field;
	    }

	}
	
	//////////////////////////////////////////////////////////////////////
	
	public static class IOUtils
	{

	    //TODO PDFBox should really use Apache Commons IO.

	    private IOUtils()
	    {
	        //Utility class. Don't instantiate.
	    }

	    /**
	     * Reads the input stream and returns its contents as a byte array.
	     * @param in the input stream to read from.
	     * @return the byte array
	     * @throws IOException if an I/O error occurs
	     */
	    public static byte[] toByteArray(InputStream in) throws IOException
	    {
	        ByteArrayOutputStream baout = new ByteArrayOutputStream();
	        copy(in, baout);
	        return baout.toByteArray();
	    }

	    /**
	     * Copies all the contents from the given input stream to the given output stream.
	     * @param input the input stream
	     * @param output the output stream
	     * @return the number of bytes that have been copied
	     * @throws IOException if an I/O error occurs
	     */
	    public static long copy(InputStream input, OutputStream output) throws IOException
	    {
	        byte[] buffer = new byte[4096];
	        long count = 0;
	        int n = 0;
	        while (-1 != (n = input.read(buffer)))
	        {
	            output.write(buffer, 0, n);
	            count += n;
	        }
	        return count;
	    }

	    /**
	     * Populates the given buffer with data read from the input stream. If the data doesn't
	     * fit the buffer, only the data that fits in the buffer is read. If the data is less than
	     * fits in the buffer, the buffer is not completely filled.
	     * @param in the input stream to read from
	     * @param buffer the buffer to fill
	     * @return the number of bytes written to the buffer
	     * @throws IOException if an I/O error occurs
	     */
	    public static long populateBuffer(InputStream in, byte[] buffer) throws IOException
	    {
	        int remaining = buffer.length;
	        while (remaining > 0)
	        {
	            int bufferWritePos = buffer.length - remaining;
	            int bytesRead = in.read(buffer, bufferWritePos, remaining);
	            if (bytesRead < 0)
	            {
	                break; //EOD
	            }
	            remaining -= bytesRead;
	        }
	        return buffer.length - remaining;
	    }

	    /**
	     * Null safe close of the given {@link Closeable} suppressing any exception.
	     *
	     * @param closeable to be closed
	     */
	    public static void closeQuietly(Closeable closeable)
	    {
	        try
	        {
	            if (closeable != null)
	            {
	                closeable.close();
	            }
	        }
	        catch (IOException ioe)
	        {
	            // ignore
	        }
	    }
	}
	
}
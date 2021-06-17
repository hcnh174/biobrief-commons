package org.biobrief.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import com.google.common.collect.Lists;

import lombok.Data;

//https://github.com/vaadin/spreadsheet/blob/master/vaadin-spreadsheet/src/main/java/com/vaadin/addon/spreadsheet/XSSFColorConverter.java
//https://superuser.com/questions/641471/how-can-i-automatically-convert-powerpoint-to-pdf
//https://stackoverflow.com/questions/4650785/extracting-images-from-pptx-with-apache-poi
//https://elearningart.com/community/free/pixel-print/
//https://stackoverflow.com/questions/61527399/change-font-in-pptx-slide-master-in-apache-poi
//https://www.pixelto.net/cm-to-px-converter#:~:text=1%20inch%20is%20equal%20to%202.54%20centimeters.,38%20px%20for%2096%20dpi.
public class PowerPointHelper
{
	public static final String PPTX="pptx";
	public static final Double FONT_SIZE=11.0;
	public static final String FONT="Yu Gothic";//Meiryo"; //"Yu Gothic"; //Arial
	public static final Double TEXT_WIDTH_ADJUST=13.0;
	public static final Integer BORDER_WIDTH=1;
	public static final Color BORDER_COLOR=Color.BLACK;
	//public static final String PPTX_TEMPLATE=Constants.DATA_DIR+"/templates/ppt/template-variant-report.pptx";
	public static final String TEMP_PPTX_TEMPLATE=Constants.BIOBRIEF_DIR+"/data/templates/ppt/template.pptx";
	public static final XMLSlideShow TEMP_PPTX=loadPptxFile(TEMP_PPTX_TEMPLATE);
	public static final XSLFSlide TEMP_SLIDE=TEMP_PPTX.createSlide();

	public static Dimension getScreenSize(float width, float height)
	{
		float dpc=38; // pixels per centimeter //int dpi=96; // dots per inch
		return new Dimension(Math.round(width*dpc), Math.round(height*dpc));
	}
	
	//String command="CSCRIPT convert_pptx_to_ppt.vbs "D:/temp/ppt/PP_A000113452529.pptx" "D:/temp/ppt/PP_A000113452529.ppt" "A0001"
	public static void convertPptxToPpt(String pptxfile, String pptfile, String password)
	{
		System.out.println("convertPptxToPpt: pptxfile="+pptxfile+" pptfile="+pptfile+" password="+password);
		FileHelper.checkExists(pptxfile);
		if (FileHelper.exists(pptfile))
		{
			System.out.println("pptfile already exists. deleting: "+pptfile);
			FileHelper.deleteFile(pptfile);
		}
		String script=Constants.SCRIPTS_DIR+"/scripts/vba/convert_pptx_to_ppt.vbs";
		CCommandLine command=new CCommandLine("CSCRIPT");
		command.addArg(script);
		command.addArg(StringHelper.doubleQuote(pptxfile));
		command.addArg(StringHelper.doubleQuote(pptfile));
		command.addArg(password);
		System.out.println("command: "+command);
		int exitcode=command.execute();
		System.out.println("exitcode: "+exitcode);
		if (exitcode!=0)
			throw new CException("convert_pptx_to_ppt.vbs script failed: exitcode="+exitcode+" command=["+command+"]");
		FileHelper.checkExists(pptfile);
	}
	
	public static HSLFSlideShow loadPptFile(String filename, String password)
	{
		try
		{
			//https://poi.apache.org/encryption.html
			FileHelper.checkExists(filename);
			Biff8EncryptionKey.setCurrentUserPassword(password);
			POIFSFileSystem fs = new POIFSFileSystem(new File(filename), true);
			HSLFSlideShow ppt = new HSLFSlideShow(fs);
			return ppt;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}		
	}
	
	public static XMLSlideShow loadPptxFile(String filename)
	{
		try
		{
			FileHelper.checkExists(filename);
			XMLSlideShow pptx = new XMLSlideShow(new FileInputStream(filename));
			return pptx;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}		
	}
	
	// https://stackoverflow.com/questions/31844308/java-poi-the-supplied-data-appears-to-be-in-the-office-2007-xml#comment51610596_31844449
	// https://stackoverflow.com/questions/3748/storing-images-in-db-yea-or-nay/3751#3751
	// https://www.codota.com/code/java/classes/java.awt.image.CropImageFilter
	//https://poi.apache.org/encryption.html
	public static XMLSlideShow loadPptxFile(String filename, String password)
	{
		try
		{
			FileHelper.checkExists(filename);
			//https://stackoverflow.com/questions/44897500/using-apache-poi-zip-bomb-detected
			ZipSecureFile.setMinInflateRatio(0);// todo - hack - dangerous?
			POIFSFileSystem filesystem = new POIFSFileSystem(new File(filename), true);
			EncryptionInfo info = new EncryptionInfo(filesystem);
			Decryptor decryptor = Decryptor.getInstance(info);
			if (!decryptor.verifyPassword(password))
				throw new CException("Unable to process: document is encrypted: "+filename);
			Biff8EncryptionKey.setCurrentUserPassword(password);
			XMLSlideShow pptx = new XMLSlideShow(decryptor.getDataStream(filesystem));
			return pptx;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}		
	}
	
	//https://jar-download.com/artifacts/org.apache.poi/poi-scratchpad/3.17-beta1/source-code/org/apache/poi/hslf/usermodel/HSLFPictureShape.java
	//http://www.java2s.com/Code/Java/2D-Graphics-GUI/Imagecrop.htm
	public static String writePicture(HSLFPictureShape shape, String dir, String prefix)
	{
		try
		{
			HSLFPictureData pict=shape.getPictureData();
			byte[] data = pict.getData();
			PictureData.PictureType type = pict.getType();
			String ext = type.extension;
			String filename=dir+"/"+prefix+ext;
			FileOutputStream out = new FileOutputStream(filename);
			out.write(data);
			out.close();
			//writeInfo(shape, dir, prefix);
			return filename;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static void writeInfo(HSLFPictureShape shape, String dir, String prefix)
	{
		HSLFPictureData pict=shape.getPictureData();
		String infofilename=dir+"/"+prefix+".txt";
		String info="dimension: "+pict.getImageDimension()+"\n";
		info+="pixels: "+pict.getImageDimensionInPixels()+"\n";
		info+="content type: "+pict.getContentType()+"\n";
		info+="index: "+pict.getIndex()+"\n";
		info+="offset: "+pict.getOffset()+"\n";
		info+="type: "+pict.getType()+"\n";
		info+="shape: "+StringHelper.toString(shape)+"\n";
		FileHelper.writeFile(infofilename, info);
	}
	
	public static String writePicture(XSLFPictureShape shape, String dir, String prefix)
	{
		try
		{
			XSLFPictureData pict=shape.getPictureData();
			byte[] data = pict.getData();
			PictureData.PictureType type = pict.getType();
			String ext = type.extension;
			String filename=dir+"/"+prefix+ext;
			FileOutputStream out = new FileOutputStream(filename);
			out.write(data);
			out.close();
			return filename;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}

	//////////////////////////////////////////////////////
	
	//https://www.tutorialspoint.com/apache_poi_ppt/apache_poi_ppt_presentation.htm
	//https://stackoverflow.com/questions/50137576/create-a-table-in-powerpoint-with-apache-poi
	public static void writePptx(XMLSlideShow pptx, String filename, MessageWriter messages)
	{
		try
		{
			FileHelper.createParentDirs(filename);
			try (FileOutputStream out = new FileOutputStream(filename)) 
			{
				try
				{
					messages.println("generating pptx file: "+filename);
					pptx.write(out);
				}
				catch (IOException e)
				{
					throw new CException(e);
				}
			}
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static Rectangle2D getAnchor(double left, double top, double width, double height)
	{
		return new Rectangle2D.Double(left, top, width, height);
	}
	
	public static Rectangle2D getNextAnchor(Rectangle2D anchor, int gap)
	{
		return new Rectangle2D.Double(anchor.getX(), anchor.getMaxY()+gap, anchor.getWidth(), 0);//anchor.getHeight()
	}

	///////////////////////////////////////////////
	
	public static XSLFTextRun applyStyle(XSLFTextRun run, Style style)
	{
		run.setFontSize(style.getFontSize());
		if (StringHelper.hasContent(style.getFontFamily()))
			run.setFontFamily(style.getFontFamily());
		if (style.getFontColor()!=null)
			run.setFontColor(style.getFontColor());
		run.setItalic(style.getItalics());
		run.setBold(style.getBold());
		return run;
	}
	
	/////////////////////////////////////////////////////////////////
		
	//https://stackoverflow.com/questions/44438618/resizetofittext-leads-to-text-overflowing-textbox-with-non-latin-characters
	public static Double getTextWidth(String text, Style style)
	{
		double maxwidth=10000.0;
		double stepsize=1.0;
		double initheight=getTextHeight(text, maxwidth, style);
		double minwidth=TEXT_WIDTH_ADJUST+StringHelper.getTextWidth(text, style.getFontFamily(), style.getFontSize());
		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		//System.out.println("text="+text+" initial height="+initheight+" minwidth="+minwidth);
		for (double width=minwidth; width<maxwidth; width+=stepsize)
		{
			double height=getTextHeight(text, width, style);
			//System.out.println("adjusting width="+width+" height="+height+" initheight="+initheight);
			if (height<=initheight)
			{
				minwidth=width;
				break;//return width;
			}
		}
		//throw new CException("could not find width for text: "+text);
		return minwidth;
	}
	
	private static Double getTextHeight(String text, double width, Style style)
	{
		TEMP_SLIDE.getShapes().clear();
		XSLFTable table = TEMP_SLIDE.createTable(1, 1);
		table.setColumnWidth(0, width);
		table.setAnchor(new Rectangle2D.Double(0, 0, 0, 0));//width
		XSLFTableCell cell = table.getCell(0, 0);
		XSLFTextParagraph p = cell.addNewTextParagraph();
		p.setTextAlign(style.getAlign());
		XSLFTextRun run = p.addNewTextRun();
		run.setText(text);
		run.setFontSize(style.getFontSize());
		if (StringHelper.hasContent(style.getFontFamily()))
			run.setFontFamily(style.getFontFamily());
		run.setItalic(style.getItalics());
		run.setBold(style.getBold());
		//System.out.println("text=["+text+"] anchor="+StringHelper.toString(cell.getAnchor()));
		return cell.getAnchor().getHeight();
	}
	
	//https://stackoverflow.com/questions/47711757/how-can-i-add-rectangle-in-blank-slide
	public static XSLFTextBox createRectangle(XSLFSlide slide, Rectangle2D anchor, Color fillColor, Color lineColor, double lineWidth)
	{
		XSLFTextBox textbox = slide.createTextBox();
		XSLFSimpleShape shape=(XSLFSimpleShape)textbox;
		shape.setAnchor(anchor);		
		shape.setFillColor(fillColor);
		shape.setLineColor(lineColor);
		shape.setLineWidth(lineWidth);
		return textbox;
	}
	
	////////////////////////////////////////////////////////////////
	
	public static void merge(List<String> filenames, String outfile, MessageWriter out)
	{
		try
		{
			XMLSlideShow ppt = new XMLSlideShow();
			for(String filename : filenames)
			{
				FileInputStream inputstream = new FileInputStream(filename);
				XMLSlideShow src = new XMLSlideShow(inputstream);	
				for(XSLFSlide srcSlide : src.getSlides())
				{
					ppt.createSlide().importContent(srcSlide);
				}
			}
			//creating the file object
			FileOutputStream output = new FileOutputStream(outfile);
			// saving the changes to a file
			ppt.write(output);
			out.println("Merging done successfully");
			output.close();
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////
	
	@Data
	public static class Style
	{
		protected TextAlign align=TextAlign.LEFT;
		protected Color fontColor;//Color.black;
		protected Color fillColor=Color.white;
		protected String fontFamily=FONT;
		protected Double fontSize=FONT_SIZE;
		protected Boolean bold=false;
		protected Boolean italics=false;
		
		public Style setAlign(final TextAlign align){this.align=align; return this;}
		public Style setFontColor(final Color fontColor){this.fontColor=fontColor; return this;}
		public Style setFillColor(final Color fillColor){this.fillColor=fillColor; return this;}
		public Style setFontFamily(final String fontFamily){this.fontFamily=fontFamily; return this;}
		public Style setFontSize(final Double fontSize){this.fontSize=fontSize; return this;}
		public Style setBold(final Boolean bold){this.bold=bold; return this;}
		public Style setItalics(final Boolean italics){this.italics=italics; return this;}
	}
	
	@Data
	public static class Table
	{
		protected Row header=new Row();
		protected List<Row> rows=new ArrayList<Row>();
		protected Style style=new Style();
		protected String note=null;
	
		public void add(Row row)
		{
			this.rows.add(row);
		}

		public Row createRow()
		{
			return new Row();
		}
		
		public Row addRow()
		{
			Row row=createRow();
			add(row);
			return row;
		}
			
		public Row getRowNum(int rownum)
		{
			return this.rows.get(rownum);
		}
		
		public String getHeader(int col)
		{
			return getHeader().getValue(col);
		}
		
		public Cell addHeader(String name)
		{
			return getHeader().add(name);
		}
		
		public List<String> getColnames()
		{
			List<String> colnames=Lists.newArrayList();
			for (Cell cell : header.getCells())
			{
				colnames.add(cell.getStringValue());
			}
			return colnames;
		}
		
		public int getNumColumns()
		{
			return getHeader().getCells().size();
		}
		
		public int getNumRows()
		{
			return getRows().size();
		}

		public int size()
		{
			return this.rows.size();
		}
		
		public Double getMaxWidth(int colnum)
		{
			double max=header.getCell(colnum).getTextWidth();
			for (Row row : rows)
			{
				Cell cell=row.getCell(colnum);
				double width=cell.getTextWidth();
				if (width>max)
					max=width;
			}
			return max;
		}
	
		public List<Double> getWidths()
		{
			List<Double> widths=Lists.newArrayList();
			for (int colnum=0; colnum < getNumColumns(); colnum++)
			{
				widths.add(getMaxWidth(colnum));
			}
			return widths;
		}
		
		public boolean isEmpty(int colnum)
		{
			boolean empty=true;
			for (Row row : rows)
			{
				Cell cell=row.getCell(colnum);
				if (StringHelper.hasContent(cell.getValue()))
					empty=false;
			}
			return empty;
		}
		
		public void condense()
		{
			List<Integer> skipcols=Lists.newArrayList();
			for (int colnum=0; colnum < getNumColumns(); colnum++)
			{
				if (isEmpty(colnum))
					skipcols.add(colnum);
			}
			removeColumns(skipcols);
		}
		
		public void removeColumns(List<Integer> colnums)
		{
			Collections.reverse(colnums);
			for (int colnum : colnums)
			{
				removeColumn(colnum);
			}
		}
		
		public void removeColumn(int colnum)
		{
			getHeader().removeColumn(colnum);
			for (Row row : rows)
			{
				row.removeColumn(colnum);
			}
		}
	
		@Data
		public class Row
		{
			protected Integer id;
			protected List<Cell> cells=new ArrayList<Cell>();
			protected Style style=new Style();
			
			public void add(Cell cell)
			{
				cell.setRow(this);
				this.cells.add(cell);
			}
			
			public Cell add(Object obj)
			{
				Cell cell=new Cell(obj);
				add(cell);
				return cell;
			}
						
			public Cell getCell(int index)
			{
				return this.cells.get(index);
			}
			
			public String getValue(Integer index)
			{
				if (index==null)
					return null;
				if (index>=this.cells.size())
					return null;
				Cell cell=this.cells.get(index);
				if (cell==null || cell.getValue()==null)
					return null;
				return cell.getValue().toString();
			}
			
			public String getValue(Integer index, String dflt)
			{
				String value=getValue(index);
				if (value==null)
					return dflt;
				return value;
			}
			
			public int size()
			{
				return this.cells.size();
			}
			
			public void removeColumn(int colnum)
			{
				this.cells.remove(colnum);
			}
		}

		@Data
		public static class Cell
		{
			protected Row row;
			protected Object value;
			protected Style style=new Style();
	
			public Cell setValue(final String value)
			{
				this.value=value;
				return this;
			}

			public Cell(){}
			
			public Cell(Object value)
			{
				this.value=value;
			}
			
			public boolean isEmpty()
			{
				return !StringHelper.hasContent(this.value);
			}
			
			public String getStringValue()
			{
				return this.value.toString();
			}
			
			public String getStringValue(String dflt)
			{
				if (isEmpty())
					return dflt;
				return getStringValue();
			}
			
			public Double getTextWidth()
			{
				return PowerPointHelper.getTextWidth(getStringValue(""), style);
			}
		}
	}
}

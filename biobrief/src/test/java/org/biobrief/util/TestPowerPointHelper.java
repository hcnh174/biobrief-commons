package org.biobrief.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.sl.usermodel.Line;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.jupiter.api.Test;

//gradle :biobrief-util:test --stacktrace --info --tests *TestPowerPointHelper
public class TestPowerPointHelper
{
	//@Test
	public void convertPptxToPpt()
	{		
		String pptxfile="c:/temp/ppt/TODO.pptx";
		String pptfile="c:/temp/ppt/TODO.ppt";
		String password="TODO";
		PowerPointHelper.convertPptxToPpt(pptxfile, pptfile, password);
	}
	
	//@Test
	public void loadPptFile()
	{
		String filename="C:\\temp\\TODO.ppt";
		String password="TODO";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.TEMP_DIR+"/ppt";
		FileHelper.createDirectory(outdir);
		
		StringBuilder buffer=new StringBuilder();
		HSLFSlideShow ppt=PowerPointHelper.loadPptFile(filename, password);
		int fignum=1;
		for (HSLFSlide slide : ppt.getSlides())
		{
			buffer.append("*********************************************\n");
			System.out.println("slide="+slide.getSlideNumber());
			for (HSLFShape sh : slide.getShapes())
			{
				// name of the shape
				String name = sh.getShapeName();
				System.out.println("-----------------------------------");
				System.out.println("shape.name"+name);
				// shapes's anchor which defines the position of this shape in the slide
				//Rectangle2D anchor = sh.getAnchor();
				if (sh instanceof Line)
				{
					Line line = (Line) sh;
					System.out.println("shape:line="+line.toString());
				}
				else if (sh instanceof HSLFAutoShape)
				{
					HSLFAutoShape shape = (HSLFAutoShape) sh;
					System.out.println("shape:autoshape="+shape.toString());
					System.out.println("text="+shape.getRawText());
					buffer.append(shape.getRawText()+"\n");
				}
				else if (sh instanceof HSLFTextBox)
				{
					HSLFTextBox shape = (HSLFTextBox) sh;
					System.out.println("shape:text="+shape.toString());
					System.out.println("text="+shape.getRawText());
					buffer.append(shape.getRawText()+"\n");
				}
				else if (sh instanceof HSLFPictureShape)
				{
					HSLFPictureShape shape = (HSLFPictureShape) sh;
					System.out.println("shape:picture="+shape.toString());
					buffer.append("[FIG:"+fignum+"]\n");
					//shape.getPictureData().getData();
					PowerPointHelper.writePicture(shape, outdir, prefix+"_"+fignum);
					fignum++;
				}
			}
		}
		FileHelper.writeFile(outdir+"/"+prefix+".txt", buffer.toString());
	}
	
	//@Test
	public void loadPptxFile()
	{
		String filename="D:\\temp\\ppt\\TODO.pptx";
		String password="TODO";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir="D:/temp/ppt";//Constants.TEMP_DIR+"/ppt";
		FileHelper.createDirectory(outdir);
				
		StringBuilder buffer=new StringBuilder();
		XMLSlideShow pptx=PowerPointHelper.loadPptxFile(filename, password);
		//XMLSlideShow pptx=PowerPointHelper.loadPptxFile(filename);
		int fignum=1;
		for (XSLFSlide slide : pptx.getSlides())
		{
			buffer.append("*********************************************\n");
			System.out.println("slide="+slide.getSlideNumber());
			for (XSLFShape sh : slide.getShapes())
			{
				// name of the shape
				String name = sh.getShapeName();
				System.out.println("-----------------------------------");
				System.out.println("shape.name"+name);
				// shapes's anchor which defines the position of this shape in the slide
				//Rectangle2D anchor = sh.getAnchor();
				if (sh instanceof Line)
				{
					Line line = (Line) sh;
					System.out.println("shape:line="+line.toString());
				}
//				else if (sh instanceof XSLFAutoShape)
//				{
//					XSLFAutoShape shape = (XSLFAutoShape) sh;
//					System.out.println("shape:autoshape="+shape.toString());
//					System.out.println("text="+shape.getRawText());
//					buffer.append(shape.getRawText()+"\n");
//				}
//				else if (sh instanceof XSLFTextBox)
//				{
//					XSLFTextBox shape = (XSLFTextBox) sh;
//					System.out.println("shape:text="+shape.toString());
//					System.out.println("text="+shape.getRawText());
//					buffer.append(shape.getRawText()+"\n");
//				}
				else if (sh instanceof XSLFPictureShape)
				{
					XSLFPictureShape shape = (XSLFPictureShape) sh;
					System.out.println("shape:picture="+shape.toString());
					buffer.append("[FIG:"+fignum+"]\n");
					//shape.getPictureData().getData();
					PowerPointHelper.writePicture(shape, outdir, prefix+"_"+fignum);
					fignum++;
				}
			}
		}
		FileHelper.writeFile(outdir+"/"+prefix+".txt", buffer.toString());
	}
	
	//@Test
	public void cropImages()
	{		
		String filename="d:\\projects\\expertpanel\\TODO\\TODO.ppt";
		String password="TODO";
		
		String prefix=FileHelper.getRoot(filename);
		String outdir=Constants.TEMP_DIR+"/ppt/images";
		FileHelper.createDirectory(outdir);
		
		StringBuilder buffer=new StringBuilder();
		HSLFSlideShow ppt=PowerPointHelper.loadPptFile(filename, password);
		int fignum=1;
		for (HSLFSlide slide : ppt.getSlides())
		{
			buffer.append("*********************************************\n");
			System.out.println("slide="+slide.getSlideNumber());
			for (HSLFShape sh : slide.getShapes())
			{
				if (sh instanceof HSLFPictureShape)
				{
					HSLFPictureShape shape = (HSLFPictureShape) sh;
					System.out.println("shape:picture="+shape.toString());
					buffer.append("[FIG:"+fignum+"]\n");
					//shape.getPictureData().getData();
					PowerPointHelper.writePicture(shape, outdir, prefix+"_"+fignum);
					fignum++;
				}
			}
		}
	}
	
	@Test
	public void createPowerPoint()
	{
		String filename=Constants.TMP_DIR+"/presentation.pptx";
		
		//https://stackoverflow.com/questions/50137576/create-a-table-in-powerpoint-with-apache-poi
		XMLSlideShow powerpoint = new XMLSlideShow();
	    XSLFSlide slide = powerpoint.createSlide();
	    
	    CTable table=new CTable();
	    table.addHeader("Gene");
	    table.addHeader("AA");
	    table.addHeader("MAF");
	    table.addHeader("role");
	    table.addHeader("Position");
	    table.addHeader("ToMMo");
	    table.addHeader("Report");
	    table.addHeader("C-CAT");
	    table.addHeader("CancerVar");
	    table.addHeader("ClinVar");
	    table.addHeader("COSMIC");
	    table.addHeader("OncoKB");
	    
	    CTable.Row row=table.addRow();
	    row.add("PARP3");
	    row.add("T454M");
	    row.add("0.5303");
	    row.add("TSG");
	    row.add("3p21.2");
	    row.add("");
	    row.add("VUS");
	    row.add("VUS");
	    row.add("Benign");
	    row.add("NotFound");
	    row.add("NotFound");
	    row.add("OncoKB");
	    
	    row=table.addRow();
	    row.add("KMT2D");
	    row.add("V1561G");
	    row.add("0.5303");
	    row.add("TSG");
	    row.add("3p21.2");
	    row.add("");
	    row.add("VUS");
	    row.add("VUS");
	    row.add("Benign");
	    row.add("NotFound");
	    row.add("NotFound");
	    row.add("OncoKB");
	    
	    createTable(slide, table, new java.awt.Rectangle(20, 20, 800, 800));
	    
	    createTable(slide, table, new java.awt.Rectangle(20, 200, 800, 800));
//	    XSLFTable table = slide.createTable();
//	    table.setAnchor(new java.awt.Rectangle(50, 50, 800, 800));
//
//	    int numColumns = 3;
//	    int numRows = 5;
//	    XSLFTableRow headerRow = table.addRow();
//	    headerRow.setHeight(50);
//	    // header
//	    for (int i = 0; i < numColumns; i++) {
//	        XSLFTableCell th = headerRow.addCell();
//	        XSLFTextParagraph p = th.addNewTextParagraph();
//	        p.setTextAlign(TextParagraph.TextAlign.CENTER);
//	        XSLFTextRun r = p.addNewTextRun();
//	        r.setText("Header " + (i + 1));
//	        r.setFontSize(20.0);
//	        r.setFontColor(java.awt.Color.white);
//	        th.setFillColor(new java.awt.Color(79, 129, 189));
//	        table.setColumnWidth(i, 150);
//	    }

	    // rows
//	    for (int rownum = 0; rownum < numRows; rownum++) {
//	        XSLFTableRow tr = table.addRow();
//	        tr.setHeight(50);
//	        // header
//	        for (int i = 0; i < numColumns; i++) {
//	            XSLFTableCell cell = tr.addCell();
//	            XSLFTextParagraph p = cell.addNewTextParagraph();
//	            XSLFTextRun r = p.addNewTextRun();
//
//	            r.setText("Cell " + (i + 1));
//	            if (rownum % 2 == 0) {
//	                cell.setFillColor(new java.awt.Color(208, 216, 232));
//	            }
//	            else {
//	                cell.setFillColor(new java.awt.Color(233, 247, 244));
//	            }
//	        }
//	    }

	    writePptx(powerpoint, filename);
//	    try {
//	        try (FileOutputStream out = new FileOutputStream(filename)) 
//	         {
//	            try {
//	                powerpoint.write(out);
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
	}
	
	public static XSLFTable createTable(XSLFSlide slide, int numColumns, int numRows, java.awt.Rectangle anchor)
	{
		XSLFTable table = slide.createTable();
		//table.setAnchor(new java.awt.Rectangle(50, 50, 800, 800));
		table.setAnchor(anchor);
		
//		int numColumns = 3;
//		int numRows = 5;
		XSLFTableRow headerRow = table.addRow();
		headerRow.setHeight(20);
		// header
		for (int i = 0; i < numColumns; i++)
		{
			XSLFTableCell th = headerRow.addCell();
			XSLFTextParagraph p = th.addNewTextParagraph();
			p.setTextAlign(TextParagraph.TextAlign.CENTER);
			XSLFTextRun r = p.addNewTextRun();
			r.setText("Header " + (i + 1));
			r.setFontSize(20.0);
			r.setFontColor(java.awt.Color.white);
			th.setFillColor(new java.awt.Color(79, 129, 189));
			table.setColumnWidth(i, 150);
		}
		
		for (int rownum = 0; rownum < numRows; rownum++)
		{
	        XSLFTableRow tr = table.addRow();
	        tr.setHeight(20);
	        // header
	        for (int i = 0; i < numColumns; i++)
	        {
	            XSLFTableCell cell = tr.addCell();
//	            XSLFTextParagraph p = cell.addNewTextParagraph();
//	            XSLFTextRun r = p.addNewTextRun();
//
//	            r.setText("Cell " + (i + 1));
//	            if (rownum % 2 == 0) {
//	                cell.setFillColor(new java.awt.Color(208, 216, 232));
//	            }
//	            else {
//	                cell.setFillColor(new java.awt.Color(233, 247, 244));
//	            }
	        }
	    }
		return table;
	}
	
	public static XSLFTextRun setText(XSLFTableCell cell, String text, TextParagraph.TextAlign align)
	{
		XSLFTextParagraph p = cell.addNewTextParagraph();
		p.setTextAlign(align);
		XSLFTextRun run = p.addNewTextRun();
		run.setText(text);
		return run;
	}
	
	public static XSLFTable createTable(XSLFSlide slide, CTable table, java.awt.Rectangle anchor)
	{
		XSLFTable xtable = slide.createTable();
		xtable.setAnchor(anchor);
		
		XSLFTableRow headerRow = xtable.addRow();
		headerRow.setHeight(20);
		// header
		for (int i = 0; i < table.getNumColumns(); i++)
		{
			XSLFTableCell th = headerRow.addCell();
			XSLFTextRun run=setText(th, table.getHeader(i), TextParagraph.TextAlign.CENTER);
//			XSLFTextParagraph p = th.addNewTextParagraph();
//			p.setTextAlign(TextParagraph.TextAlign.CENTER);
//			XSLFTextRun r = p.addNewTextRun();
//			run.setText("Header " + (i + 1));
			run.setFontSize(20.0);
			run.setFontColor(java.awt.Color.white);
			th.setFillColor(new java.awt.Color(79, 129, 189));
			xtable.setColumnWidth(i, 150);
		}
		
		for (int rownum = 0; rownum < table.getNumRows(); rownum++)
		{
	        XSLFTableRow tr = xtable.addRow();
	        tr.setHeight(20);
	        // header
	        for (int colnum = 0; colnum < table.getNumColumns(); colnum++)
	        {
	            XSLFTableCell cell = tr.addCell();
	            XSLFTextRun run = setText(cell, table.getRowNum(rownum).getValue(colnum), TextParagraph.TextAlign.CENTER);//table.getRow(rownum).getCell(colnum).getAlign());
//	            XSLFTextParagraph p = cell.addNewTextParagraph();
//	            XSLFTextRun r = p.addNewTextRun();
//
//	            r.setText("Cell " + (i + 1));
//	            if (rownum % 2 == 0) {
//	                cell.setFillColor(new java.awt.Color(208, 216, 232));
//	            }
//	            else {
//	                cell.setFillColor(new java.awt.Color(233, 247, 244));
//	            }
	        }
	    }
		return xtable;
	}
	
	public static void writePptx(XMLSlideShow pptx, String filename)
	{
		try
		{
			try (FileOutputStream out = new FileOutputStream(filename)) 
			{
				try
				{
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
}


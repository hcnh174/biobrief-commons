package org.biobrief.util;


import java.awt.Color;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

//gradle test --info --tests *TestPowerPointHighlighter
public class TestPowerPointHighlighter {

	@Test
	public void testHighlightFields() throws Exception
	{
		String dir="z:/dev/germline";
		String infile=dir+"/input.pptx";
		String password="A5189";
		String outfile=dir+"/output_highlighted.pptx";

		XMLSlideShow pptx=PowerPointHelper.loadPptxFile(infile, password);

		List<XSLFSlide> slides=pptx.getSlides();
		PowerPointHelper.highlightFields(slides.get(0), Arrays.asList("患者", "臨床診断名", "既往歴（悪性腫瘍）", "過去の遺伝子検査の有無", "MSI検査"));
		PowerPointHelper.highlightFields(slides.get(2), Arrays.asList("二次的所見の開示希望"));
		PowerPointHelper.highlightFields(slides.get(3), Arrays.asList("腫瘍割合"));
		PowerPointHelper.writePptx(pptx, outfile, new MessageWriter());
	}
	
//	@Test
//	public void testHighlighting() throws Exception
//	{
//		//A518997049720
//		String dir="z:/dev/germline";
//		String infile=dir+"/input.pptx";
//		String password="A5189";
//		String outfile=dir+"/output_highlighted.pptx";
//		//String target="77歳　男性";//【患者】";
//
//		XMLSlideShow pptx=PowerPointHelper.loadPptxFile(infile, "A5189");
//
//		PowerPointHelper.highlightText(pptx, "肺腺癌");
//		//highlightText(pptx, "肺腺癌");
//		//highlightText(pptx, "あり（オンコマインDx；アクショナブル遺伝子変異認めず）");
//
//		PowerPointHelper.writePptx(pptx, outfile, new MessageWriter());
//	}
	
//	public static void highlightText(XMLSlideShow pptx, String searchText)
//	{
//		for (XSLFSlide slide : pptx.getSlides())
//		{
//			highlightText(slide, searchText);
//		}
//	}
//	
//	public static void highlightText(XSLFSlide slide, String searchText)
//	{
//		for (XSLFShape shape : slide.getShapes())
//		{
//			if (!(shape instanceof XSLFTextShape))
//				continue;
//			XSLFTextShape textShape = (XSLFTextShape) shape;
//			for (XSLFTextParagraph paragraph : textShape.getTextParagraphs())
//			{
//				highlightInParagraph(paragraph, searchText);
//			}
//		}
//	}
//	
//	private static void highlightInParagraph(XSLFTextParagraph paragraph, String searchText)
//	{
//		List<XSLFTextRun> originalRuns = paragraph.getTextRuns();
//		if (originalRuns.isEmpty())
//			return;
//
//		// Snapshot text
//		StringBuilder fullText = new StringBuilder();
//		for (XSLFTextRun r : originalRuns)
//		{
//			fullText.append(r.getRawText());
//		}
//
//		String text = fullText.toString();
//		int index = text.indexOf(searchText);
//		if (index < 0)
//			return;
//
//		// Snapshot style BEFORE clearing
//		TextRunStyle baseStyle = captureStyle(originalRuns.get(0));
//
//		// Clear paragraph safely
//		clearParagraphText(paragraph);
//
//		// Before
//		if (index > 0)
//		{
//			XSLFTextRun before = paragraph.addNewTextRun();
//			applyStyle(baseStyle, before);
//			before.setText(text.substring(0, index));
//		}
//
//		// Highlight
//		XSLFTextRun match = paragraph.addNewTextRun();
//		applyStyle(baseStyle, match);
//		match.setText(searchText);
//		match.setHighlightColor(Color.YELLOW);
//
//		// After
//		int end = index + searchText.length();
//		if (end < text.length())
//		{
//			XSLFTextRun after = paragraph.addNewTextRun();
//			applyStyle(baseStyle, after);
//			after.setText(text.substring(end));
//		}
//	}
//	
//	private static void clearParagraphText(XSLFTextParagraph paragraph)
//	{
//		CTTextParagraph ctP = paragraph.getXmlObject();
//
//		// Remove all text runs
//		ctP.getRList().clear();
//
//		// Remove line breaks if present
//		ctP.getBrList().clear();
//	}
//
////	private static void copyStyle(XSLFTextRun src, XSLFTextRun dst)
////	{
////		dst.setFontColor(src.getFontColor());
////		dst.setFontSize(src.getFontSize());
////		dst.setBold(src.isBold());
////		dst.setItalic(src.isItalic());
////		dst.setUnderlined(src.isUnderlined());
////		dst.setFontFamily(src.getFontFamily());
////	}
//	
//	//////////////////////////////
//	
//	private static TextRunStyle captureStyle(XSLFTextRun run)
//	{
//		TextRunStyle s = new TextRunStyle();
//		//s.fontColor = run.getFontColor();
//		s.fontSize = run.getFontSize();
//		s.bold = run.isBold();
//		s.italic = run.isItalic();
//		s.underlined = run.isUnderlined();
//		s.fontFamily = run.getFontFamily();
//		return s;
//	}
//	
//	private static void applyStyle(TextRunStyle s, XSLFTextRun run)
//	{
//		//if (s.fontColor != null) run.setFontColor(s.fontColor);
//		if (s.fontSize != null) run.setFontSize(s.fontSize);
//		run.setBold(s.bold);
//		run.setItalic(s.italic);
//		run.setUnderlined(s.underlined);
//		if (s.fontFamily != null) run.setFontFamily(s.fontFamily);
//	}
//	
//	static class TextRunStyle {
//		//PaintStyle fontColor;
//		Double fontSize;
//		boolean bold;
//		boolean italic;
//		boolean underlined;
//		String fontFamily;
//	}

}

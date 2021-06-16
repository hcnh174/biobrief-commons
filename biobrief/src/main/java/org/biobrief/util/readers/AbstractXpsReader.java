package org.biobrief.util.readers;

import java.util.List;

import org.biobrief.util.FileHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.MuPdfHelper;
import org.biobrief.util.PdfBoxHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public abstract class AbstractXpsReader<T> extends AbstractPdfReader<T>
{	
	public AbstractXpsReader(Class cls, MessageWriter out)
	{
		super(cls, out);
	}
	
	@Override
	public List<T> readFile(String xpsfile)
	{
		String pdffile=convertXpsToPdf(xpsfile);
		String textfile=convertPdfToText(pdffile);
		extractImages(pdffile);//List<String> images=
		T item=parseTextFile(textfile);
		return Lists.newArrayList(item);
	}
	
	protected String convertXpsToPdf(String xpsfile)
	{
		System.out.println("xpsfile="+xpsfile);
		String pdffile=FileHelper.createTempFile(getPrefix(), ".pdf");
		MuPdfHelper.convertXpsToPdf(xpsfile, pdffile);
		return pdffile;
	}
	
	protected String convertPdfToText(String pdffile)
	{
		System.out.println("pdffile="+pdffile);
		FileHelper.checkExists(pdffile);
		String textfile=FileHelper.createTempFile(getPrefix(), ".txt");
		PdfBoxHelper.extractText(pdffile, textfile);
		return textfile;
	}
	
	protected List<String> extractImages(String pdffile)
	{
		System.out.println("pdffile="+pdffile);
		FileHelper.checkExists(pdffile);
		String prefix=FileHelper.stripFiletype(pdffile);
		PdfBoxHelper.extractImages(pdffile, prefix);
		List<String> images=FileHelper.listFiles(FileHelper.stripFilename(pdffile), ".jpg", true);
		System.out.println(StringHelper.toString(images));
		return images;
	}
	
	protected T parseTextFile(String textfile)
	{
		System.out.println("textfile="+textfile);
		FileHelper.checkExists(textfile);
		String str=FileHelper.readFile(textfile);
		return parse(str);
	}
	
	protected abstract T parse(String str);
	
	private String getPrefix()
	{
		return this.cls.getName().toLowerCase();
	}
}
package org.biobrief.util;

//https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
public enum ContentType
{
	HTML("text/html"),
	PLAIN("text/plain"),
	TXT("text/txt"),
	XML("text/xml"),
	PDF("application/pdf"),
	JAVASCRIPT("application/x-javascript"),
	JSON("application/json"),
	JPEG("image/jpeg"),
	PNG("image/png"),
	EMF("image/emf"),
	TIFF("image/tiff"),
	SVG("image/svg+xml"),
	DOC("application/msword"),
	XLS("application/vnd.ms-excel"),
	PPT("application/vnd.ms-powerpoint"),
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	XLSX ( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	PPTX ( "application/vnd.openxmlformats-officedocument.presentationml.presentation");
	
	private final String mimeType;
	
	ContentType(String mimeType)
	{
		this.mimeType=mimeType;
	}

	public String getMimeType()
	{
		return mimeType;
	}
	
	public static ContentType findByFilename(String filename)
	{
		return findByExtension(FileHelper.getSuffix(filename));
	}
	
	public static ContentType findByExtension(String suffix)
	{
		if (suffix.startsWith("."))
			suffix=suffix.substring(1);
		if (suffix.equalsIgnoreCase("html"))
			return HTML;
		if (suffix.equalsIgnoreCase("txt"))
			return TXT;
		if (suffix.equalsIgnoreCase("xml"))
			return XML;
		if (suffix.equalsIgnoreCase("pdf"))
			return PDF;
		if (suffix.equalsIgnoreCase("js"))
			return JAVASCRIPT;
		if (suffix.equalsIgnoreCase("json"))
			return JSON;
		if (suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("jpg"))
			return JPEG;
		if (suffix.equalsIgnoreCase("png"))
			return PNG;
		if (suffix.equalsIgnoreCase("svg"))
			return SVG;
		if (suffix.equalsIgnoreCase("xls"))
			return XLS;
		if (suffix.equalsIgnoreCase("xlsx"))
			return XLSX;
		if (suffix.equalsIgnoreCase("docx"))
			return DOCX;
		if (suffix.equalsIgnoreCase("pptx"))
			return PPTX;
		if (suffix.equalsIgnoreCase("emf"))
			return EMF;
		if (suffix.equalsIgnoreCase("tif") || suffix.equalsIgnoreCase("tiff"))
			return TIFF;
		throw new CException("no handler for suffix type: "+suffix);
	}
}

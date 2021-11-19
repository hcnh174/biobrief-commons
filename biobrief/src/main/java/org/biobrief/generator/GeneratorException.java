package org.biobrief.generator;

import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

@SuppressWarnings("serial")
public class GeneratorException extends CException
{
	private String filename; 
	
	public GeneratorException(String message)
	{
		super(message);
	}
	
	@Override
	public String getMessage()
	{
		String message=super.getMessage();
		if (StringHelper.hasContent(filename))
			return message+" filename: "+filename;
		return message;
	}
	
	public String getFilename(){return this.filename;}
	public void setFilename(final String filename){this.filename=filename;}
}
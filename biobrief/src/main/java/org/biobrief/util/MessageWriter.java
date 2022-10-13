package org.biobrief.util;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import jakarta.servlet.http.HttpServletResponse;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MessageWriter
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(MessageWriter.class);
	
	protected final PrintWriter writer;
	protected final boolean isSystemOut;
	protected int errors=0;
	
	public MessageWriter(PrintWriter writer)
	{
		this.writer=writer;
		isSystemOut=isSystemOut();
	}
	
	public MessageWriter()
	{
		this.writer=new PrintWriter(System.out);
		isSystemOut=true;
	}
	
	public void echo(String str)
	{
		//if (echo && !isSystemOut)
		if (!isSystemOut)
			System.out.println(str);
	}
	
	public void println(String str)
	{
		echo(str);
		this.writer.println(str);
		this.writer.flush();
	}
	
	public void write(String str)
	{
		echo(str);
		this.writer.print(str);
	}
	
	public void br()
	{
		echo("");
		this.writer.println("");
	}
	
	public void message(String message)
	{
		echo(message);
		write(message);
		br();
		flush();
	}
	
	public void messages(List<String> messages)
	{
		for (String message : messages)
		{
			message(message);
		}
	}
	
	public void warn(String message)
	{
		this.errors++;
		message("<span style=\"color:red;\">WARN: "+message+"</span>");
	}
	
	public void error(String message)
	{
		this.errors++;
		message("<span style=\"color:red;\">ERROR: "+message+"</span>");
	}
	
	public void error(Exception e)
	{
		this.errors++;
		error(e.toString());
		StringHelper.println(e.toString());
		e.printStackTrace();
	}
	
	public void error(String message, Exception e)
	{
		error(message);
		error(e);
	}
	
	public PrintWriter getWriter(){return this.writer;}
	
	public void flush()
	{
		this.writer.flush();
	}
	
	public boolean hasErrors()
	{
		return this.errors>0;
	}

	//////////////////////////////////////////
	
	private boolean isSystemOut()
	{
		return writer.getClass().getCanonicalName().equals(System.out.getClass().getCanonicalName());
	}
}
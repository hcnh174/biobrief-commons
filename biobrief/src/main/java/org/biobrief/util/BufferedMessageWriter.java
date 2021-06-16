package org.biobrief.util;

import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferedMessageWriter extends MessageWriter
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(BufferedMessageWriter.class);

	protected List<String> messages=Lists.newArrayList();

	public BufferedMessageWriter(PrintWriter writer)
	{
		super(writer);
	}
	
	public BufferedMessageWriter()
	{
		super();
	}
	
	public void reset()
	{
		messages.clear();
	}
	
	public void println(String str)
	{
		append(str);
		super.println(str);
	}
	
	public void write(String str)
	{
		append(str);
		super.write(str);
	}
	
	public List<String> getMessages()
	{
		return messages;
	}	
	
	public List<String> getMessages(boolean reset)
	{
		if (!reset)
			return messages;
		List<String> messages=Lists.newArrayList(this.messages.iterator());
		reset();
		return messages;
	}
	
	private void append(String str)
	{
		System.out.println("appending message: "+str);
		this.messages.add(str);
	}
}
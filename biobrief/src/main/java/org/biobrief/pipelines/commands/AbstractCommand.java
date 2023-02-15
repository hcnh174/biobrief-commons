package org.biobrief.pipelines.commands;

import org.biobrief.pipelines.commands.Output.OutputFile;
import org.biobrief.pipelines.commands.Output.OutputItem;
import org.biobrief.util.Constants.IO;

public abstract class AbstractCommand implements Command
{
	protected final CommandTemplate template;
	
	public AbstractCommand(String command)
	{
		this.template=new CommandTemplate(command);
	}
	
	public CommandTemplate getTemplate(){return template;}
	
	protected Command param(String name, String value)
	{
		template.put(name, value);
		return this;
	}
	
	protected Command param(String name, OutputItem file)
	{
		return param(name, file.getFilename());
	}
	
	protected Command param(String name, Object value)
	{
		return param(name, value.toString());
	}
	
	//////////////////////////////////////////////////////
	
	protected Command param(String name, String value, IO io)
	{
		template.put(name, value, io);
		return this;
	}
	
	protected Command param(String name, OutputFile file, IO io)
	{
		return param(name, file.getFilename(), io);
	}
	
	protected Command param(String name, Object value, IO io)
	{
		return param(name, value.toString(), io);
	}
	
	//////////////////////////////////////////////////////
	
	@Override
	public boolean isValid()
	{
		return template.isValid();
	}
	
	@Override
	public void validate()
	{
		template.validate();
	}
	
	@Override
	public String format()
	{
		return template.format();
	}
}
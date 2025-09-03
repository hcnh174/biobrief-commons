package org.biobrief.pipelines.commands;

import java.util.List;
import java.util.Optional;

import org.biobrief.util.CException;
import org.biobrief.util.Constants.IO;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

//run_init_sample.sh --outdir $outdir
public class CommandTemplate
{
	protected final String template;
	protected List<Parameter> parameters=Lists.newArrayList();
	
	public CommandTemplate(String template)
	{
		this.template=template;
		String[] tokens=parse(template);
		for (String token : tokens)
		{
			if (token.startsWith("--"))
				continue;
			if (!token.contains("$"))
				continue;
			token=StringHelper.unquote(token);
			if (token.startsWith("$"))
				addParameter(token.substring(1));
		}
	}
	
	private String[] parse(String template)
	{
		if (!StringHelper.hasContent(template))
			throw new ParsingException("command template is empty: ["+template+"]");
		String[] tokens=template.split("\\s+");
		return tokens;
	}
	
	public void addParameter(String name)
	{
		if (!variableExists(name))
			parameters.add(new Parameter(name));
	}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this); 
	}
	
	public void put(String name, String value)
	{
		if (name.startsWith("$"))
			name=name.substring(1);
		getByName(name).setValue(value);
	}
	
	public void put(String name, String value, IO io)
	{
		if (name.startsWith("$"))
			name=name.substring(1);
		Parameter parameter=getByName(name);
		parameter.setValue(value);
		parameter.setIoType(io);
	}
	
	public String getTemplate(){return template;}
	
	public List<Parameter> getParameters(){return parameters;}
	
	public List<Parameter> getParameters(IO ioType)
	{
		List<Parameter> list=Lists.newArrayList();
		for (Parameter parameter : parameters)
		{
			if (parameter.getIoType()==ioType)
				list.add(parameter);
		}
		return list;
	}
	
	public List<String> getVariableNames()
	{
		List<String> names=Lists.newArrayList();
		for (Parameter parameter : parameters)
		{
			names.add(parameter.getName());
		}
		return names;
	}
	
	public String getValue(String name)
	{
		return getByName(name).getValue();
	}
	
	public String format()
	{
		validate();
		String command=template;
		for (Parameter parameter : parameters)
		{
			command=parameter.format(command);
		}
		return command;
	}
	
	public void validate()
	{
		List<String> missing=Lists.newArrayList();
		List<String> unused=Lists.newArrayList();
		for (Parameter parameter : parameters)
		{
			if (parameter.isMissing())
				missing.add(parameter.getName());
		}
		if (!missing.isEmpty())
			throw new ValidationException(missing, unused);
	}
	
	public boolean isValid()//Map<String, String> values)
	{
		try
		{
			validate();
			return true;
		}
		catch(ValidationException e)
		{
			return false;
		}
	}
	
	/////////////////////////////
		
	public boolean variableExists(String variableName)
	{
		return !findByName(variableName).isEmpty();
	}
	
	/////////////////////////////
	
	public Optional<Parameter> findByName(String name)
	{
		for (Parameter parameter : parameters)
		{
			if (parameter.getName().equals(name))
				return Optional.of(parameter);
		}
		return Optional.empty();
	}
	
	/////////////////////////////
	
	public Parameter getByName(String name)
	{
		Optional<Parameter> parameter=findByName(name);
		if (parameter.isEmpty())
			throw new VariableNotFoundException(name);
		return parameter.get();
	}
	
	/////////////////////////////////////////////////////
	
	public String generateWrapperScript()
	{
		List<String> buffer=Lists.newArrayList();
		buffer.add("#!/bin/bash");
		buffer.add("");
		buffer.add("source /home/nelson/.bashrc");
		buffer.add("source /mnt/hlsg/hlsg-scripts/functions.sh");
		//buffer.add("source $HLSG_SCRIPTS_DIR/functions/functions-longshot.sh");
		buffer.add("");
		buffer.add("#######################################");
		
		buffer.add("");
		for (Parameter param : parameters)
		{
			buffer.add("declare "+param.getName());
		}
		buffer.add("");
		buffer.add("while [[ \"$#\" -gt 0 ]]; do case $1 in");
		for (Parameter param : parameters)
		{
			buffer.add("  --"+param.getName()+") "+param.getName()+"=\"$2\"; shift;;");
		}
		buffer.add("  *) logerr \"Unknown parameter passed: $1\"; exit 1;;");
		buffer.add("esac; shift; done");
		
		buffer.add("");
		for (Parameter param : parameters)
		{
			buffer.add("[[ -z \"$"+param.getName()+"\" ]] && { logerr \"Error: --"+param.getName()+" not set\"; exit 1; }");
		}
		
		buffer.add("");
		for (Parameter param : parameters)
		{
			buffer.add("trace \""+param.getName()+": $"+param.getName()+"\"");
		}
		
		buffer.add("");
		buffer.add("#######################################");
		buffer.add("");
		return StringHelper.join(buffer, "\n");
	}
	
	/////////////////////////////
	
	public static class Parameter
	{
		protected String name;
		protected String value;
		protected IO ioType=IO.input;
		
		public Parameter(String name)
		{
			this.name=name;
		}
	
		public void setValue(final String value)
		{
			if (!StringHelper.hasContent(value))
				throw new ParameterNoContentException(name, value);
			this.value=value;
		}
		
		public String format(String command)
		{
			return StringHelper.replace(command, "$"+name, value);
		}
		
		public boolean isMissing()
		{
			return !StringHelper.hasContent(value);
		}
		
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public String getValue(){return this.value;}
		
		public IO getIoType(){return this.ioType;}
		public void setIoType(final IO ioType){this.ioType=ioType;}
	}
	
	public static class CommandException extends CException
	{
		private static final long serialVersionUID = 1L;

		public CommandException(String message)
		{
			super(message);
		}
	}
	
	public static class ParsingException extends CommandException
	{
		private static final long serialVersionUID = 1L;
		
		public ParsingException(String message)
		{
			super(message);
		}
	}
	
	public static class ParameterNoContentException extends CommandException
	{
		private static final long serialVersionUID = 1L;
		
		public ParameterNoContentException(String name, String value)
		{
			super("parameter no content exception: name=["+name+"] value=["+value+"]");
		}
	}
	
	public static class ParameterNotFoundException extends CommandException
	{
		private static final long serialVersionUID = 1L;
		
		public ParameterNotFoundException(String name)
		{
			super("parameter not found exception: name=["+name+"]");
		}
	}
	
	public static class VariableNotFoundException extends CommandException
	{
		private static final long serialVersionUID = 1L;
		
		public VariableNotFoundException(String name)
		{
			super("parameter not found exception: name=["+name+"]");
		}
	}
	
	public static class ValidationException extends CommandException
	{
		private static final long serialVersionUID = 1L;
		
		public ValidationException(List<String> missing, List<String> unused)
		{
			super("command parameters missing=["+StringHelper.join(missing, ",")+"]; unused=["+StringHelper.join(unused, ",")+"]");
		}
	}
}
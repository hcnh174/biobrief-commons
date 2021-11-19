package org.biobrief.generator.templates;

import java.util.Map;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;
import org.biobrief.util.UnhandledCaseException;

public abstract class AbstractParams
{
	public enum Mode{none, edit, nested, print, search, readonly};
	
	private static final String MODE="mode";
	
	private BeanHelper beanhelper=new BeanHelper();
	protected Mode mode=Mode.none;

	public void setParam(String name, String value)
	{
		try
		{
			if (!StringHelper.hasContent(name) || !StringHelper.hasContent(value))
				return;
			if (name.equals(MODE))// handle in setParams to call mode first and then allow other params to override
				return;
			//System.out.println("setParam name="+name+" value="+value);
			beanhelper.setPropertyFromString(this, name, value);
		}
		catch (Exception e)
		{
			throw new CException("failed to set param: "+name+"="+value);//LogUtil.logUnknownParam(name, this));
		}
	}

	public void setParams(Map<String, Object> values)
	{
		//System.out.println("Params.setParams:\n"+StringHelper.toString(values));
		if (values.containsKey(MODE))
			setMode(values.get(MODE).toString());
		for (String name : values.keySet())
		{
			String value=values.get(name).toString();
			if (name.equals(MODE))
				continue;
			setParam(name, value);
		}
	}

	protected final void setMode(String mode)
	{
		setMode(Mode.valueOf(mode));
	}
	
	protected final boolean setMode(Mode mode)
	{
		//System.out.println("setting mode: "+mode);
		this.mode=mode;
		switch(mode)
		{
		case none:
			return true;
		case edit:
			return setEditMode();
		case nested:
			return setNestedMode();
		case print:
			return setPrintMode();
		case search:
			return setSearchMode();
		case readonly:
			return setReadonlyMode();
		default:
			throw new UnhandledCaseException(mode); 
		}
	}
	
	public boolean setEditMode()
	{
		return true;
	}
	
	public boolean setNestedMode()
	{
		return true;
	}
	
	public boolean setPrintMode()
	{
		return true;
	}
	
	public boolean setSearchMode()
	{
		return true;
	}
	
	public boolean setReadonlyMode()
	{
		return true;
	}
	
	public Mode getMode(){return mode;}
	
	public void validate()
	{
		
	}
	
	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}
}
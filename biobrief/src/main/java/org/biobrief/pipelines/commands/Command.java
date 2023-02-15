package org.biobrief.pipelines.commands;

public interface Command
{
	boolean isValid();
	void validate();
	String format();
	CommandTemplate getTemplate();
}
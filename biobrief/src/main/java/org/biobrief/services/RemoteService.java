package org.biobrief.services;

import java.util.List;

import org.biobrief.util.MessageWriter;

public interface RemoteService
{
	String execute(String command, MessageWriter out);
	List<String> execute(List<String> commands, MessageWriter out);
	int executeLocal(String command, MessageWriter out);
}

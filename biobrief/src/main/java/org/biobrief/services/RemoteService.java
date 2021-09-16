package org.biobrief.services;

import java.util.List;

public interface RemoteService
{
	String execute(String... commands);
	List<String> execute(List<String> commands);
	int executeLocal(String command);
}

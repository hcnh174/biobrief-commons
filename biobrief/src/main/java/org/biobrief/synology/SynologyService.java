package org.biobrief.synology;

import org.biobrief.services.AbstractRemoteService;
import org.biobrief.services.FileService;
import org.biobrief.synology.SynologyHelper.SynogroupGetCommand;
import org.biobrief.synology.SynologyHelper.SynouserGetCommand;
import org.biobrief.synology.SynologyHelper.SynouserLoginCommand;
import org.biobrief.util.LogUtil;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RemoteProperties;
import org.biobrief.util.StringHelper;
import org.springframework.stereotype.Service;

@Service
public class SynologyService extends AbstractRemoteService
{
	private static final String LOGFILE="log-synology.txt";
	
	public SynologyService(FileService fileService, RemoteProperties properties)
	{
		super(fileService, properties);
	}

	public boolean login(String username, String password, MessageWriter out)
	{
		if (!StringHelper.hasContent(username))
			return log("login failed: username is null or empty: ["+username+"]");
		if (!StringHelper.hasContent(password))
			return log("login failed: password is null or empty: ["+password+"]");
		SynouserLoginCommand command=SynologyHelper.login(username, password);
		String response=execute(command.format(), out);
		return command.parse(response);
	}
	
	public SynouserGetCommand.Result getUser(String username, MessageWriter out)
	{
		SynouserGetCommand command=SynologyHelper.getUser(username);
		String response=execute(command.format(), out);
		return SynouserGetCommand.parse(response);
	}
	
	public SynogroupGetCommand.Result getGroup(String group, MessageWriter out)
	{
		SynogroupGetCommand command=SynologyHelper.getGroup(group);
		String response=execute(command.format(), out);
		return SynogroupGetCommand.parse(response);
	}
	
	//////////////////////////////////////////////
	
	private boolean log(String message)
	{
		LogUtil.logMessage(LOGFILE, message);
		return false;
	}
	
	private boolean log(String message, Exception e)
	{
		LogUtil.logMessage(LOGFILE, message, e);
		return false;
	}
}

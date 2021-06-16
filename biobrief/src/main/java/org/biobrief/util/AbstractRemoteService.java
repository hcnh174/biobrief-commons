package org.biobrief.util;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class AbstractRemoteService implements RemoteService
{
	protected final FileService fileService;
	protected final RemoteProperties properties;

	public AbstractRemoteService(FileService fileService, RemoteProperties properties)
	{
		//System.out.println("properties: "+StringHelper.toString(properties));
		this.fileService=fileService;
		this.properties=properties;
	}
	
	@Override
	public List<String> execute(List<String> commands)
	{
		List<String> output=Lists.newArrayList();
		for (String command : commands)
		{
			output.add(execute(command));
		}
		return output;
	}

	@Override
	public String execute(String... commands)
	{
		String command=StringHelper.join(commands, " ");
		System.out.println("command="+command);
		String output=SshHelper.execute(getCredentials(), command);
		System.out.println("output="+output);
		return output;
	}
	
	
	public int executeLocal(String command)
	{
		System.out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("command "+command+" exited with non-zero exit code: "+exitcode);
		return exitcode;
	}
	
	/////////////////////////////////////////////
	
	protected SshHelper.SshCredentials getCredentials()
	{
		return SshHelper.getCredentials(properties.getUsername(), properties.getPassword(), properties.getHost());
	}
}

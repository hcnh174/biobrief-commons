package org.biobrief.services;

import java.util.List;

import org.biobrief.util.CCommandLine;
import org.biobrief.util.CException;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RemoteProperties;
import org.biobrief.util.SshHelper;

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
	public List<String> execute(List<String> commands, MessageWriter out)
	{
		List<String> output=Lists.newArrayList();
		for (String command : commands)
		{
			output.add(execute(command, out));
		}
		return output;
	}

//	@Override
//	public String execute(String... commands)
//	{
//		String command=StringHelper.join(commands, " ");
//		System.out.println("command="+command);
//		String output=SshHelper.execute(getCredentials(), command);
//		System.out.println("output="+output);
//		return output;
//	}
	
	@Override
	public String execute(String command, MessageWriter out)
	{
		//String command=StringHelper.join(commands, " ");
		//System.out.println("command="+command);
		String output=SshHelper.execute(getCredentials(), command, out);
		//System.out.println("output="+output);
		return output;
	}
	
	public int executeLocal(String command, MessageWriter out)
	{
		out.println("command: "+command);
		int exitcode=CCommandLine.execute(command);
		if (exitcode!=0)
			throw new CException("command "+command+" exited with non-zero exit code: "+exitcode);
		return exitcode;
	}
	
	/////////////////////////////////////////////
	
	protected SshHelper.SshCredentials getCredentials()
	{
		return SshHelper.getCredentials(properties.getUsername(), properties.getPassword(),
				properties.getHost(), properties.getPort());
	}
}

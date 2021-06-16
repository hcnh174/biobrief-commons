package org.biobrief.util;

import java.util.List;

//import org.junit.Test;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

//gradle --stacktrace --info :biobrief-util:test --tests *TestHlsgSshHelper
public class TestSshHelper
{
	//@Test
	public void executeLs()
	{
		SshHelper.SshCredentials credentials=getCredentials();
		String command="ls";
		String output=SshHelper.execute(credentials, command);
		System.out.println("ls="+output);
	}
	
	//@Test
	public void executePwd()
	{
		SshHelper.SshCredentials credentials=getCredentials();
		String command="pwd";
		String output=SshHelper.execute(credentials, command);
		System.out.println("pwd="+output);
	}
	
	//@Test
	public void executeSinfo()
	{
		SshHelper.SshCredentials credentials=getCredentials();
		String command="sinfo";
		String output=SshHelper.execute(credentials, command);
		System.out.println("sinfo="+output);
	}
	
	//////////////////////////////////////////////

	private SshHelper.SshCredentials getCredentials()
	{
		return SshHelper.getCredentials("username", "password", "url");
	}
}
	
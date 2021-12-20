package org.biobrief.util;

//gradle --stacktrace --info test --tests *TestHlsgSshHelper
public class TestSshHelper
{
	//@Test
	public void executeLs()
	{
		MessageWriter out=new MessageWriter();
		SshHelper.SshCredentials credentials=getCredentials();
		String command="ls";
		String output=SshHelper.execute(credentials, command, out);
		System.out.println("ls="+output);
	}
	
	//@Test
	public void executePwd()
	{
		MessageWriter out=new MessageWriter();
		SshHelper.SshCredentials credentials=getCredentials();
		String command="pwd";
		String output=SshHelper.execute(credentials, command, out);
		System.out.println("pwd="+output);
	}
	
	//@Test
	public void executeSinfo()
	{
		MessageWriter out=new MessageWriter();
		SshHelper.SshCredentials credentials=getCredentials();
		String command="sinfo";
		String output=SshHelper.execute(credentials, command, out);
		System.out.println("sinfo="+output);
	}
	
	//////////////////////////////////////////////

	private SshHelper.SshCredentials getCredentials()
	{
		return SshHelper.getCredentials("username", "password", "url", 22);
	}
}
	
package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle --info test --tests *TestSshHelper
public class TestSshHelper
{
	@Test
	public void executeTree()
	{
		MessageWriter out=new MessageWriter();
		SshHelper.SshCredentials credentials=getCredentials();
		String command="cd /mnt/reports; tree -f -h -D --timefmt '%FT%T' -P '*.docx|*.pdf|*.xlsx|*.pptx' -i --noreport | grep -v -e \"#recycle\"";
		String output=SshHelper.execute(credentials, command, out);
		System.out.println("tree="+output);
	}
	
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
		String username=RuntimeHelper.getEnvironmentVariable("ANALYSIS_SERVER_USERNAME", true);
		String password=RuntimeHelper.getEnvironmentVariable("ANALYSIS_SERVER_PASSWORD", true);
		String host=RuntimeHelper.getEnvironmentVariable("ANALYSIS_SERVER_HOST", true);
		System.out.println("username="+username);
		System.out.println("password="+password);
		System.out.println("host="+host);
		return SshHelper.getCredentials(username, password, host, 22);
	}
}
	
//https://github.com/hierynomus/sshj
package org.biobrief.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.PasswordUtils;
import net.schmizz.sshj.xfer.FileSystemFile;

//ssh -l nhayes sutil.hgc.jp -i ~/.ssh/shirokane_openssh.ppk ls out
//ssh -l nhayes sutil.hgc.jp -i ~/.ssh/shirokane.ppk ls out
//https://stackoverflow.com/questions/7580083/sshj-example-of-public-key-auth-from-file
//https://github.com/hierynomus/sshj/issues/159
public class SshHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(SshHelper.class);
	private static final boolean enabled=true;
	
	public static SshCredentials getCredentials(String username, String password, String host, Integer port)
	{
		return new SshHelper.SshCredentials(username, password, host, port);
	}
	
	public static SshCredentials getCredentials(String username, String password, String host, Integer port, String keyfile)
	{
		return new SshHelper.SshCredentials(username, password, host, port, keyfile);
	}
	
	//http://www.thegeeky.space/2014/02/how-to-use-ls-bash-command-10-tips-and-tricks.html
	//http://giantdorks.org/alain/make-ls-output-date-in-long-iso-format-instead-of-short-month/
	//https://unix.stackexchange.com/questions/141480/ls-content-of-a-directory-ignoring-symlinks
	public static List<FileInfo> listFiles(SshCredentials credentials, String dir, MessageWriter out)
	{
		//ls -l | grep -v ^l
		//String command="ls -lh --time-style long-iso --block-size=K "+dir+" | grep -v ^l";
		String command="ls -lhL --time-style long-iso --block-size=K "+dir;
		String str=execute(credentials, command, out);
		return FileInfo.parse(str);
	}
	
	//-rw-r--r-- 1 nhayes hgc0895 227906K 2018-05-19 03:25 [01;31mSam1-1_S1_L001_R2_001.fastq.gz[0m
	public static class FileInfo
	{
		public static final String DATETIME_PATTERN="yyyy-MM-dd' 'HH:mm";//2018-05-19 03:25
		
		private String filename;
		private String permissions;
		private String user;
		private String group;
		private Integer size;// kilobytes
		private Date date;
		
		public String getFilename(){return this.filename;}
		public String getPermissions(){return this.permissions;}
		public String getUser(){return this.user;}
		public String getGroup(){return this.group;}
		public Integer getSize(){return this.size;}
		public Date getDate(){return this.date;}
		
		public static List<FileInfo> parse(String str)
		{
			List<FileInfo> files=Lists.newArrayList();
			for (String line : StringHelper.splitLines(str))
			{
				//System.out.println("line="+line);
				String[] tokens = line.split("\\s+");
				//System.out.println("parts="+tokens.length);
				if (tokens.length!=8)
					continue;
				FileInfo info=new FileInfo();
				info.permissions=tokens[1];
				info.user=tokens[2];
				info.group=tokens[3];
				info.size=MathHelper.parseInt(StringHelper.chomp(tokens[4]));
				info.date=DateHelper.parse(tokens[5]+" "+tokens[6], DATETIME_PATTERN);
				info.filename=tokens[7];
				files.add(info);
			}
			return files;
		}
		
		public static Optional<FileInfo> findFile(List<FileInfo> files, String filename)
		{
			for (FileInfo file : files)
			{
				if (file.getFilename().equals(filename))
					return Optional.of(file);
			}
			return Optional.empty();
		}
	}
	
//	public static String execute(SshCredentials credentials, List<String> commands)
//	{
//		String command= StringHelper.join(commands, ";\\\n");
//		return execute(credentials, command);
//	}
	
	public static List<String> execute(SshCredentials credentials, List<String> commands, MessageWriter out)
	{
		List<String> output=Lists.newArrayList();
		for (String command : commands)
		{
			output.add(execute(credentials, command, out));
		}
		return output;
	}
	
	//https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/Exec.java
	public static String execute(SshCredentials credentials, String command, MessageWriter out)
	{
		String logfile=logCommand(command, out);
		if (!enabled)
			return "";
		SSHClient client=null;
		Session session=null;
		try
		{
			client=createClient(credentials);
			session = client.startSession();
			Command cmd = session.exec(command);
			
			String output=IOUtils.readFully(cmd.getInputStream()).toString();
			String err=IOUtils.readFully(cmd.getErrorStream()).toString();
			
			Integer status=cmd.getExitStatus();
			
			log(logfile, "output="+output, out);
			log(logfile, "err="+err, out);
			log(logfile, "status="+status, out);

			if (status!=null && status!=0)
				throw new CException("ssh command return non-zero exit status: status="+status+"; command="+command);
			return output;
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			destroySession(session);
			destroyClient(client);
		}
	}
	
	public static String logCommand(String command, MessageWriter out)
	{
		String filename=LogUtil.getLogDir()+"/ssh/"+new Date().getTime()+".sh";
		//out.println(command);
		FileHelper.writeFile(filename, command);
		return filename;
	}
	
	private static void log(String logfile, String message, MessageWriter out)
	{
		//out.println(message);
		FileHelper.appendFile(logfile, message);
	}
	
	//https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/SCPUpload.java
	public static void upload(SshCredentials credentials, String filename, String destdir)
	{
		SSHClient client=null;
		try
		{
			client=createClient(credentials);
			client.newSCPFileTransfer().upload(new FileSystemFile(filename), destdir);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			destroyClient(client);
		}
	}
	
	//https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/SCPDownload.java
	public static void download(SshCredentials credentials, String filename, String destdir)
	{
		SSHClient client=null;
		try
		{
			client=createClient(credentials);
			client.newSCPFileTransfer().download(filename, new FileSystemFile(destdir));
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			destroyClient(client);
		}
	}
	
//	private static SSHClient createClient(SshCredentials credentials) throws IOException
//	{
//		SSHClient client = new SSHClient();
//		client.loadKnownHosts();
//		File privateKey=new File(credentials.keyfile);
//		PasswordFinder finder=PasswordUtils.createOneOff(credentials.password.toCharArray());
//		KeyProvider keys=client.loadKeys(privateKey.getPath(), finder);
//		client.connect(credentials.host);
//		client.authPublickey(credentials.username, keys);
//		return client;
//	}
	
	//https://www.programcreek.com/java-api-examples/?api=net.schmizz.sshj.SSHClient
	//https://stackoverflow.com/questions/3630101/could-not-load-known-hosts-exception-using-sshj
	private static SSHClient createClient(SshCredentials credentials) throws IOException
	{
		//System.out.println("credentials: "+StringHelper.toString(credentials));
		SSHClient client = new SSHClient();
		//client.loadKnownHosts();
		//client.addHostKeyVerifier("public-key-fingerprint");
		client.addHostKeyVerifier(new PromiscuousVerifier());
		PasswordFinder finder=PasswordUtils.createOneOff(credentials.password.toCharArray());
		if (credentials.keyfile.isPresent())
		{
			//System.out.println("using private key: "+credentials.keyfile);
			File privateKey=new File(credentials.keyfile.get());
			KeyProvider keys=client.loadKeys(privateKey.getPath(), finder);
			client.connect(credentials.host, credentials.port);
			client.authPublickey(credentials.username, keys);
		}
		else
		{
			//System.out.println("using password");// "+credentials.password);
			client.addHostKeyVerifier(new PromiscuousVerifier());
			client.connect(credentials.host, credentials.port);
			client.authPassword(credentials.username, credentials.password);
			
		}
		return client;
	}
	
	private static void destroyClient(SSHClient client)
	{
		try
		{
			if (client!=null)
			{
				client.disconnect();
				client.close();
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}
	
	private static void destroySession(Session session)
	{
		try
		{
			if (session != null)
				session.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static class SshCredentials
	{
		private final String username;
		private final String password;
		private final String host;
		private final Integer port;
		private final Optional<String> keyfile;

		public SshCredentials(String username, String password, String host, Integer port, String keyfile)
		{
			this.username=username;
			this.password=password;
			this.host=host;
			this.port=port;
			this.keyfile=Optional.ofNullable(keyfile);
		}
		
		public SshCredentials(String username, String password, String host, Integer port)
		{
			this.username=username;
			this.password=password;
			this.host=host;
			this.port=port;
			this.keyfile=Optional.empty();
		}
	}
}

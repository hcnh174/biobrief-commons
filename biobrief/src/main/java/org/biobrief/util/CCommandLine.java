package org.biobrief.util;

import java.io.File;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

//http://commons.apache.org/proper/commons-exec/tutorial.html
//https://stackoverflow.com/questions/36319241/java-parse-output-of-execute-command
//https://wrapper.tanukisoftware.com/doc/english/child-exec.html
//http://www.speakingcs.com/2014/06/executing-external-programs-from-java.html
//https://www.baeldung.com/apache-commons-chain
@Data
public class CCommandLine
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(CCommandLine.class);
	
	protected CommandLine commandLine=null;
//	protected File workingDir=null;
//	protected Integer watchdog=null;
//	protected Integer exitValue=null;
	protected boolean throwExceptionIfOutputStream=false;
	protected boolean throwExceptionIfErrorStream=false;
//	protected boolean cygwin=false;
	protected boolean debug=true;
	protected PlatformType.Platform platform=PlatformType.find().getPlatform();

	////////////////////////////////////////////////
	
	public static int execute(String command)
	{
		CCommandLine cmd=new CCommandLine(command);
		return cmd.execute();
	}
	
	////////////////////////////
	
	public CCommandLine(String command)
	{
		System.out.println("command="+command);
		this.commandLine = CommandLine.parse(command);
	}
	
	public CCommandLine(List<String> commands)
	{
		addArgs(commands);
	}

//	public CommandLine getCommandLine()
//	{
//		if (this.cygwin && this.platform==PlatformType.Platform.WINDOWS)
//			return getCygwinCommandLine(this.commandLine);
//		return this.commandLine;
//	}
	
	public static CommandLine getCygwinCommandLine(CommandLine commandLine)
	{
		CCommandLine commands=new CCommandLine("bash");
		commands.addArg("--login");
		commands.addArg("-c");
		commands.addArg(commandLine.toString(), true);
		return commands.getCommandLine();
	}
	
	public void addArg(Object value)
	{
		if (value==null)
			return;
		this.commandLine.addArgument(value.toString());
	}
	
	public void addArg(Object value, boolean handleQuoting)
	{
		if (value==null)
			return;
		this.commandLine.addArgument(value.toString(),handleQuoting);
	}
	
	public void addArgIf(boolean addarg, Object value)
	{
		if (!addarg)
			return;
		if (value==null)
			return;
		this.commandLine.addArgument(value.toString());
	}
	
	public void addArgs(List<String> args)
	{
		for (String arg : args)
		{
			addArg(arg);
		}
	}
	
	public void addArg(String name, Object value)
	{
		if (value==null)
			return;
		addArg(name);
		addArg(value);
	}
	
	public void addArgIf(boolean addarg, String name, Object value)
	{
		if (!addarg)
			return;
		if (value==null)
			return;
		addArg(name);
		addArg(value);
	}
	
//	public void setWorkingDir(String dir)
//	{
//		setWorkingDir(new File(dir));
//	}
	
	public int execute()
	{
		try
		{
			if (debug)
			{
				System.out.println("ENVIRONMENT");
				System.out.println(StringHelper.toString(EnvironmentUtils.getProcEnvironment()));
			}
			return createExecutor().execute(getCommandLine());
		}
		catch(Exception e)
		{
			throw new CException("Command failed: "+commandLine.toString(), e);
		}
	} 
	
//	public File getWorkingDir(){return this.workingDir;}
//	public void setWorkingDir(final File workingDir){this.workingDir=workingDir;}
//	
//	public Integer getWatchdog(){return this.watchdog;}
//	public void setWatchdog(int watchdog){this.watchdog=watchdog;}
//	
//	public Integer getExitValue(){return this.exitValue;}
//	public void setExitValue(int exitValue){this.exitValue=exitValue;}
//	
//	public boolean getThrowExceptionIfOutputStream(){return this.throwExceptionIfOutputStream;}
//	public void setThrowExceptionIfOutputStream(final boolean throwExceptionIfOutputStream){this.throwExceptionIfOutputStream=throwExceptionIfOutputStream;}
//	
//	public boolean getThrowExceptionIfErrorStream(){return this.throwExceptionIfErrorStream;}
//	public void setThrowExceptionIfErrorStream(final boolean throwExceptionIfErrorStream){this.throwExceptionIfErrorStream=throwExceptionIfErrorStream;}
//	
//	public boolean getCygwin(){return this.cygwin;}
//	public void setCygwin(final boolean cygwin){this.cygwin=cygwin;}
//	
//	public PlatformType.Platform getPlatform(){return this.platform;}
//	public void setPlatform(final PlatformType.Platform platform){this.platform=platform;}
	
	public DefaultExecutor createExecutor()
	{
		//Executor exec = DefaultExecutor.builder().get();
		DefaultExecutor.Builder<?> builder=DefaultExecutor.builder();
		//if (this.workingDir!=null)
	//		builder.setWorkingDirectory(this.workingDir);
//		if (this.exitValue!=null)
//			builder.setExitValue(this.exitValue);
//		if (this.watchdog!=null)
//			builder.setWatchdog();
		DefaultExecutor exec=builder.get();
		//exec.setWatchdog(new ExecuteWatchdog(this.watchdog));
		return exec;
	}
	
	
	
//	public DefaultExecutor createExecutor()
//	{
//		DefaultExecutor.builder();
//		if (this.workingDir!=null)
//			DefaultExecutor.builder().setWorkingDirectory(this.workingDir);
//		if (this.exitValue!=null)
//			DefaultExecutor.builder().setExitValue(this.exitValue);
//		if (this.watchdog!=null)
//			DefaultExecutor.builder().setWatchdog()
//			builder.setWatchdog(new ExecuteWatchdog(this.watchdog));
//		return builder.get();
//	} 
	
//	@SuppressWarnings("deprecation")
//	public DefaultExecutor createExecutor()
//	{
//		DefaultExecutor executor = new DefaultExecutor();
//		if (this.workingDir!=null)
//			executor.setWorkingDirectory(this.workingDir);
//		if (this.exitValue!=null)
//			executor.setExitValue(this.exitValue);
//		if (this.watchdog!=null)
//			executor.setWatchdog(new ExecuteWatchdog(this.watchdog));
//		return executor;
//	} 
	
	@Override
	public String toString()
	{
		return this.commandLine.toString();
	}
	
//	public static boolean isWindows() 
//	{
//		return System.getProperty("os.name").toLowerCase().startsWith("windows");
//	}
	
	public static class Output
	{
		protected int exitValue;
		protected String out;
		protected String err;
				
		public int getExitValue(){return this.exitValue;}
		public void setExitValue(final int exitValue){this.exitValue=exitValue;}
		
		public String getOut(){return this.out;}
		public void setOut(final String out){this.out=out;}

		public String getErr(){return this.err;}
		public void setErr(final String err){this.err=err;}
		
		public boolean hasOutput()
		{
			return StringHelper.hasContent(this.out);
		}
		
		public boolean hasError()
		{
			return StringHelper.hasContent(this.err);
		}
		
		public void dump()
		{
			System.out.println("exitValue: "+getExitValue());
			System.out.println("stdout: ["+getOut()+"]");
			System.out.println("stderr: ["+getErr()+"]");
		}
	}
}

package org.biobrief.util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;

import com.google.common.collect.Iterables;

//https://github.com/zeroturnaround/zt-exec
//https://github.com/zeroturnaround/zt-process-killer
public final class RuntimeHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(RuntimeHelper.class);
	
	private RuntimeHelper(){}
	
	public static Optional<String> getEnvironmentVariable(String name) 
	{
		String value=System.getenv(name);
		//System.out.println("env name="+name+" value="+value);
		if (StringHelper.hasContent(value))
			return Optional.of(value);
		return Optional.empty();
	}
	
	public static String getEnvironmentVariable(String name, boolean strict) 
	{
		Optional<String> value=getEnvironmentVariable(name);
		if (value.isEmpty() && strict)
			throw new CException("environment variable not set: "+name);
		return value.get();
	}
	
	public static String getEnvironmentVariable(String name, String dflt) 
	{
		Optional<String> value=getEnvironmentVariable(name);
		if (value.isPresent())
			return value.get();
		return dflt;
	}
	
	public static boolean isWindows() 
	{
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
	
	public static String execute(List<String> commands)
	{
		return execute(Iterables.toArray(commands, String.class));
	}
	
	public static String execute(String...commands)
	{
		if (commands.length==0)
			throw new CException("execute requires at least one command. array is empty");
		try
		{
			return new ProcessExecutor()
				.command(commands)
				.readOutput(true)
				.execute().outputUTF8();
		}
		catch (IOException | TimeoutException | InterruptedException e)
		{
			throw new CException(e);
		}
	}
	
	public static void killProcess(int pid)
	{
		try
		{
			PidProcess process = Processes.newPidProcess(pid);
			ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 30, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
		}
		catch (IOException | InterruptedException | TimeoutException e)
		{
			throw new CException(e);
		}
	}
}

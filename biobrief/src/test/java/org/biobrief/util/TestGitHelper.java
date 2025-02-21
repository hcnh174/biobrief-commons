package org.biobrief.util;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.biobrief.util.GitHelper.GitLog;
import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *GitHelper
public class TestGitHelper
{
	//private String LOGFILE="/mnt/out/logs/gitlog.txt";
	private String LOGFILE="z:/logs/gitlog.txt";
	
	//@Test
	public void getGitLogCommand()
	{
		String command=GitHelper.getGitLogCommand("~/workspace/hucgc", LOGFILE);
		System.out.println("command="+command);
	}
	
	@Test
	public void parseGitLog()
	{
		GitLog log=GitHelper.parseGitLog(LOGFILE);
		System.out.println("git log:\n"+JsonHelper.toJson(log));
	}
}

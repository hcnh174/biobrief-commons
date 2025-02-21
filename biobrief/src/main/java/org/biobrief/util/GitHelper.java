package org.biobrief.util;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

public final class GitHelper
{
	private GitHelper(){}

	// outfile="/mnt/out/logs/gitlog.txt"
	public static String getGitLogCommand(String dir, String outfile)
	{
		//String command="cd "+dir+";";
		String command="git log --format=medium --max-count=100 --abbrev-commit --date=unix > "+outfile;
		return command;
	}

	public static GitLog parseGitLog(String logfile)
	{
		GitLog log=new GitLog();
		GitLog.Commit commit=new GitLog.Commit("");
		for (String line : FileHelper.readLines(logfile))
		{
			//System.out.println("line=["+line+"]");
			if (line.matches("commit [a-z0-9]+"))
			//if (line.startsWith("commit"))
				commit=log.add(parseCommitLine(line));
			else if (line.startsWith("Author:"))
			{
				commit.setAuthor(parseAuthor(line));
				commit.setEmail(parseEmail(line));
			}
			else if (line.startsWith("Date:"))
				commit.setDate(parseDateLine(line));
			else if (!StringHelper.hasContent(line))
				continue;
			else commit.appendMessage(line.trim());
		}
		return log;
	}
	
	private static String parseCommitLine(String line)
	{
		return StringHelper.remove(line, "commit").trim();
	}
	
	private static String parseAuthor(String line)
	{
		String value=StringHelper.remove(line, "Author:").trim();
		return value.substring(0, value.indexOf("<")).trim();
	}
	
	private static String parseEmail(String line)
	{
		return line.substring(line.indexOf("<")+1, line.indexOf(">"));
	}
	
	private static Date parseDateLine(String line)
	{
		String value=StringHelper.remove(line, "Date:").trim();
		return new Date(Long.parseLong(value));
	}
	
	@Data
	public static class GitLog
	{
		protected List<Commit> commits=Lists.newArrayList();
		
		public Commit add(String name)
		{
			System.out.println("name="+name);
			Commit commit=new Commit(name);
			this.commits.add(commit);
			return commit;
		}

		@Data
		public static class Commit
		{
			protected String name;
			protected String author;
			protected String email;
			protected Date date;
			protected String message;
			
			public Commit(String name)
			{
				this.name=name;
				this.message="";
			}
			
			public void appendMessage(String message)
			{
				if (StringHelper.hasContent(this.message))
					this.message+="\n";
				this.message+=message;
			}
		}
	}
}

package org.biobrief.synology;

import java.util.List;

import org.biobrief.util.CException;
import org.biobrief.util.MathHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

//https://global.download.synology.com/download/Document/Software/DeveloperGuide/Firmware/DSM/All/enu/Synology_DiskStation_Administration_CLI_Guide.pdf?_ga=2.232053785.729724569.1636343797-137552518.1631750977
// synouser {--help}
//	synouser {--add} username passwd full name expired email app privilege
//	synouser {--del} username...
//	synouser {--rename} old username new username
//	synouser {--modify} username passwd full name expired email
public class SynologyHelper
{
	///////////////////
	
	public static SynouserLoginCommand login(String username, String password)
	{
		return new SynouserLoginCommand(username, password);
	}
	
	public static SynouserGetCommand getUser(String username)
	{
		return new SynouserGetCommand(username);
	}
	
	public static SynogroupGetCommand getGroup(String group)
	{
		return new SynogroupGetCommand(group);
	}
	
	public static SynouserAddCommand addUser(String username, String password, String name, String email)
	{
		SynouserAddCommand command=new SynouserAddCommand(username);
		command.setPassword(password);
		command.setFullname(name);
		command.setEmail(email);
		return command;
	}
	
	public static SynogroupAddCommand addGroup(String group)//, List<String> usernames)
	{
		return new SynogroupAddCommand(group);//, usernames);
	}
	
	public static SynogroupMembersCommand addUsersToGroup(String group, List<String> usernames)
	{
		return new SynogroupMembersCommand(group, usernames);
	}
	
	////////////////////////////////////////////////////////////
	
	public static String format(List<AbstractCommand> commands)
	{
		StringBuilder buffer=new StringBuilder();
		for (SynologyHelper.AbstractCommand command : commands)
		{
			buffer.append(command.format());
		}
		return buffer.toString();
	}
	
	//////////////////
	
	@Data
	public abstract static class AbstractCommand
	{			
		public String format()
		{
			check();
			StringBuilder buffer=new StringBuilder();
			format(buffer);
			return buffer.toString();
		}
		
		public abstract void format(StringBuilder buffer);
		
		public void check(){}

		//User Type   : [AUTH_LOCAL]
		protected static String extractBrackets(String line)
		{
			int start=StringHelper.indexOf(line, "[");
			int end=StringHelper.indexOf(line, "]", start);
			return line.substring(start+1, end);
		}
		
		//(100) users
		protected static String extractGroup(String line)
		{
			int start=StringHelper.indexOf(line, "(");
			int end=StringHelper.indexOf(line, ")", start);
			return line.substring(end+1).trim();
		}
	}
	
	////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true) // fires if exit code is no
	public static class IfExitCodeCommand extends AbstractCommand
	{
		protected Integer exitcode;
		protected List<AbstractCommand> ifCommands=Lists.newArrayList();
		protected List<AbstractCommand> elseCommands=Lists.newArrayList();
		
		public IfExitCodeCommand(Integer exitcode)
		{
			this.exitcode=exitcode;
		}
		
		public IfExitCodeCommand()
		{
			this(0);
		}
		
		public void addIfCommand(AbstractCommand command)
		{
			this.ifCommands.add(command);
		}
		
		public void addElseCommand(AbstractCommand command)
		{
			this.elseCommands.add(command);
		}
		
		public void format(StringBuilder buffer)
		{
			buffer.append("if [ \"$?\" -ne \""+exitcode+"\" ]; then\n");
			for (AbstractCommand command : ifCommands)
			{
				buffer.append("  "+command.format());
			}
			if (!elseCommands.isEmpty())
			{
				buffer.append("else\n");
				for (AbstractCommand command : elseCommands)
				{
					buffer.append("  "+command.format());
				}
			}
			buffer.append("fi\n");
		}
		
		@Override
		public void check()
		{
			super.check();
			StringHelper.checkHasContent(exitcode);
			if (ifCommands.isEmpty())
				throw new CException("at least one command needed for if statement");
		}
	}
	
	///////////////////////////////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class SynouserCommand extends AbstractCommand
	{
		protected String username;
		
		public SynouserCommand(String username)
		{
			this.username=username;
		}
		
		@Override
		public void format(StringBuilder buffer)
		{
			buffer.append("sudo /usr/syno/sbin/synouser");
		}
		
		@Override
		public void check()
		{
			super.check();
			StringHelper.checkHasContent(username);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynouserGetCommand extends SynouserCommand
	{		
		public SynouserGetCommand(String username)
		{
			super(username);
		}
		
		//synouser {--add} username passwd full name expired email app privilege
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --get");
			buffer.append(" "+username);
			buffer.append("\n");
		}
		
		
		/*
				User Name   : [testuser]
		User Type   : [AUTH_LOCAL]
		User uid    : [1031]
		Primary gid : [100]
		Fullname    : [Test User]
		User Dir    : [/var/services/homes/testuser]
		User Shell  : [/sbin/nologin]
		Expired     : [false]
		User Mail   : [testuser@testuser.net]
		Alloc Size  : [148]
		Member Of   : [1]
		(100) users
		*/
		public static Result parse(String str)
		{
			Result result=new Result();
			for (String line : StringHelper.splitLines(str))
			{
				if (line.startsWith("User Name"))
					result.setUsername(extractBrackets(line));
				else if (line.startsWith("User Type"))
					result.setUsertype(extractBrackets(line));
				else if (line.startsWith("User uid"))
					result.setUid(Integer.parseInt(extractBrackets(line)));
				else if (line.startsWith("Primary gid"))
					result.setGid(Integer.parseInt(extractBrackets(line)));
				else if (line.startsWith("Fullname"))
					result.setFullname(extractBrackets(line));
				else if (line.startsWith("User Dir"))
					result.setDir(extractBrackets(line));
				else if (line.startsWith("User Shell"))
					result.setShell(extractBrackets(line));
				else if (line.startsWith("Expired"))
					result.setExpired(Boolean.parseBoolean(extractBrackets(line)));
				else if (line.startsWith("User Mail"))
					result.setEmail(extractBrackets(line));
				else if (line.startsWith("Alloc Size"))
					result.setAllocSize(Integer.parseInt(extractBrackets(line)));
				else if (line.startsWith("("))
					result.addGroup(extractGroup(line));
			}
			return result;
		}
		
		@Data
		public static class Result
		{
			protected String username;
			protected String usertype;
			protected Integer uid;
			protected Integer gid;
			protected String fullname;
			protected String dir;
			protected String shell;
			protected Boolean expired;
			protected String email;
			protected Integer allocSize;
			protected List<String> groups=Lists.newArrayList();
			
			public void addGroup(String group)
			{
				this.groups.add(group);
			}
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynouserAddCommand extends SynouserCommand
	{
		protected String password;
		protected String fullname;
		protected Boolean expired=false;
		protected String email;
		protected Boolean ftp=false;//FTP Value 0x01
		protected Boolean filestation=true;//� File Station Value 0x02
		protected Boolean audiostation=false;//� Audio Station Value 0x04
		protected Boolean downloadstation=true;//� Download Station Value 0x08
		
		public SynouserAddCommand(String username)
		{
			super(username);
		}
		
		//synouser {--add} username passwd full name expired email app privilege
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --add");
			buffer.append(" "+username);
			buffer.append(" '"+StringHelper.escapeSingleQuotesBash(password)+"'");
			buffer.append(" '"+StringHelper.escapeSingleQuotesBash(fullname)+"'");
			buffer.append(" ").append(expired ? "1" : "0");
			buffer.append(" '"+StringHelper.dflt(email)+"'");
			buffer.append(" "+getAppPrivilege());
			buffer.append("\n");
		}
		
		private Integer getAppPrivilege()
		{
			char value=0;
			if (ftp)
				value+=0x01;
			if (filestation)
				value+=0x02;
			if (audiostation)
				value+=0x04;
			if (downloadstation)
				value+=0x08;
			return (int)value;
		}
		
		@Override
		public void check()
		{
			super.check();
			StringHelper.checkHasContent(password);
			StringHelper.checkHasContent(fullname);
			StringHelper.checkHasContent(expired);
			//StringHelper.checkHasContent(email);// optional?
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynouserLoginCommand extends SynouserCommand
	{
		protected String password;
		
		public SynouserLoginCommand(String username, String password)
		{
			super(username);
			this.password=password;
		}
		
		//sudo synouser --login username 'passwd'
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --login");
			buffer.append(" "+username);
			buffer.append(" '"+StringHelper.escapeSingleQuotesBash(password)+"'");
			buffer.append("\n");
		}
		
		@Override
		public void check()
		{
			super.check();
			StringHelper.checkHasContent(password);
		}
		
		public boolean parse(String str)
		{
			str=str.trim();
			if (str.equals("LOGIN OK."))
				return true;
			if (str.equals("LOGIN failed."))
				return false;
			throw new CException("unexpected login response: ["+str+"]");
		}
	}
	
	//////////////////////////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract static class SynogroupCommand extends AbstractCommand
	{
		protected String group;
		
		public SynogroupCommand(String group)
		{
			this.group=group;
		}
		
		@Override
		public void format(StringBuilder buffer)
		{
			buffer.append("sudo  /usr/syno/sbin/synogroup");
		}
		
		@Override
		public void check()
		{
			super.check();
			StringHelper.checkHasContent(group);
		}
	}
	
	/////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynogroupAddCommand extends SynogroupCommand
	{
		public SynogroupAddCommand(String group)
		{
			super(group);
		}
		
		//synogroup --add groupname...
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --add");
			buffer.append(" "+group);
			buffer.append("\n");
		}
	}
	
	///////////////////////////////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynogroupGetCommand extends SynogroupCommand
	{
		public SynogroupGetCommand(String group)
		{
			super(group);
		}
		
		//synogroup --get groupname
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --get");
			buffer.append(" "+group);
			buffer.append("\n");
		}
		
		public static Result parse(String str)
		{
			Result result=new Result();
			for (String line : StringHelper.splitLines(str))
			{
				if (line.startsWith("Group Name"))
					result.setGroup(extractBrackets(line));
				else if (line.startsWith("Group Type"))
					result.setGrouptype(extractBrackets(line));
				else if (line.startsWith("Group ID"))
					result.setGid(Integer.parseInt(extractBrackets(line)));
				else if (MathHelper.isInteger(line.substring(0, line.indexOf(":"))))
					result.addMember(extractBrackets(line));
			}
			return result;
		}
		
		@Data
		public static class Result
		{
			protected String group;
			protected String grouptype;
			protected Integer gid;
			protected List<String> members=Lists.newArrayList();
			
			public void addMember(String member)
			{
				this.members.add(member);
			}
		}
	}
	
	//////////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public static class SynogroupMembersCommand extends SynogroupCommand
	{
		protected List<String> usernames=Lists.newArrayList();
		
		public SynogroupMembersCommand(String group, List<String> usernames)
		{
			super(group);
			this.usernames.addAll(usernames);
		}
		
		//synogroup --member groupname username1 username2 username3
		@Override
		public void format(StringBuilder buffer)
		{
			super.format(buffer);
			buffer.append(" --member");
			buffer.append(" "+group);
			buffer.append(" "+StringHelper.join(usernames, " "));
			buffer.append("\n");
		}
		
		@Override
		public void check()
		{
			super.check();
			if (usernames.isEmpty())
				throw new CException("username list is empty");
		}
	}
}

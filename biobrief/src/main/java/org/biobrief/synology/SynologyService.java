package org.biobrief.synology;

import org.biobrief.services.AbstractRemoteService;
import org.biobrief.services.FileService;
import org.biobrief.synology.SynologyHelper.SynogroupGetCommand;
import org.biobrief.synology.SynologyHelper.SynouserGetCommand;
import org.biobrief.synology.SynologyHelper.SynouserLoginCommand;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.RemoteProperties;
import org.springframework.stereotype.Service;

@Service
public class SynologyService extends AbstractRemoteService
{
	public SynologyService(FileService fileService, RemoteProperties properties)
	{
		super(fileService, properties);
	}

	public boolean login(String username, String password, MessageWriter out)
	{
		SynouserLoginCommand command=SynologyHelper.login(username, password);
		String response=execute(command.format(), out);
		return command.parse(response);
	}
	
	public SynouserGetCommand.Result getUser(String username, MessageWriter out)
	{
		SynouserGetCommand command=SynologyHelper.getUser(username);
		String response=execute(command.format(), out);
		return SynouserGetCommand.parse(response);
	}
	
	public SynogroupGetCommand.Result getGroup(String group, MessageWriter out)
	{
		SynogroupGetCommand command=SynologyHelper.getGroup(group);
		String response=execute(command.format(), out);
		return SynogroupGetCommand.parse(response);
	}
	
	/*
	public String generateSynologyUsersScript(MessageWriter out)
	{
		String script=userLoader.createScript(out);
		String filename=CoreConstants.TMP_DIR+"/synology.sh";
		FileHelper.writeFile(filename, script);
		return filename;
	}
	public String createScript(MessageWriter out)
	{
		Map<String, User> users=dao.asUsernameMap(dao.findAll());
		UserSpreadsheetReader.read(properties.getUserSpreadsheet(), users, out);
		StringDataFrame passwords=UserPasswordReader.read(properties.getUserPasswords(), out);
		//FileHelper.writeFile(GENERATED_PASSWORD_FILE);
		
		List<User> list=Lists.newArrayList();
		for (User user : users.values())
		{
			Optional<String> password=findOrCreatePassword(passwords, user, out);
			if (password.isEmpty())
				continue;
			user.setPassword(password.get());
			list.add(user);
		}
		return createScript(list);
	}
	
	public static String createScript(List<User> users)
	{
		List<SynologyHelper.AbstractCommand> commands=Lists.newArrayList();
		List<String> expertpanel=Lists.newArrayList();
		for (User user : users)
		{
			expertpanel.add(user.getUsername());
			//if (user.getAdministrators())
			//	continue;
			commands.add(SynologyHelper.getUser(user.getUsername()));
			SynologyHelper.IfExitCodeCommand command=new SynologyHelper.IfExitCodeCommand();
			command.addIfCommand(SynologyHelper.addUser(user.getUsername(), user.getPassword(),
					user.getName(), user.getEmail()));
			commands.add(command);
		}
		commands.add(SynologyHelper.addGroup("expertpanel"));
		commands.add(SynologyHelper.addUsersToGroup("expertpanel", expertpanel));
		return SynologyHelper.format(commands);
	}
	*/
}

package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.biobrief.services.EmailService;
import org.biobrief.services.NotificationService2;
import org.biobrief.services.SmtpEmailServiceImpl;
import org.biobrief.users.entities.User;
import org.biobrief.util.SyncFusionHelper.FileManager;
import org.biobrief.util.SyncFusionHelper.FileManager.ActionRequest;
import org.biobrief.util.SyncFusionHelper.FileManager.ReadResponse;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

//gradle --stacktrace --info test --tests *TestSyncFusionHelper
public class TestSyncFusionHelper
{
	@Test
	public void readRoot()
	{
		read("/schedule/");
	}
	
	@Test
	public void readSubdir()
	{
		read("/schedule/meeting-2021-06-22/");
	}
	
	@Test
	public void readTerminalDir()
	{
		read("/schedule/meeting-2021-06-22/A105813309232/");
	}
	
	private void read(String path)
	{
		VirtualFileSystem vfs=TestVirtualFileSystem.createVirtualFileSystem();
		//EmailService emailService=new SmtpEmailServiceImpl();
//		NotificationService2 notificationService=new NotificationService2(emailService, false,
//			"nobody@nowhere.com", Lists.newArrayList("nobody@nowhere.com"), Lists.newArrayList());

		ActionRequest request=new ActionRequest();
		request.setPath(path);
		
		FileManager manager=new FileManager(vfs, null, null);//, notificationService);
		User user=new User("id", "user", "password");
		ReadResponse response=manager.read(request, user);
		String json=JsonHelper.toJson(response);
		//FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp"+"/files.json", json);
		System.out.println(json);
		assertThat(true).isEqualTo(true);
	}

	/*
	//@Test
	public void read()
	{
		FileManager manager=new FileManager("x:");
		manager.skipPrefix(".");
		manager.skipSuffix(".dat");
		manager.skipSuffix(".yaml");

		manager.skipDir("呉医療センター（C）");
		manager.skipDir("県立広島病院（B）");
		manager.skipDir("2020年12月説明会資料");
		manager.skipDir("【B】県立広島病院");
		manager.skipDir("【C】呉医療センター");
		manager.skipDir("【D】広島市立安佐市民病院");
		manager.skipDir("123_広島大学病院_クリーンアップ");
		manager.skipDir("ctdb");
		manager.skipDir("dojo");
		manager.skipDir("OLDA014809812710");
		manager.skipDir("trashbox");
		manager.skipDir("BAKmeetings");
		manager.skipDir(".DS_Store");

		manager.skipBefore(DateHelper.addMonths(new Date(), -1));
		
		//System.out.println("isAfter: "+manager.isAfter("x:/HU20190007"));
		
		ActionRequest request=new ActionRequest();
		request.setPath("/meetings/");
		ReadResponse response=manager.read(request);
		FileHelper.writeFile(Constants.BIOBRIEF_DIR+"/.temp/tmp"+"/files.json", JsonHelper.toJson(response));
		//System.out.println(response);
		assertThat(true).isEqualTo(true);
	}
	
	@Test
	public void parseRead()
	{
		String filename=Constants.BASE_DIR+"/biobrief-util/src/test/resources/filemanager-read.json";
		ActionRequest request=JsonHelper.parseFile(filename, ActionRequest.class);
		System.out.println("request: "+JsonHelper.toJson(request));
	}
	*/
}


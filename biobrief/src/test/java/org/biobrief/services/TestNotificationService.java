package org.biobrief.services;

import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestNotificationService
public class TestNotificationService
{
	@Test 
	public void get()
	{
		EmailService emailService=null;
		String configfile="q:/config/notifications.yaml";
		NotificationService2 notificationService=new NotificationService2(emailService, configfile);
		MessageWriter out=new MessageWriter();
		
		NotificationService2.NotificationConfig config=notificationService.getConfig();
		System.out.println("config="+JsonHelper.toJson(config));
		notificationService.notify("report_load_started", StringHelper.createMap("report", "ABC012345_F1"), out);
	}
}

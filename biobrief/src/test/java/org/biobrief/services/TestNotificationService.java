package org.biobrief.services;

import org.biobrief.util.Context;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.junit.jupiter.api.Test;


//gradle --stacktrace --info test --tests *TestNotificationService
public class TestNotificationService
{
	@Test 
	public void testNotify()
	{
		EmailService emailService=new MockEmailServiceImpl();
		String configfile="q:/config/notifications.yaml";
		//String configfile="q:/config/notifications-test.yaml";
		NotificationService notificationService=new NotificationService(emailService, configfile, true);
		Context context=new Context("hcnh174", new MessageWriter());
		
		NotificationService.NotificationConfig config=notificationService.getConfig();
		System.out.println("config="+JsonHelper.toJson(config));
		notificationService.notify("report_load", "report loaded", "ABC012345_F1", context);
		
		notificationService.notify("app", "app started", "app started", context); 
	}
}

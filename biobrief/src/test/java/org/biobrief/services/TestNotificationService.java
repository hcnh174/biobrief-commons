package org.biobrief.services;

import org.biobrief.services.NotificationService2.Model;
import org.biobrief.util.Context;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.junit.jupiter.api.Test;


//gradle --stacktrace --info test --tests *TestNotificationService
public class TestNotificationService
{
	@Test 
	public void get()
	{
		EmailService emailService=null;
		String configfile="q:/config/notifications2.yaml";
		NotificationService2 notificationService=new NotificationService2(emailService, configfile, true);
		Context context=new Context("hcnh174", new MessageWriter());
		
		NotificationService2.NotificationConfig config=notificationService.getConfig();
		System.out.println("config="+JsonHelper.toJson(config));
		Model model=new Model();
		model.put("report", "ABC012345_F1");
		notificationService.notify("report_load", model, context); 
	}
}

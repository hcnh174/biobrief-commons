package org.biobrief.services;

import org.biobrief.services.NotificationService.Model;
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
		NotificationService notificationService=new NotificationService(emailService, configfile, true);
		Context context=new Context("hcnh174", new MessageWriter());
		
		NotificationService.NotificationConfig config=notificationService.getConfig();
		System.out.println("config="+JsonHelper.toJson(config));
		Model model=new Model();
		model.put("report", "ABC012345_F1");
		notificationService.notify("report_load", model, context); 
	}
}

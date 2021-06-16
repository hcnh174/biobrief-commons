package org.biobrief.util;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService
{	
	void sendEmail(String from, List<String> to, String subject, String body);
	void sendEmail(String from, String to, String subject, String body);
	void sendEmail(SimpleMailMessage message);
	//static void logEmail(SimpleMailMessage email);
	//static void logEmailError(SimpleMailMessage email, Exception e);
}

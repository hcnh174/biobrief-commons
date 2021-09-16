package org.biobrief.services;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService
{	
	void sendEmail(String from, List<String> to, String subject, String body);
	void sendEmail(String from, String to, String subject, String body);
	void sendEmail(SimpleMailMessage message);
}

package org.biobrief.services;

import java.util.List;

import org.biobrief.util.MessageWriter;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService
{	
	void sendEmail(String from, List<String> to, String subject, String body, MessageWriter out);
	void sendEmail(String from, String to, String subject, String body, MessageWriter out);
	void sendEmail(SimpleMailMessage message, MessageWriter out);
}

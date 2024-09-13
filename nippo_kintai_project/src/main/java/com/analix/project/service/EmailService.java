package com.analix.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);
	}
	
	public void sendEmails(List<Users> unsubmittedUsers, Users manager) {
		//マネージャーに未提出者一覧を送信
		String managerMessage ="未提出者一覧:" + unsubmittedUsers.stream()
		.map(Users::getName).collect(Collectors.joining(","));
		sendEmail(manager.getEmail(),"未提出者一覧",managerMessage);
		//未提出者に通知を送信
		for(Users users: unsubmittedUsers) {
			String userMessage = "本日の日報・勤怠が未提出です。早急に提出してください。";
			sendEmail(users.getEmail(),"未提出通知",userMessage);
		}
		
	}

}

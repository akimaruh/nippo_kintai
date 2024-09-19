package com.analix.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	/**
	 * アラート送信
	 * @param to 宛先
	 * @param subject 件名
	 * @param content 本文
	 */
	public void alert(String to, String subject, String content) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setSubject(subject);
		email.setText(content);
		try {
			mailSender.send(email);
			System.out.println("メール送信成功");
		} catch (Exception e) {
			System.out.println("メール送信失敗: " + e.getMessage());
		}
	}
}


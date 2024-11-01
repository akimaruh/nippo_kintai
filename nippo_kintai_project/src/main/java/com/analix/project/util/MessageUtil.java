package com.analix.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MessageUtil {
	@Value("${app.baseUrl}")
    private String baseUrl;

	/**
	 * メールの共通メッセージ
	 * @return 
	 */
	public static final String mailCommonMessage() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		 
		String message = "\n" +
				"下記よりご確認ください。\n" +
				baseUrl + "\n" +
				"※当メールは送信専用となっております。";

		return message;
	}
	
	public String mailCommonMessageForBatch() {
		 
		String message ="\n" +"\n" +
				"下記よりご確認ください。\n" +
				baseUrl + "\n" +
				"※当メールは送信専用となっております。";

		return message;
	}
	
	
//	public static final 
	
	

}

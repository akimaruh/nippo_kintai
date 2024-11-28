package com.analix.project.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.analix.project.entity.Users;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {
	
	@Autowired
	private HttpSession session;
	
	public Users getUser() {
		return (Users) session.getAttribute("loginUser");
	}
	
	public void setUser(Users user) {
		session.setAttribute("loginUser", user);
	}
	
	
	
	// 削除
//	public void removeUser() {
//		session.removeAttribute("loginUser");
//	}
	
	// セッションが有効か確認する
//	public boolean isSessionValid() {
//		return session != null && session.getAttribute("loginUser") != null;
//	}
	
}

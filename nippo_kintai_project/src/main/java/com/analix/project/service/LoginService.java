package com.analix.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.LoginMapper;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;

	//	ユーザーIDとパスワードを使用してユーザーを検索
	public Users findByIdAndPassword(Integer id, String password) {
		return loginMapper.findByIdAndPassword(id, password);
	}
	
	
//	// パスワードをハッシュ化
//	private String hashPassword(String password) {
//		 return new BCryptPasswordEncoder().encode(password);
//	}
	

	
}

package com.analix.project.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.LoginMapper;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;

	public Users findByIdAndPassword(String employeeCode, String password) {
		Integer code = Integer.parseInt(employeeCode);
		return loginMapper.findByCodeAndPassword(code, password);
	}

	// 利用開始日チェック
	public boolean isDate(Users user) {
		if (user.getStartDate() == null) {
			return false;
		}
		return user.getStartDate().isBefore(LocalDate.now());
	}

	//	// パスワードをハッシュ化
	//	private String hashPassword(String password) {
	//		 return new BCryptPasswordEncoder().encode(password);
	//	}

}

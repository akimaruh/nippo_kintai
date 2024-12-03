package com.analix.project.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.LoginMapper;
import com.analix.project.util.PasswordUtil;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;
	@Autowired
	private PasswordUtil passwordUtil;

	public Users findByIdAndPassword(String employeeCode, String password) {
		Integer employeeCodeInteger = Integer.parseInt(employeeCode);
		String hashPassword = passwordUtil.getSaltedAndStrechedPassword(password, employeeCode);
		
		//マッパーで使えるパスワード
		//セッションに入れてセッションの中をフィルタークラスで確認し仮パスワード作成日を確認する。
		Users loginUser = loginMapper.findByCodeAndPassword(employeeCodeInteger, hashPassword);
		System.out.println(hashPassword);
		System.out.println(loginUser);
		return loginUser;  

	}

	// 利用開始日チェック
	public boolean isDate(LocalDate startDate) {
		if (startDate == null) {
			return false;
		}
		return startDate.isAfter(LocalDate.now());
	}

}

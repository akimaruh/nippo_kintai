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
		String strechedPassword = passwordUtil.getSaltedAndStrechedPassword(password, employeeCode);
		//仮パスワードの場合
		if (password.length() == 8) {
			return loginMapper.findByCodeAndPassword(employeeCodeInteger, password);
		}
		return loginMapper.findByCodeAndPassword(employeeCodeInteger, strechedPassword);

	}

	// 利用開始日チェック
	public boolean isDate(Users user) {
		if (user.getStartDate() == null) {
			return false;
		}
		return user.getStartDate().isBefore(LocalDate.now());
	}

}

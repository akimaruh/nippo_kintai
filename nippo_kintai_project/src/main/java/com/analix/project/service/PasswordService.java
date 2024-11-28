package com.analix.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.MessageUtil;
import com.analix.project.util.PasswordUtil;

@Service
public class PasswordService {

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private PasswordUtil passwordUtil;

	/**
	 * パスワード再発行
	 * @param employeeCodeString
	 * @param email
	 */
	public void reissuePassword(String employeeCodeString, String email) {

		Integer employeeCode = null;
		try {
			employeeCode = Integer.parseInt(employeeCodeString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		Integer userId = userMapper.findIdByEmployeeCodeAndEmail(employeeCode, email);
		System.out.println(userId);
		if (userId != null) {
			Users users = new Users();
			users.setId(userId);
			users.setPassword(passwordUtil.getRandomPassword());
			if (userMapper.updateUserData(users)) {
				emailService.sendReissuePassword(email, users.getPassword(), MessageUtil.mailCommonMessage());
			}
		}

	}

	/**
	 * パスワード変更
	 * @param id
	 * @param pastPassword
	 * @param newPassword
	 */
	public boolean changePassword(Integer id, Integer employeeCode, String newPassword) {

		Users users = new Users();
		String employeeCodeString = String.valueOf(employeeCode);
		String strechedPassword = passwordUtil.getSaltedAndStrechedPassword(newPassword, employeeCodeString);
		users.setId(id);
		users.setPassword(strechedPassword);
		boolean isUpdate = userMapper.updateUserData(users);
		return isUpdate;
	}
}

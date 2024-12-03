package com.analix.project.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.TemporaryPassword;
import com.analix.project.mapper.TemporaryPasswordMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.MessageUtil;
import com.analix.project.util.PasswordUtil;

@Service
public class PasswordService {

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private TemporaryPasswordMapper temporaryPasswordMapper;
	@Autowired
	private PasswordUtil passwordUtil;

	/**
	 * パスワード再発行
	 * @param employeeCodeString
	 * @param email
	 */
	public void reissuePassword(String employeeCodeString, String email) {
		if (!employeeCodeString.matches("^[0-9]*$") || !email.contains("@")) {
			return;
		}

		Integer employeeCode = null;
		try {
			employeeCode = Integer.parseInt(employeeCodeString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		Integer userId = userMapper.findIdByEmployeeCodeAndEmail(employeeCode, email);
		System.out.println(userId);

		if (userId != null) {
			String temporaryPass = passwordUtil.getTemporaryPassword();
			boolean isRegist = false;
			TemporaryPassword temporaryPassword = new TemporaryPassword();
			temporaryPassword.setUserId(userId);
			temporaryPassword
					.setTemporaryPassword(passwordUtil.getSaltedAndStrechedPassword(temporaryPass, employeeCodeString));
			temporaryPassword
					.setExpirationDateTime(LocalDateTime.now().plusHours(Constants.TEMP_PASSWORD_EXPIRE_HOURS));
			//レコードにユーザーIDが存在したら該当レコードに上書きする
			if (temporaryPasswordMapper.exsistTemporaryPasswordTable(userId)) {
				System.out.println("更新"+temporaryPassword);
				isRegist = temporaryPasswordMapper.updateTemporaryPassword(temporaryPassword);
			} else {
				System.out.println("新規"+temporaryPassword);
				isRegist = temporaryPasswordMapper.insertTemporaryPassword(temporaryPassword);
			}
			if (isRegist) {
				emailService.sendReissuePassword(email, temporaryPass, MessageUtil.mailCommonMessage());
			}
			//不正アクセス対策のため失敗してもエラーを出さない
		}
	}

	/**
	 * パスワード変更
	 * @param id
	 * @param pastPassword
	 * @param newPassword
	 */
	public boolean changePassword(Integer id, Integer employeeCode, String newPassword) {
		String employeeCodeString = String.valueOf(employeeCode);
		String strechedPassword = passwordUtil.getSaltedAndStrechedPassword(newPassword, employeeCodeString);

		boolean isUpdate = userMapper.updatePassword(id, strechedPassword);
		return isUpdate;
	}
}

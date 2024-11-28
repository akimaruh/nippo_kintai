package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.analix.project.entity.Users;
import com.analix.project.service.PasswordService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PasswordController {
	
	@Autowired
	PasswordService passwordService;

	/**
	 * 『パスワードを忘れた方はこちら』リンク押下後
	 * @return 仮パスワード発行画面
	 */
	@GetMapping("/password/reissue")
	public String reissuePassword() {
		System.out.println("きてるよー");
		return "password/reissue";
	}

	/**
	 * 『送信』ボタン押下後
	 * @param employeeCode
	 * @param mail
	 * @param model
	 * @return 送信完了メッセージ画面
	 */
	@PostMapping("/password/reissue/complete")
	public String submitMailAddress(String employeeCode, String mail, Model model) {
		passwordService.reissuePassword(employeeCode, mail);
		model.addAttribute("message", "仮パスワードを送信しました。メールフォルダをご確認ください。");
		return "password/reissue";
	}

	/**
	 * パスワード変更ボタン押下時・初回ログイン後
	 * @return パスワード変更画面
	 */
	@GetMapping("/password/change")
	public String changePassword() {
		return "password/regist";
	}

	/**
	 * 『変更』ボタン押下後
	 * @param newPassword
	 * @param confirmPassword
	 * @param model
	 * @param session
	 * @return 変更成功・失敗メッセージ
	 */
	@PostMapping("/password/change/complete")
	public String completeChangePassword(String newPassword, String confirmPassword, Model model, HttpSession session) {
		Users loginUser = (Users) session.getAttribute("loginUser");

		if (newPassword.equals(confirmPassword)) {
			boolean isChange = passwordService.changePassword(loginUser.getId(), loginUser.getEmployeeCode(),
					newPassword);
			if (isChange) {
				model.addAttribute("message", "パスワード変更が完了しました。");
			} else {
				model.addAttribute("error", "パスワード変更に失敗しました。");
			}

		} else {
			model.addAttribute("error", "パスワードが一致しません。");
		}
		return "password/regist";
	}

}

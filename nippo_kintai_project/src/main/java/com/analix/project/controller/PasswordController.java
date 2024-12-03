package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.Users;
import com.analix.project.service.PasswordService;
import com.analix.project.util.Constants;
import com.analix.project.util.SessionHelper;

@Controller
public class PasswordController {

	@Autowired
	PasswordService passwordService;
	@Autowired
	SessionHelper sessionHelper;

	/**
	 * 『パスワードを忘れた方はこちら』リンク押下後
	 * @return 仮パスワード発行画面
	 */
	@GetMapping("/password/reissue")
	public String reissuePassword() {
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
	public String submitMailAddress(String employeeCode, String email, Model model) {
		try {
			passwordService.reissuePassword(employeeCode, email);
		} catch (Exception e) {
			e.getStackTrace();
		}
		model.addAttribute("message", "仮パスワードを送信しました。メールフォルダをご確認ください。");

		return "common/login";
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
	public String completeChangePassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, Model model,
			RedirectAttributes redirectAttributes) {
		Users loginUser = (Users) sessionHelper.getUser();

		if (newPassword == "" || confirmPassword == "") {
			model.addAttribute("error", "パスワードを入力して下さい。");
			return "password/regist";
		}
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "パスワードが一致しません。");
			return "password/regist";

		} else {

			boolean isChange = passwordService.changePassword(loginUser.getId(), loginUser.getEmployeeCode(),
					newPassword);
			if (!isChange) {
				model.addAttribute("error", "パスワード変更に失敗しました。");
				return "password/regist";
			} else {
				model.addAttribute("message", "パスワード変更が完了しました。");

				//セッション情報を更新
				loginUser.setActiveFlg(Constants.INACTIVE_FLG);
				sessionHelper.setUser(loginUser);
			}
		}
		return "redirect:/common/startMenu";
	}

}

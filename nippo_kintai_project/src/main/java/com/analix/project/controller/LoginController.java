package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.entity.Users;
import com.analix.project.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginService;

	@GetMapping("/")
	public String getLogin(Model model) {
		model.addAttribute("error", false);
		return "common/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("id")Integer id, @RequestParam("password") String password,
			HttpSession session, Model model) {
		Users user = loginService.findByIdAndPassword(id, password);

		if (user != null) {
			session.setAttribute("loginUser", user);
			String role = user.getRole();
			
//			Integer userId = user.getId();
//	        Integer status = attendanceService.findStatusByUserId(userId);
//	        session.setAttribute("status", status);

			// 権限がadminの場合ユーザー管理画面へ遷移
			if ("Admin".equals(role)) {
				return "redirect:/user/regist";
				// 権限がuserの場合勤怠登録画面へ遷移
			} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
				return "redirect:/attendance/regist";
				// その他の場合にはエラー処理などを行う
			} else {
				model.addAttribute("error", "ログインに失敗しました。");
				return "common/login";
			}

		} else {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}
	}

//	// ログアウト処理
//	@GetMapping("/logout")
//	public String logout(HttpSession session) {
//		session.invalidate(); // セッションを無効化
//		return "redirect:/login"; // ログインページへリダイレクト
//	}
}
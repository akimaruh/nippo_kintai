package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.service.LoginService;
import com.analix.project.util.SessionHelper;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private SessionHelper sessionHelper;

	@GetMapping("/")
	public String getLogin(Model model, HttpSession session) {
		return "common/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("employeeCode") String employeeCode, @RequestParam("password") String password,
			HttpSession session, Model model) {
		System.out.println(sessionHelper.getUser());
		String loginResult = loginService.handleLogin(employeeCode, password);

		if (loginResult.equals("error")) {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}
		//ログイン通過
		////ログイン完了後遷移(コメントアウト中)
		////権限による画面遷移の可能性を考え、現状権限で分岐する書き方で進める
		//		if ("Admin".equals(role)) {
		//			return "redirect:/common/startMenu";
		//		} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
		//			return "redirect:/common/startMenu";
		//		}
		//	}
		return "redirect:/common/startMenu";
	}

	@GetMapping("/timeout")
	public String timeout(Model model, HttpSession session) {
		session.invalidate(); // セッションを無効化
		model.addAttribute("message", "タイムアウトしました。再ログインしてください。");
		return "common/login";
	}

	//	// ログアウト処理
	//	@GetMapping("/logout")
	//	public String logout(HttpSession session) {
	//		session.invalidate(); // セッションを無効化
	//		return "redirect:/login"; // ログインページへリダイレクト
	//	}
}
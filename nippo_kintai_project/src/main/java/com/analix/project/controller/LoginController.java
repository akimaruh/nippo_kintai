package com.analix.project.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public String getLogin(Model model, HttpSession session) {

		model.addAttribute("error", false);
		return "common/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("id") String id, @RequestParam("password") String password,
			HttpSession session, Model model) {

		// ユーザーIDチェック
		String idRegex = "^[0-9]{1,16}$";
		Pattern idPattern = Pattern.compile(idRegex);
		Matcher idMatcher = idPattern.matcher(id);
		if (!idMatcher.matches()) {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}

		// パスワードチェック
		String passRegex = "^[0-9a-zA-Z]{1,16}$";
		Pattern passPattern = Pattern.compile(passRegex);
		Matcher passMatcher = passPattern.matcher(password);
		if (!passMatcher.matches()) {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}

		Users user = new Users();
		user = loginService.findByIdAndPassword(id, password);

		if (user != null) {

			// 利用開始日チェック
			if (loginService.isDate(user) == false) {
				model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
				return "common/login";
			}
			
			session.setAttribute("loginUser", user);
			
			String role = user.getRole();
			//ログイン完了後遷移
			//権限による画面遷移の可能性を考え、現状権限で分岐する書き方で進める
			if ("Admin".equals(role)) {
				return "common/startMenu";

			} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
				return "common/startMenu";

			} else {
				model.addAttribute("error", "ログインに失敗しました。");
				return "common/login";
			}

		} else {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}
	}
	@GetMapping("/timeout")
	public String timeout(Model model,HttpSession session) {

		model.addAttribute("message","タイムアウトしました。再ログインしてください。");
		return "common/login";
	}

	//	// ログアウト処理
	//	@GetMapping("/logout")
	//	public String logout(HttpSession session) {
	//		session.invalidate(); // セッションを無効化
	//		return "redirect:/login"; // ログインページへリダイレクト
	//	}
}
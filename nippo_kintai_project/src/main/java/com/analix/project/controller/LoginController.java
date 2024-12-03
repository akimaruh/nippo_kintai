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

		session.invalidate(); // セッションを無効化
		model.addAttribute("error", false);
		return "common/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("employeeCode") String employeeCode, @RequestParam("password") String password,
			HttpSession session, Model model) {
		System.out.println("ログインボタン押下後"+(Users)session.getAttribute("loginUser") =="" ? null:(Users)session.getAttribute("loginUser"));
		// ユーザーIDチェック
		String employeeCodeRegex = "^[0-9]{1,9}$";
		Pattern employeeCodePattern = Pattern.compile(employeeCodeRegex);
		Matcher employeeCodeMatcher = employeeCodePattern.matcher(employeeCode);
		if (!employeeCodeMatcher.matches()) {
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

		Users loginUser = loginService.findByIdAndPassword(employeeCode, password);
		//セッション用に値をセット
		//TODO:セッションを利用するときはsessionHelperクラスをAutowiredするように修正する。
		//TODO:sessionHelperクラスのAutowired修正が全て完了したらsession.setAttributeにloginUserクラスを代入する。
		
		if (loginUser != null) {
//			Users user = new Users();
//			user.setId(loginUser.getId());
//			user.setEmployeeCode(loginUser.getEmployeeCode());
//			user.setName(loginUser.getName());
//			user.setRole(loginUser.getRole());
//			user.setExpirationDateTime(loginUser.getExpirationDateTime());
//			user.setStartDate(loginUser.getStartDate());
//			System.out.println(user);
			// 利用開始日チェック
			if (loginService.isDate(loginUser.getStartDate())) {
				model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
				return "common/login";
			}
			String role =loginUser.getRole();
			session.setAttribute("loginUser",loginUser);
			sessionHelper.setUser(loginUser);
			//ログイン完了後遷移
			//権限による画面遷移の可能性を考え、現状権限で分岐する書き方で進める
			if ("Admin".equals(role)) {
				return "redirect:/common/startMenu";
			} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
				return "redirect:/common/startMenu";
			}
		}
		model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
		return "common/login";

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
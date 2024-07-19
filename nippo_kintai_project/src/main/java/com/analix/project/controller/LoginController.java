package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.entity.Users;
import com.analix.project.service.LoginService;

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
	public String login(@RequestParam Integer id, @RequestParam String password, Model model) {
		Users user = loginService.findByIdAndPassword(id, password);
		if(user != null) {
			 model.addAttribute("user", user);
			 return "redirect:/attendance/regist";
		}else {
			model.addAttribute("error", "ユーザーIDまたはパスワードが正しくありません");
			return "common/login";
		}
	}

//	// ログイン画面への遷移
//	@GetMapping("/")
//	public String getLogin() {
//		return "common/login";
//	}
//
//	@PostMapping("/login")
//	public String login(@ModelAttribute("user") Users user, HttpSession session, Model model) {
//		Users authenticatedUser = loginService.findByIdAndPassword(user.getId(), user.getPassword());
//		System.out.println("ここまでOK");
//
//		if (authenticatedUser != null) {
//			session.setAttribute("user", authenticatedUser);
//			System.out.println("ログイン成功");
//
//			// ログイン成功時の処理
//			String role = authenticatedUser.getRole();
//
//			if ("admin".equals(role)) {
//				// 権限がadminの場合
//				return "redirect:/user/regist"; // ユーザー管理画面へ遷移
//			} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
//				// 権限がuserの場合
//				return "redirect:/attendance/regist"; // 勤怠登録画面へ遷移
//			} else {
//				// その他の場合にはエラー処理などを行う
//				model.addAttribute("error", "ログインに失敗しました。");
//				return "common/login";
//			}
//		} else {
//			// ログイン失敗時
//			System.out.prinln("ログイン失敗");
//			model.addAttribute("error", "IDまたはパスワードが正しくありません。");
//			return "common/login"; // 再度ログイン画面へ遷移
//		}
//	}
//	
}
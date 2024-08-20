package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/common")
public class StartMenuController {

	/**
	 * 初期表示
	 * @return
	 */
	@RequestMapping(path = "/startMenu")
	public String showMenu(HttpSession session) {

		return "common/startMenu";
	}

	/**
	 * 『ログオフ』ボタン押下後
	 * @return ページを閉じる
	 */
	@RequestMapping(path = "/logoff")
	public String logoff(HttpSession session) {
		session.invalidate();
		return "redirect:/login";//JavaScriptで閉じる
	}
}

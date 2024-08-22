package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class StartMenuController {

	/**
	 * 初期表示
	 * @return
	 */
	@RequestMapping(path = "/common/startMenu")
	public String showMenu(HttpSession session) {

		return "common/startMenu";
	}

	/**
	 * 『ログオフ』ボタン押下後
	 * @return ページを閉じる
	 */
	@RequestMapping(path = "/common/logoff", method = RequestMethod.POST)
	public String logoff(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		redirectAttributes.addFlashAttribute("message", "ログオフしました。再度ログインしてください。");

		return "redirect:/";//JavaScriptで閉じないならこっち
	}
}

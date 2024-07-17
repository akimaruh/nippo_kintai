package com.analix.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	// 初期画面
	@RequestMapping(path = "")
	public String index() {
		// 現在のユーザーの認証情報を取得
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// ユーザーがログインしている場合
		
//		if (authentication != null && authentication.isAuthenticated()) {
//			return "redirect:/attendance/regist";
//		}
		return "common/login"; // ユーザーがログインしていない場合、"/login"にリダイレクする
	}

	// ログインボタン押下
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login() {
		return "common/login";
	}
}

//ログインボタン押下→勤怠登録画面が表示される
//登録されている情報と一致しない場合、エラーメッセージを画面に表示する
//チェック
//存在チェック
//ユーザID,パスワードがシステムに登録されていること
//有効期間チェック
//システム登録されているユーザIDが有効期間切れでないこと
//ログインボタン押下→入力チェック
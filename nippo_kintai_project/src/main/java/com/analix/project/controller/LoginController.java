package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	// 初期画面
	@RequestMapping(path = "")
	public String index() {
		return "common/login";
	}

	// ログインボタン押下
	@RequestMapping("/login")
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
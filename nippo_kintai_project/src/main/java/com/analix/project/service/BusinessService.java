package com.analix.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;

@Service
public class BusinessService {
	
	@Autowired
	AlertService alertService;
	@Autowired
	UserMapper userMapper;
	
//	public void someCriticalMethod() {
//		try {
//			// 何らかの処理
//		} catch (Exception e) {
//			//障害発生時のアラート
//			List<Users> adminList = userMapper.findUserListByRole(Constants.CODE_VAL_ADMIN);
//			for (Users admin : adminList) {
//				String subject = "システム障害アラート";
//				String content = "エラーが発生しました:" + e.getMessage();
//				System.out.println(e.getMessage());
//
//				alertService.alert(admin.getEmail(), subject, content);
//			}
//		}
//	}
	
	public void someCriticalMethod() {
		try {
			// 何らかの処理
			 
//		} catch (SQLException e) {
//			// データベース関連の障害
//			handleAlert("データベース接続エラー", e);
			
		} catch (NullPointerException e) {
			// Nullポインタエラー
			handleAlert("Nullポインタ例外が発生しました", e);
			
		} catch (Exception e) {
			// その他の障害
			handleAlert("不明なエラーが発生しました", e);
		}
	}
	
	private void handleAlert(String errorType, Exception e) {
		// 障害発生時のアラート
		List<Users> adminList = userMapper.findUserListByRole(Constants.CODE_VAL_ADMIN);
		for (Users admin : adminList) {
			String subject = "システム障害アラート";
			String content = errorType + "e,getMessage()" + "\n" + "詳細: " + e.toString();
			
			alertService.alert(admin.getEmail(), subject, content);
		}
	}

}

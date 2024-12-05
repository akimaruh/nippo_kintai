package com.analix.project.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.analix.project.entity.Users;
import com.analix.project.service.PasswordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptorによる処理を実装するクラス
 */
@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {
	@Autowired
	PasswordService passwordService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {// 各Controllerの処理が始まる前に呼ばれる
		Users loginUser = (Users) request.getSession().getAttribute("loginUser");
		LocalDateTime now = LocalDateTime.now();
		if (loginUser == null) {
			if (!request.getRequestURI().contains("/password/reissue")) { // 必要なセッション情報を確認
				response.setStatus(HttpServletResponse.SC_FOUND);
				response.sendRedirect("/timeout"); // 無ければトップページにリダイレクト
				return false; // Controllerは起動しない
			}
		} else if (loginUser.getExpirationDateTime() != null && loginUser.getActiveFlg() == 1
				&& loginUser.getExpirationDateTime().isAfter(now)) {
			response.setStatus(HttpServletResponse.SC_FOUND);
			response.setHeader("layout", "redirect");
			response.sendRedirect("/password/change");
			return false;
		}

		return true;
	}

}

package com.analix.project.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptorによる処理を実装するクラス
 */
@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {// 各Controllerの処理が始まる前に呼ばれる
		System.out.println("URI:" + request.getRequestURI());
		System.out.println("URL:" + request.getRequestURL());
		if (!request.getRequestURI().contains("/password/reissue")
				&& request.getSession().getAttribute("loginUser") == null) { // 必要なセッション情報を確認
			response.setStatus(HttpServletResponse.SC_FOUND);
			response.sendRedirect("/timeout"); // 無ければトップページにリダイレクト
			return false; // Controllerは起動しない
		}

		return true;
	}

}

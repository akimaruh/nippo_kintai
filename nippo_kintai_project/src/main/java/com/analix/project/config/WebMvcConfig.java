package com.analix.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * InterceptorをSpringの設定に追加するクラス
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Autowired
	private SessionTimeoutInterceptor sessionTimeoutInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(sessionTimeoutInterceptor) // interceptorを追加
			.addPathPatterns("/**") // 全てのパスパターンを対象に追加
			.excludePathPatterns("/","/login","/timeout", "/resources/**","/css/style.css"); // ログイン画面を対象から除外
			
	}

}

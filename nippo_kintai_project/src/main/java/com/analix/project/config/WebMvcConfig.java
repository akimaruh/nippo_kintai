package com.analix.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * InterceptorをSpringの設定に追加するクラス
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Bean
	public SessionTimeoutInterceptor SessionTimeoutInterceptor() {
		return new SessionTimeoutInterceptor();
	}
	
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(SessionTimeoutInterceptor()) // interceptorを追加
			.addPathPatterns("/*") // 全てのパスパターンを対象に追加
			.excludePathPatterns("/","/login","/timeout"); // ログイン画面を対象から除外
			
	}

}

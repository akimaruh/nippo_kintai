package com.analix.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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
	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		// メッセージファイルを読込むための設定を記載します
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		// 「setBasename」を使用することで任意のファイル名に変更することも可能です
		messageSource.setBasename("classpath:ValidationMessages");
		// 「setDefaultEncoding」を使用することで任意の文字コードに変更することも可能です
		messageSource.setDefaultEncoding("UTF-8");
		validator.setValidationMessageSource(messageSource);
		return validator;
	}

}

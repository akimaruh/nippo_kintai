package com.analix.project.config;
//
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//		http.formLogin(login -> login
//				//ログインフォームの送信先URL
//				.loginProcessingUrl("/")
//				//ログイン画面として表示されるURL
//				.loginPage("/login")
//				//ログイン成功後のリダイレクトURL
//				.defaultSuccessUrl("/common/startMenu", true)
//				//ログイン失敗時のリダイレクトURL
////				.failureForwardUrl("/login?error")
//				//ログイン関連のURLへのアクセス許可
//				.permitAll()
//
//		).logout(logout -> logout
//				//ログアウトのURL
//				.logoutUrl("/common/logoff")
//				//ログアウト成功後のリダイレクト先
//				.logoutSuccessUrl("/")
//				.permitAll()
//				
//		).authorizeHttpRequests(authz -> authz
//				//スタティックリソースへのアクセスを許可
//				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//				//これらのURLへのアクセスを許可
//				.requestMatchers("/", "/login", "/timeout", "/password/**", "/common/**", "/**").permitAll()
//				//それ以外のリクエストには認証が必要
//				.anyRequest().authenticated()
//				);
//
//		return http.build();
//	}
//	
//}
//

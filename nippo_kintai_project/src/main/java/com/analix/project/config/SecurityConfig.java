//package com.analix.project.config;
//
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration // このクラスは設定クラスである
//@EnableWebSecurity // Webセキュリティを有効にする
//public class SecurityConfig {
//
//	@Bean
//	public PasswordEncoder passwordEncoder() { // パスワードエンコーダー(パスワードのハッシュ化)を提供するメソッド
//		return new MessageDigestPasswordEncoder("SHA-256"); // SHA-256を使用するPasswordEncoderを返す
//	}
//
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.formLogin(login -> login
//                .loginPage("/") // ログインページのパス
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/") // ログイン成功後の遷移先を設定
//                .failureUrl("/?error=true")   // ログイン失敗後の遷移先を設定
//                .permitAll()
//                
//		).logout(
//				 logout -> logout
//				 
//				 .logoutUrl("/logout")
//				 .logoutSuccessUrl("/login")
//				 
//		).authorizeHttpRequests(auth -> auth
//				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//				.requestMatchers("/login").permitAll() // /loginへのアクセスは全て許可
//				.requestMatchers("/attendance/regist").permitAll()
//				.requestMatchers("/user/regist").permitAll()
//				.requestMatchers("/user/regist").hasAnyRole("Admin")
//				.requestMatchers("/attendance/regist").hasAnyRole("UnitManager", "Manager", "Regular")
//				
////				.requestMatchers("/user/regist").hasAnyRole("ADMIN")
////				.requestMatchers("/attendance/regist").hasAnyRole("Regular")
//                
//                
////                .requestMatchers("/manager/employeeList").hasAnyRole("ADMIN")
////                .requestMatchers("/manager/employeeList-edit").hasAnyRole("ADMIN")
////                .requestMatchers("/manager/team-create").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/manager/team-detail").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/manager/team-edit").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/manager/teamlist").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/manager/teams/**").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/manager/assignment/**").hasAnyRole("ADMIN", "USER")
////                .requestMatchers("/member/**").hasRole("USER")
////                .requestMatchers("/member/**").hasRole("USER")
////                .requestMatchers("/manager/home/**").hasRole("USER")
////                .requestMatchers("/manager/report-search").hasRole("USER")
//                .anyRequest().authenticated() // その他のリクエストは認証が必要
//                
//        ).exceptionHandling(ex -> ex.accessDeniedPage("/"));
//		
//		return http.build();
//	}

//}

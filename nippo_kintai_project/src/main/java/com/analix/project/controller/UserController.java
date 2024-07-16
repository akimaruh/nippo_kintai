package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	
	@RequestMapping(path = "/regist")
	public String showUserRegist() {
		
		return "user/regist";
	}
	
	@RequestMapping(path = "/regist/search")
	public String searchUserByUserID() {
		return "user/regist";
	}
	
	@RequestMapping(path = "/regist/complete")
	public String copleteUserRegist() {
		return "user/regist";
	}
	

}

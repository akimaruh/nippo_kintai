package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analix.project.entity.Users;
import com.analix.project.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	@RequestMapping(path = "/regist")
	public String showUserRegist() {
		
		return "user/regist";
	}
	
	@RequestMapping(path = "/regist/search")
	public String searchUserByUserID(String name,Model model) {
		Users userData = userService.getUserDataByUserName(name);
		
		model.addAttribute("userData",userData);
		
		return "user/regist";
	}
	
	@RequestMapping(path = "/regist/complete")
	public String copleteUserRegist() {
		return "user/regist";
	}
	

}

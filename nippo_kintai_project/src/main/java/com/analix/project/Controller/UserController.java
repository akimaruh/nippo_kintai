package com.analix.project.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
	
	
	
	@RequestMapping(path = "/user/regist")
	public String showUserRegist() {
		
		return "user/regist";
	}
	
	

}

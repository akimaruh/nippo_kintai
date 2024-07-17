package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
//	 @InitBinder
//	    public void initBinder(WebDataBinder binder) {
//	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	        dateFormat.setLenient(false);
//	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
//	    }

	@RequestMapping(path = "/regist")
	public String showUserRegist(Model model) {
		model.addAttribute("userData", new Users());

		return "user/regist";
	}

	@RequestMapping(path = "/regist/search")
	public String searchUserByUserID(String name, Model model) {
		Users userData = userService.getUserDataByUserName(name);

		model.addAttribute("userData", userData);

		return "user/regist";
	}

	@RequestMapping(path = "/regist/complete")
	public String completeUserRegist(@ModelAttribute("userData") Users userData,Integer id, Model model) {
		System.out.println("コントローラクラス" + id);
		userService.registUserData(userData,id);

		return "redirect:/user/regist";
	}

}

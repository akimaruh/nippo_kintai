package com.analix.project.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
import com.analix.project.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@RequestMapping(path = "/regist")
	public String showUserRegist(Model model) {
		Users userData = userService.getUserDataByUserName(null);
		model.addAttribute("userData", userData);
		//		model.addAttribute("userData", new Users());

		return "user/regist";
	}

	@RequestMapping(path = "/regist/search")
	public String searchUserByUserName(String name, Model model) {

		Users userData = userService.getUserDataByUserName(name);

		model.addAttribute("userData", userData);

		return "user/regist";
	}

	@RequestMapping(path = "/regist/complete")
	public String completeUserRegist(@ModelAttribute RegistUserForm registUserForm, Integer id,
			String name, Model model) {

		// 日付形式を定義
		Date startDate = registUserForm.getStartDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String stringTypeStartDate = dateFormat.format(startDate);
		System.out.println("stringTypeStartDate" + stringTypeStartDate);
		//		if ("9999/99/99".equals(stringTypeStartDate)) {
		//			System.out.println("削除します");
		//			userService.deleteUser(id);
		//			model.addAttribute("message", "データが削除されました");
		//			return "redirect:/user/regist";

		try {
			 dateFormat.setLenient(false);
	            dateFormat.parse(stringTypeStartDate);
	            userService.registUserData(registUserForm, id,name);

		} catch (ParseException e) {
			if ("9999/99/99".equals(stringTypeStartDate)) {
				System.out.println("削除します");
				userService.deleteUser(id);
				model.addAttribute("message", "データが削除されました");
				return "redirect:/user/regist";
			} else {
				// 他の無効な日付形式の場合
				model.addAttribute("message", "無効な日付です");
				//			result.rejectValue("startDate", "error.userData", "無効な日付形式です。");
				return "redirect:/user/regist";
			}
		}

		return "redirect:/user/regist";

	}

}

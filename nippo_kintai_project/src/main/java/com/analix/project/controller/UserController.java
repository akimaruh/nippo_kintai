package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	/**
	 * 初期表示
	 * @param model
	 * @return ユーザー管理画面
	 * 
	 */
	@RequestMapping(path = "/regist")
	public String showUserRegist(Model model) {

		model.addAttribute("userData", new Users());
		//		model.addAttribute("userData", new Users());

		return "user/regist";
	}

	/**
	 * 『検索』ボタン押下後
	 * @param name
	 * @param model
	 * @return ユーザー管理画面
	 */
	@RequestMapping(path = "/regist/search")
	public String searchUserByUserName(RegistUserForm registUserForm, Model model) {
		String inputName = registUserForm.getName();
		RegistUserForm userData = userService.getUserDataByUserName(inputName);
		String searchedName = userData.getName();

		if (searchedName == null) {
			String error = "存在しないユーザーです";
			model.addAttribute("error", error);
			userData.setName(inputName);
		}
		model.addAttribute("userData", userData);
		return "user/regist";
	}

	/**
	 * 『登録』ボタン押下後
	 * @param registUserForm
	 * @param id
	 * @param name
	 * @param model
	 * @param result
	 * @return 
	 */
	@RequestMapping(path = "/regist/complete")
	public String completeUserRegist(@Validated @ModelAttribute RegistUserForm registUserForm, Integer id,
			String name, Model model, BindingResult result, RedirectAttributes redirectAttributes) {

		userService.validationForm(registUserForm, result);

		if (result.hasErrors()) {

			model.addAttribute("userData", registUserForm);
			model.addAttribute("error","利用開始日が不正です");
			return "user/regist";
		}
		System.out.println(registUserForm);
		String message = userService.registUserData(registUserForm, id, name);
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/user/regist";

	}

}

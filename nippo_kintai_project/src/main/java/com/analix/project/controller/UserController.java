package com.analix.project.controller;

import java.util.Map;

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
		Map<String, Integer> departmentMap = userService.pulldownDepartment();

		model.addAttribute("registUserForm", new Users());
		model.addAttribute("departmentList", departmentMap);

		return "user/regist";
	}

	/**
	 * 『検索』ボタン押下後
	 * @param name
	 * @param model
	 * @return ユーザー管理画面
	 */
	@RequestMapping(path = "/regist/search")
	public String searchUserByUserName(@Validated @ModelAttribute RegistUserForm registUserForm, Model model,
			BindingResult result) {
		String inputName = registUserForm.getName();
		RegistUserForm userData = userService.getUserDataByUserName(inputName, result);
		String searchedName = userData.getName();

		Map<String, Integer> departmentMap = userService.pulldownDepartment();
		model.addAttribute("departmentList", departmentMap);

		if (result.hasErrors()) {

			model.addAttribute("registUserForm", registUserForm);
			model.addAttribute("error", "エラー内容に従って修正してください");
			return "user/regist";
		}
		if (searchedName == null) {
			String error = "存在しないユーザーです";
			model.addAttribute("error", error);
			userData.setName(inputName);
		}

		model.addAttribute("registUserForm", userData);

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

		boolean errorFlg = userService.validationForm(registUserForm, result);

		if (result.hasErrors()) {
			Map<String, Integer> departmentMap = userService.pulldownDepartment();
			model.addAttribute("registUserForm", registUserForm);
			model.addAttribute("departmentList", departmentMap);
			model.addAttribute("errorFlg", errorFlg);
			model.addAttribute("error", "エラー内容に従って修正してください");
			return "user/regist";
		}
		
		String message = userService.registUserData(registUserForm, id);
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/user/regist";

	}

}

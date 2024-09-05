package com.analix.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.Department;
import com.analix.project.service.DepartmentService;

@Controller
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;
	
	/**
	 * 初期表示
	 * @param model
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist")
	public String departmentRegist(Model model) {
		
		// 「登録済の部署」リストプルダウン
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);

		return "/department/regist";
	}
	
	/**
	 * 「登録」ボタン押下
	 */
	@RequestMapping(path = "/department/regist/complete", method = RequestMethod.POST)
	public String departmentComplete(@RequestParam("newName") String newName, Department department, Model model, RedirectAttributes redirectAttributes) {

		department.setName(newName);
		String message = departmentService.registDepartment(newName);
		redirectAttributes.addFlashAttribute("error", message);
//		model.addAttribute("message", message);
		
		return "redirect:/department/regist";
	}
}

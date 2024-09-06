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
	 * @param newName 新部署名
	 * @param department
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(path = "/department/regist/complete", method = RequestMethod.POST)
	public String departmentComplete(@RequestParam("newName") String newName, Department department, Model model,
			RedirectAttributes redirectAttributes) {

		boolean isRegistComplete = departmentService.registDepartment(newName);

		if (isRegistComplete) {
			redirectAttributes.addFlashAttribute("message", "登録しました");
		} else {
			redirectAttributes.addFlashAttribute("error", "この部署名は既に登録済です");
			redirectAttributes.addFlashAttribute("newName", newName);
		}
		
		//この部署名は無効化されています

		return "redirect:/department/regist";
	}

	// 「変更」ボタン押下
	@RequestMapping(path = "/department/regist/update", method = RequestMethod.POST)
	public String updateDepartment(@RequestParam("newName") String newName,
			@RequestParam("exsistsName") String exsistsName, RedirectAttributes redirectAttributes) {
		
		if (exsistsName.equals(newName)) {
			redirectAttributes.addFlashAttribute("error", "部署名同じ");
			return "redirect:/department/regist";
		}

		boolean isUpdate = departmentService.updateDepartment(newName, exsistsName);

		if (isUpdate) {
			redirectAttributes.addFlashAttribute("message", "変更しました");
		} else {
			redirectAttributes.addFlashAttribute("error", "変更できませんでした");
		}
		
		//この部署名は無効化されています
		
		return "redirect:/department/regist";
	}
	
	//「削除」ボタン押下(論理削除)
	@RequestMapping(path = "/department/regist/delete", method = RequestMethod.POST)
	public String deleteDepartment(@RequestParam("exsistsName") String exsistsName,
			RedirectAttributes redirectAttributes) {

		boolean isDelete = departmentService.deleteDepartment(exsistsName);

		if (isDelete) {
			redirectAttributes.addFlashAttribute("message", "削除しました");
		} else {
			redirectAttributes.addFlashAttribute("error", "削除できませんでした");
		}
		return "redirect:/department/regist";

	}

}
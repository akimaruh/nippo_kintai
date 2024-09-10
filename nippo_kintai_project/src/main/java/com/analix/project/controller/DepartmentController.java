package com.analix.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
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

		// 「無効な部署」リストプルダウン
		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		// フォームの初期化
		model.addAttribute("department", new Department());

		return "/department/regist";
	}

	/**
	 * 「登録」ボタン押下
	 * @param newName 新部署名
	 * @param department
	 * @param model
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/complete", method = RequestMethod.POST)
	public String departmentComplete(@ModelAttribute("department") @Validated Department department,
			@RequestParam("newName") String newName, Model model,
			RedirectAttributes redirectAttributes, BindingResult result) {

		departmentService.validationForm(department, true, true, false, false, result);

		// プルダウンメニューのデータを再取得
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);

		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			model.addAttribute("newName", newName);
			return "/department/regist";
		}

		boolean isRegistComplete = departmentService.registDepartment(newName);

		if (isRegistComplete) {
			redirectAttributes.addFlashAttribute("message", newName + "を登録しました。");
		} else {
			redirectAttributes.addFlashAttribute("error", "この部署名は既に登録済です。");
			redirectAttributes.addFlashAttribute("newName", newName);
		}

		//この部署名は無効化されています

		return "redirect:/department/regist";
	}

	/**
	 * 「変更」ボタン押下
	 * @param newName 新部署名
	 * @param exsistsName 登録済の部署名
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/update", method = RequestMethod.POST)
	public String updateDepartment(@ModelAttribute("department") @Validated Department department,
			@RequestParam("newName") String newName,
			@RequestParam("exsistsName") String exsistsName, Model model, RedirectAttributes redirectAttributes,
			BindingResult result) {

		departmentService.validationForm(department, true, true, true, false, result);

		// プルダウンメニューのデータを再取得
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);

		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			model.addAttribute("newName", newName);
			model.addAttribute("exsistsName", exsistsName);
			return "/department/regist";
		}

		boolean isUpdate = departmentService.updateDepartment(newName, exsistsName);

		if (isUpdate) {
			redirectAttributes.addFlashAttribute("message", exsistsName + "から" + newName + "に変更しました。");
		} else {
			redirectAttributes.addFlashAttribute("error", "変更に失敗しました。");
		}

		//この部署名は無効化されています

		return "redirect:/department/regist";
	}

	/**
	 * 「削除」ボタン押下(論理削除)
	 * @param exsistsName 登録済の部署名
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/delete", method = RequestMethod.POST)
	public String deleteDepartment(@ModelAttribute("department") @Validated Department department,
			@RequestParam("exsistsName") String exsistsName,
			Model model, RedirectAttributes redirectAttributes, BindingResult result) {

		departmentService.validationForm(department, false, false, true, false, result);

		// プルダウンメニューのデータを再取得
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);

		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			return "/department/regist";
		}

		boolean isDelete = departmentService.deleteDepartment(exsistsName);

		if (isDelete) {
			redirectAttributes.addFlashAttribute("message", exsistsName + "を削除しました。");
		} else {
			redirectAttributes.addFlashAttribute("error", "削除に失敗しました。");
		}

		return "redirect:/department/regist";
	}

	/**
	 * 「有効化」ボタン押下
	 * @param inactiveName 無効な部署名
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/active", method = RequestMethod.POST)
	public String updateDepartmentToActive(@ModelAttribute("department") @Validated Department department,
			@RequestParam("inactiveName") String inactiveName, Model model, RedirectAttributes redirectAttributes,
			BindingResult result) {

		departmentService.validationForm(department, false, false, false, true, result);

		// プルダウンメニューのデータを再取得
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);

		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			return "/department/regist";
		}

		boolean isUpdate = departmentService.updateDepartmentToActive(inactiveName);

		if (isUpdate) {
			redirectAttributes.addFlashAttribute("message", inactiveName + "を有効化しました。");
		} else {
			redirectAttributes.addFlashAttribute("error", "有効化に失敗しました。");
		}

		return "redirect:/department/regist";

	}

}
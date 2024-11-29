package com.analix.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.DepartmentUserDto;
import com.analix.project.entity.Department;
import com.analix.project.entity.UserDepartmentOrder;
import com.analix.project.form.DepartmentForm;
import com.analix.project.form.ModifyDepartmentGroup;
import com.analix.project.form.RegistDepartmentGroup;
import com.analix.project.service.DepartmentService;
import com.analix.project.util.SessionHelper;

@Controller
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private SessionHelper sessionHelper;

	/**
	 * 共通メソッド(部署一覧カード表示：有効部署リスト、部署人数)
	 * @param model
	 */
	public void loadDepartmentDate(Model model) {
		// 有効部署リスト
		//		List<Department> departmentList = departmentService.showDepartment();
		//		model.addAttribute("departmentList", departmentList);

		// セッションヘルパークラスからuserIdを取得
		Integer userId = sessionHelper.getUser().getId();

		// 有効部署リスト(ユーザーごとの順序付き)
		List<Department> departmentList = departmentService.showDepartmentWithOrder(userId);
		model.addAttribute("departmentList", departmentList);

		// 部署ごとのユーザー人数を取得 Map<部署Id, 人数>
		Map<Integer, Integer> userCountMap = new HashMap<>();
		for (Department department : departmentList) {
			Integer userCount = departmentService.getUserCountByDepartmentId(department.getDepartmentId());
			userCountMap.put(department.getDepartmentId(), userCount);
		}
		model.addAttribute("userCountMap", userCountMap);
	}

	/**
	 * 初期表示
	 * @param model
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist")
	public String departmentRegist(Model model) {

		model.addAttribute("action", "regist");

		// 共通メソッド(部署一覧カード表示)
		loadDepartmentDate(model);

		// フォームの初期化
		model.addAttribute("departmentForm", new DepartmentForm());

		//		//「廃止済み部署の復元」は初期表示では非表示 (nullを渡す)
		//        model.addAttribute("inactiveDepartmentList", null);
		//        //「所属ユーザー」は初期表示では非表示 (nullを渡す)
		//        model.addAttribute("departmentUserList", null);

		return "/department/regist";
	}

	/**
	 * 「登録」ボタン押下
	 * @param departmentForm
	 * @param result
	 * @param model
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/complete", method = RequestMethod.POST)
	public String departmentComplete(
			@ModelAttribute("departmentForm") @Validated(RegistDepartmentGroup.class) DepartmentForm departmentForm,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		
		model.addAttribute("action", "complete");

		// 共通メソッド(部署一覧カード表示)
		loadDepartmentDate(model);
				
		String name = departmentForm.getName();

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			model.addAttribute("name", name);
			return "/department/regist";
		}

		boolean isRegistComplete = departmentService.registDepartment(name);

		if (isRegistComplete) {
			redirectAttributes.addFlashAttribute("message", name + "を登録しました。");
		} else {
			Byte status = departmentService.getDepartmentStatus(name);
			if (status == 0) {
				redirectAttributes.addFlashAttribute("error", "この部署は廃止されています。廃止済部署の復元から復元可能です。");
				redirectAttributes.addFlashAttribute("name", name);
			} else {
				redirectAttributes.addFlashAttribute("error", "この部署名は既に登録済です。");
				redirectAttributes.addFlashAttribute("name", name);
			}
		}
		return "redirect:/department/regist";
	}

	/**
	 * ユーザーアイコン押下、「所属ユーザー一覧」の表示
	 * @param departmentId
	 * @param name
	 * @param model
	 * @return
	 */
	@PostMapping("/department/regist/users")
	public String showDepartmentUsers(@RequestParam("departmentId") Integer departmentId,
			@RequestParam("name") String name, Model model) {

		model.addAttribute("action", "users");
		model.addAttribute("departmentForm", new DepartmentForm());

		// 所属ユーザー情報リストを取得
		List<DepartmentUserDto> departmentUserList = departmentService.getUsersByDepartmentId(departmentId);
		model.addAttribute("departmentUserList", departmentUserList);

		// 部署名の表示
		String departmentName = "【" + name + "】";
		model.addAttribute("departmentName", departmentName);

		return "/department/regist";
	}

	/**
	 * 「復元」ボタン押下、「廃止済み部署の復元」の表示
	 * @param model
	 * @return
	 */
	@PostMapping("/department/regist/restore")
	public String showInactive(Model model) {

		model.addAttribute("action", "restore");
		model.addAttribute("departmentForm", new DepartmentForm());

		// 廃止済み部署のデータを取得
		List<Department> inactiveDepartmentList = departmentService.showInactiveDepartment();
		model.addAttribute("inactiveDepartmentList", inactiveDepartmentList);

		return "/department/regist";
	}

	/**
	 * 「変更」ボタン押下
	 * @param departmentForm
	 * @param result
	 * @param model
	 * @param redirectAttributes
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/update", method = RequestMethod.POST)
	public String updateDepartment(
			@ModelAttribute("departmentForm") @Validated(ModifyDepartmentGroup.class) DepartmentForm departmentForm,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		// 共通メソッド(部署一覧カード表示)
		loadDepartmentDate(model);

		String newName = departmentForm.getNewName();
		String exsistsName = departmentForm.getExsistsName();

		// 入力チェック
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("errorMessage", "エラー内容に従って修正してください。"); // alert alert-dangerに表示される文字
			redirectAttributes.addFlashAttribute("exsistsName", exsistsName);
			redirectAttributes.addFlashAttribute("openModal", true);
			redirectAttributes.addFlashAttribute("inputNewName", newName); //入力したnewName
			if (result.hasFieldErrors("newName")) {
				String newNameErrorMessage = result.getFieldError("newName").getDefaultMessage();
				redirectAttributes.addFlashAttribute("newNameErrorMessage", newNameErrorMessage);
			}
			return "redirect:/department/regist";
		}

		boolean isUpdate = departmentService.updateDepartment(newName, exsistsName);

		if (isUpdate) {
			redirectAttributes.addFlashAttribute("message", exsistsName + "から" + newName + "に変更しました。");
		} else {
			Byte status = departmentService.getDepartmentStatus(newName);
			if (status == 0) {
				redirectAttributes.addFlashAttribute("error", "この部署は廃止されています。廃止済部署の復元から復元可能です。");
			} else if (status == 1) {
				redirectAttributes.addFlashAttribute("error", "変更に失敗しました。");
			}
		}
		return "redirect:/department/regist";
	}

	/**
	 * 「削除」ボタン押下(論理削除)
	 * @param departmentForm
	 * @param exsistsName
	 * @param departmentId
	 * @param model
	 * @param redirectAttributes
	 * @param result
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/delete", method = RequestMethod.POST)
	public String deleteDepartment(@ModelAttribute("departmentForm") @Validated DepartmentForm departmentForm,
			@RequestParam("exsistsName") String exsistsName,
			@RequestParam("departmentId") Integer departmentId,
			Model model, RedirectAttributes redirectAttributes, BindingResult result) {

		// 共通メソッド(部署一覧カード表示)
		loadDepartmentDate(model);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			return "/department/regist";
		}

		Integer usersCount = departmentService.getUserCountByDepartmentId(departmentForm.getDepartmentId());

		if (usersCount > 0) {
			redirectAttributes.addFlashAttribute("error", "この部署にはユーザーがいるため廃止できません。");
		} else {
			boolean isDelete = departmentService.deleteDepartment(exsistsName);

			if (isDelete) {
				redirectAttributes.addFlashAttribute("message", exsistsName + "を廃止しました。");
			} else {
				redirectAttributes.addFlashAttribute("error", "廃止に失敗しました。");
			}
		}
		return "redirect:/department/regist";
	}

	/**
	 * 「有効化」ボタン押下
	 * @param departmentForm
	 * @param inactiveName 無効な部署名
	 * @param model
	 * @param redirectAttributes
	 * @param result
	 * @return 部署登録画面
	 */
	@RequestMapping(path = "/department/regist/active", method = RequestMethod.POST)
	public String updateDepartmentToActive(@ModelAttribute("departmentForm") @Validated DepartmentForm departmentForm,
			@RequestParam("inactiveName") String inactiveName, Model model, RedirectAttributes redirectAttributes,
			BindingResult result) {

		// 共通メソッド(部署一覧カード表示)
		loadDepartmentDate(model);

		if (result.hasErrors()) {
			model.addAttribute("error", "エラー内容に従って修正してください。");
			return "/department/regist";
		}

		boolean isUpdate = departmentService.updateDepartmentToActive(inactiveName);

		if (isUpdate) {
			redirectAttributes.addFlashAttribute("message", inactiveName + "を復元しました。");
		} else {
			redirectAttributes.addFlashAttribute("error", "復元に失敗しました。");
		}
		return "redirect:/department/regist";

	}

	/**
	 * 部署一覧テーブルの順序を保存
	 * @param orderData
	 */
	@PostMapping("/department/saveOrder")
	@ResponseBody
	public void saveDepartmentOrder(@RequestBody List<UserDepartmentOrder> orderData) {
		departmentService.saveDepartmentOrder(orderData);
	}
	
	// ユーザー一覧からユーザー管理画面に遷移
//	@PostMapping("/user/regist")
//	public String showUser(@RequestParam("employeeCode") Integer employeeCode, Model model) {
//		model.addAttribute("employeeCode", employeeCode);
//		
//		
//	}

}
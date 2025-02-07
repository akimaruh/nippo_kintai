package com.analix.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.UserCsvInputDto;
import com.analix.project.dto.UserSearchDto;
import com.analix.project.entity.Department;
import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
import com.analix.project.form.RegistUserGroup;
import com.analix.project.form.RegistUserListForm;
import com.analix.project.form.SearchUserGroup;
import com.analix.project.service.DepartmentService;
import com.analix.project.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor //lombok使ってコンストラクタ作成省略
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final SmartValidator validator;
	private final DepartmentService departmentService;

	/**
	 * 初期表示
	 * @param model
	 * @return ユーザー管理画面
	 * 
	 */
	@RequestMapping(path = "/regist")
	public String showUserRegist(Model model, HttpSession session) {
		Map<String, Integer> departmentMap = userService.pulldownDepartment();
		session.setAttribute("departmentMap", departmentMap);

		model.addAttribute("registUserForm", new RegistUserForm());
		model.addAttribute("departmentList", departmentMap);

		return "user/regist";
	}

	/**
	 * 共通メソッド（検索ボタン押下後）部署プルダウン、ユーザー情報をmodelに追加
	 * @param userData
	 * @param model
	 */
	public void addUserAndDepartmentToModel(RegistUserForm userData, Model model) {
		Map<String, Integer> departmentMap = userService.pulldownDepartment();
		model.addAttribute("departmentList", departmentMap);

		model.addAttribute("registUserForm", userData);
	}

	/**
	 * 『検索』ボタン押下後
	 * @param name
	 * @param model
	 * @return ユーザー管理画面
	 */
	@RequestMapping(path = "/regist/search")
	public String searchUserByUserName(
			@Validated(SearchUserGroup.class) @ModelAttribute("registUserForm") RegistUserForm registUserForm,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("registUserForm", registUserForm);
			model.addAttribute("error", "エラー内容に従って修正してください");
			return "user/regist";
		}

		RegistUserForm userData = userService.getUserDataByEmployeeCode(registUserForm.getSearchEmployeeCode());

		if (userData.getId() == null) {
			String error = "存在しないユーザーです";
			userData.setEmployeeCode(registUserForm.getSearchEmployeeCode());
			model.addAttribute("error", error);
		}

		// 共通メソッド（部署プルダウン、ユーザー情報をmodelに追加）
		addUserAndDepartmentToModel(userData, model);

		return "user/regist";
	}

	/**
	 * 別画面から遷移(『検索』ボタン押下してある状態）
	 * @param employeeCode
	 * @param model
	 * @return
	 */
	@PostMapping("/regist/employeeSearch")
	public String searchUserByEmployeeCode(@RequestParam("employeeCode") String employeeCode, Model model) {

		RegistUserForm userData = userService.getUserDataByEmployeeCode(employeeCode);

		// 共通メソッド（部署プルダウン、ユーザー情報をmodelに追加）
		addUserAndDepartmentToModel(userData, model);

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
	public String completeUserRegist(@Validated(RegistUserGroup.class) @ModelAttribute RegistUserForm registUserForm,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		String registMessage = null;
		if (result.hasErrors()) {
			Map<String, Integer> departmentMap = userService.pulldownDepartment();
			System.out.println(registUserForm);
			model.addAttribute("registUserForm", registUserForm);
			model.addAttribute("departmentList", departmentMap);
			model.addAttribute("error", "エラー内容に従って修正してください");
			return "user/regist";
		}
		registMessage = userService.registUserData(registUserForm);
		if (registMessage.contains("登録しました") || registMessage.contains("更新しました")) {
			redirectAttributes.addFlashAttribute("message", registMessage);
		} else {
			redirectAttributes.addFlashAttribute("error", registMessage);
		}

		return "redirect:/user/regist";

	}

	/**
	 * インポートユーザープレビュー
	 * @param importFile
	 * @param redirectAttributes
	 * @param model
	 * @param registUserListForm
	 * @param result
	 * @return
	 */
	@PostMapping(path = "/regist/import")
	public String previewImportUsers(@RequestParam("importFile") MultipartFile importFile,
			RedirectAttributes redirectAttributes, Model model, @ModelAttribute RegistUserListForm registUserListForm) {
		Map<String, List<String>> errorMessageMap = new LinkedHashMap<>();

		try {
			if (!importFile.getContentType().toUpperCase().contains("CSV")) {
				throw new IllegalStateException("拡張子はcsvのみが対応です。");
			}

			List<UserCsvInputDto> userCsvInputDtoList = userService.showImportList(importFile);

			int lineNumber = 2;
			for (UserCsvInputDto user : userCsvInputDtoList) {
				// 各ユーザーごとに個別の BindingResult を作成
				DataBinder binder = new DataBinder(user);
				BindingResult result = binder.getBindingResult();
				validator.validate(user, result);

				//各ユーザーごとにエラーメッセージ(複数の場合あり)を格納するリストを作成
				List<String> errorMessageList = new ArrayList<>();
				for (FieldError fieldError : result.getFieldErrors()) {
					errorMessageList.add(fieldError.getDefaultMessage());
				}
				// エラーがある場合はメッセージを記録
				if (result.hasErrors()) {
					errorMessageMap.put("[" + lineNumber + "行目]", errorMessageList);
				}
				lineNumber++;
			}
			if (!errorMessageMap.isEmpty()) {
				redirectAttributes.addFlashAttribute("errorMessageMap", errorMessageMap);
				return "redirect:/user/allCreate";
			}
			//入力チェックエラーがなければモーダルウィンドウを開く
			redirectAttributes.addFlashAttribute("usersList", userCsvInputDtoList);
			redirectAttributes.addFlashAttribute("openModal", true);
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/user/allCreate";
	}

	/**
	 * 『インポート』・『全件インポート』押下後
	 * @param usersList
	 * @return HTTPレスポンス(badRequest/ok)
	 */
	@PostMapping(path = "/regist/import/complete")
	public ResponseEntity<Map<String, String>> insertImportUsers(
			@RequestBody List<UserCsvInputDto> userCsvInputDtoList) {
		boolean isRegistComplete = false;
		Map<String, String> messageRequest = new LinkedHashMap<>();
		try {
			isRegistComplete = userService.handleCsvImport(userCsvInputDtoList);
		} catch (IllegalStateException e) {
			messageRequest.put("status", "error");
			messageRequest.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(messageRequest);
		}
		if (isRegistComplete == false) {
			System.out.println("登録失敗");

			messageRequest.put("status", "error");
			messageRequest.put("message", "ユーザーの一括登録に失敗しました。");
			return ResponseEntity.badRequest().body(messageRequest);
		} else {
			System.out.println("登録成功");

			messageRequest.put("status", "succsess");
			messageRequest.put("message", "ユーザーの一括登録が成功しました。");
			return ResponseEntity.ok().body(messageRequest);
		}

	}

//ユーザー一覧画面
	//ユーザー一覧画面初期表示
	@GetMapping("/list")
	public String showUserList(Model model, HttpSession session) {
		List<Users> userList = userService.getUserList();
		model.addAttribute("userList", userList);
		
		Map<String, Integer> departmentMap = userService.pulldownDepartment();
		model.addAttribute("departmentList", departmentMap);
		session.setAttribute("departmentMap", departmentMap);
		
		model.addAttribute("userSearchDto", new UserSearchDto());
		return "user/list";
	}

	//検索ボタン押下
	@GetMapping("/list/search")
	public String searchUsers(UserSearchDto userSearchDto, Model model, HttpSession session) {
		//すべてnullまたは空なら全リスト表示
		if ((userSearchDto.getKeyword() == null || userSearchDto.getKeyword().isEmpty()) &&
				(userSearchDto.getEmployeeCode() == null || userSearchDto.getEmployeeCode().isEmpty()) &&
				(userSearchDto.getUserName() == null || userSearchDto.getUserName().isEmpty()) &&
				(userSearchDto.getDepartment() == null || userSearchDto.getDepartment().isEmpty()) &&
				(userSearchDto.getRole() == null || userSearchDto.getRole().isEmpty())) {
			return "redirect:/user/list";
		}

		//入力内容に基づいたリスト表示
		List<Users> userList = userService.getSearchUserList(userSearchDto);
		model.addAttribute("userList", userList);
		
		//部署プルダウン
		Map<String, Integer> departmentMap = userService.pulldownDepartment();
		model.addAttribute("departmentList", departmentMap);
		
		model.addAttribute("userSearchDto", userSearchDto);
		
		//検索条件表示
		String searchConditions = userService.generateSearchConditions(userSearchDto);
		model.addAttribute("searchConditions", searchConditions);
		
		return "user/list";
	}

	
//ユーザー一括登録画面
	//一括登録画面初期表示
	@GetMapping("/allCreate")
	public String showAllCreate() {
		return "user/allCreate";
	}
	
	//テンプレートダウンロードについて押下
	@GetMapping("/allCreateDownload")
	public String showAllCreateDownload(Model model) {
		//部署リスト
		List<Department> departmentList = departmentService.showDepartment();
		model.addAttribute("departmentList", departmentList);
		return "user/allCreateDownload";
	}
	
	//テンプレートダウンロード
	@PostMapping("/downloadTemplate")
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		 userService.ExcelOutput(response);
	}
}

package com.analix.project.service;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.analix.project.entity.Department;
import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
import com.analix.project.mapper.DepartmentMapper;
import com.analix.project.mapper.UserMapper;

@Service
public class UserService {

	private final UserMapper userMapper;
	private final DepartmentMapper departmentMapper;

	public UserService(UserMapper userMapper, DepartmentMapper departmentMapper) {
		this.userMapper = userMapper;
		this.departmentMapper = departmentMapper;

	}

	/**
	 * ユーザー名で検索
	 * @param name
	 * @return registUserForm
	 */
	public RegistUserForm getUserDataByUserName(String name, BindingResult result) {

		if (name != null) {
			String fullwidthRegex = "^[\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFFa-zA-Z]+$";
			Pattern fullwidthPattern = Pattern.compile(fullwidthRegex);
			Matcher nameMatcher = fullwidthPattern.matcher(name);

			if (name.length() > 20) {
				result.addError(
						new FieldError("registUserForm", "name",
								"20文字以内で入力してください"));
			}
			//空欄または全角以外で入力があった場合
			if (!nameMatcher.find()) {
				result.addError(
						new FieldError("registUserForm", "name",
								"全角で入力してください"));
			}
		}

		//DBでユーザー検索
		Users userDataBySearch = userMapper.findUserDataByUserName(name);
		RegistUserForm registUserForm = new RegistUserForm();
		//エンティティからフォームへ詰めなおし
		if (userDataBySearch != null) {
			registUserForm.setId(userDataBySearch.getId());
			registUserForm.setName(userDataBySearch.getName());
			registUserForm.setPassword(userDataBySearch.getPassword());
			registUserForm.setRole(userDataBySearch.getRole());
			registUserForm.setDepartmentId(userDataBySearch.getDepartmentId());
			registUserForm.setDepartmentName(userDataBySearch.getDepartmentName());

			//LocalDate型(yyyy-MM-dd)からString型(yyyy/MM/dd)へ変換
			LocalDate startDate = userDataBySearch.getStartDate();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			String startDateString = startDate.format(dateTimeFormatter);

			//DB保存の"9999-12-31"を外部表示用"9999/99/99"に戻す
			if (startDateString.equals("9999/12/31")) {
				startDateString = "9999/99/99";
			}

			registUserForm.setStartDate(startDateString);
		} else {
			//ユーザーが存在しない場合新しいユーザーIDを払い出し
			Integer NextUserId = userMapper.createNewId();
			registUserForm.setId(NextUserId + 1);
		}

		return registUserForm;
	}

	/**
	 * 入力チェック
	 * @param registUserForm
	 * @param result
	 */
	public boolean validationForm(RegistUserForm registUserForm, BindingResult result) {
		String startDate = registUserForm.getStartDate();
		String name = registUserForm.getName();
		String password = registUserForm.getPassword();
		String role = registUserForm.getRole();
		Integer departmentId = registUserForm.getDepartmentId();

		String fullwidthRegex = "^[^ -~｡-ﾟ]+$";
		Pattern fullwidthPattern = Pattern.compile(fullwidthRegex);
		Matcher nameMatcher = fullwidthPattern.matcher(name);
		//		if (name == "") {
		//			result.addError(
		//					new FieldError("registUserForm", "name",
		//							"名前を入力してください"));
		//		}
		if (name.length() > 20) {
			result.addError(
					new FieldError("registUserForm", "name",
							"全角20文字以内で入力してください"));
		}
		//空欄または全角以外の入力があった場合
		if (!nameMatcher.find()) {
			result.addError(
					new FieldError("registUserForm", "name",
							"全角で入力してください"));
		}
		String alnumRegex = "^[\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFFa-zA-Z]+$";
		Pattern alnumPattern = Pattern.compile(alnumRegex);
		Matcher passwordMatcher = alnumPattern.matcher(password);
		//		if (password == "") {
		//			result.addError(
		//					new FieldError("registUserForm", "password",
		//							"パスワードを入力してください"));
		//		}
		if (password.length() > 16) {
			result.addError(
					new FieldError("registUserForm", "password",
							"半角16文字以内で入力してください"));
		}
		//空欄または半角以外の入力があった場合
		if (!passwordMatcher.find()) {
			result.addError(
					new FieldError("registUserForm", "password",
							"半角で入力してください"));
		}

		if (role == "") {
			result.addError(
					new FieldError("registUserForm", "role",
							"権限を選択してください"));
		}
		if (departmentId == 0 || departmentId == null) {
			result.addError(
					new FieldError("registUserForm", "departmentId",
							"所属部署を選択してください"));
		}

		if (startDate.equals("9999/99/99")) {
			return false;
		}

		else {
			DateFormat format = DateFormat.getDateInstance();
			// 日付/時刻解析を厳密に行う
			format.setLenient(false);
			try {
				format.parse(startDate);
				return false;

			} catch (Exception e) {
				result.addError(
						new FieldError("registUserForm", "startDate",
								"yyyy/MM/dd形式で入力して下さい"));
				return true;
			}
		}
	}

	/**
	 * ユーザー更新
	 * @param users
	 * @return 反映結果
	 */
	public String registUserData(RegistUserForm registUserForm, Integer id) {
		System.out.println(registUserForm.getName());
		Users registUser = new Users();
		String startDate = registUserForm.getStartDate();
		String userName = registUserForm.getName();

		//"利用開始日に9999/99/99が入力されている場合
		if (startDate.equals("9999/99/99")) {
			//DBで保存できる最大日付へ変更
			startDate = "9999/12/31";
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate startDateLoalDate = LocalDate.parse(startDate, formatter);

		registUser.setPassword(registUserForm.getPassword());
		registUser.setRole(registUserForm.getRole());
		registUser.setName(userName);
		registUser.setStartDate(startDateLoalDate);
		registUser.setDepartmentId(registUserForm.getDepartmentId());
		System.out.println(registUser.getName());
		Integer userCheck = userMapper.countUserDataById(id,userName);
		System.out.println("ユーザー合致数"+userCheck);
		//ユーザー更新処理
		if (userCheck == 1) {
			registUser.setId(id);
			boolean updateCheck = userMapper.updateUserData(registUser);
			if (updateCheck == true) {
				return userName + "を更新しました。";
			} else {
				return userName+"の登録が失敗しました。";
			}
		}

		//ユーザー登録処理
		else if (userCheck == 0) {
			boolean updateCheck = userMapper.insertUserData(registUser);
			if (updateCheck == true) {
				return userName + "を登録しました。";
			} else {
				return userName+"の登録が失敗しました。";
			}

		} else {
			//登録ユーザー重複時
			return userName+"は既に登録されています。";
		}

	}

	/**
	 * 部署入力プルダウン用リスト
	 * @return 部署リスト
	 */
	public Map<String, Integer> pulldownDepartment() {
		List<Department> departmentList = departmentMapper.findAllDepartmentName();
		Map<String, Integer> departmentMap = new LinkedHashMap<>();

		for (Department row : departmentList) {
			String departmentName = row.getName();
			Integer departmentId = row.getDepartmentId();
			departmentMap.put(departmentName, departmentId);
		}

		return departmentMap;

	}

}

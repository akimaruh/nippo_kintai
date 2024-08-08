package com.analix.project.service;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
import com.analix.project.mapper.UserMapper;

@Service
public class UserService {

	private final UserMapper userMapper;

	public UserService(UserMapper userMapper) {
		this.userMapper = userMapper;

	}

	/**
	 * ユーザー名で検索
	 * @param name
	 * @return registUserForm
	 */
	public RegistUserForm getUserDataByUserName(String name) {
		//DBでユーザー検索
		Users userDataBySearch = userMapper.findUserDataByUserName(name);

		RegistUserForm registUserForm = new RegistUserForm();
		//エンティティからフォームへ詰めなおし
		if (userDataBySearch != null) {
			registUserForm.setId(userDataBySearch.getId());
			registUserForm.setName(userDataBySearch.getName());
			registUserForm.setPassword(userDataBySearch.getPassword());
			registUserForm.setRole(userDataBySearch.getRole());

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

		if (startDate.equals("9999/99/99")) {
			return true;
		}

		else {
			DateFormat format = DateFormat.getDateInstance();
			// 日付/時刻解析を厳密に行う
			format.setLenient(false);
			try {
				format.parse(startDate);
				return true;

			} catch (Exception e) {
				result.addError(
						new FieldError("registUserForm", "startDate",
								"yyyy/MM/dd形式で入力して下さい"));
				return false;
			}
		}
	}

	/**
	 * ユーザー更新
	 * @param users
	 * @return 反映結果
	 */
	public String registUserData(RegistUserForm registUserForm, Integer id, String name) {

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

		Integer userCheck = userMapper.countUserDataById(userName);

		//ユーザー更新処理
		if (userCheck == 1) {
			registUser.setId(id);
			boolean updateCheck = userMapper.updateUserData(registUser);
			if (updateCheck == true) {
				return userName + "を更新しました。";
			} else {
				return "登録が失敗しました。";
			}
		}

		//ユーザー登録処理
		else if (userCheck == 0) {
			boolean updateCheck = userMapper.insertUserData(registUser);
			if (updateCheck == true) {
				return userName + "を登録しました。";
			} else {
				return "登録が失敗しました。";
			}

		} else {
			//登録ユーザー重複時
			return "登録が失敗しました。";
		}

	}

}

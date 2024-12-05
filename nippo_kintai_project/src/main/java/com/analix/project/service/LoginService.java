package com.analix.project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.LoginMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.PasswordUtil;
import com.analix.project.util.SessionHelper;

@Service
public class LoginService {

	@Autowired
	private LoginMapper loginMapper;
	@Autowired
	private PasswordUtil passwordUtil;
	@Autowired
	private SessionHelper sessionHelper;

	/**
	 * ログイン処理手続き
	 * @param employeeCode
	 * @param password
	 * @return 不備有："error" 不備無:"(空白)"
	 */
	public String handleLogin(String employeeCode, String password) {
		System.out.println("lockLogin():" + lockLogin());
		if (lockLogin()) {

			return "error";
		}
		System.out.println("hasValidateError:" + hasValidateError(employeeCode, password));
		if (hasValidateError(employeeCode, password)) {
			hasExceededLoginFailureLimit();
			return "error";
		}
		System.out.println("notFindLoginUser:" + notFindLoginUser(employeeCode, password));
		if (notFindLoginUser(employeeCode, password)) {
			hasExceededLoginFailureLimit();
			return "error";

		}

		return "";
	}

	/**
	 * 入力チェック
	 * @param employeeCode
	 * @param password
	 * @return true:不備有 false:不備無
	 */
	public boolean hasValidateError(String employeeCode, String password) {
		// ユーザーIDチェック
		String employeeCodeRegex = "^[0-9]{1,9}$";
		Pattern employeeCodePattern = Pattern.compile(employeeCodeRegex);
		Matcher employeeCodeMatcher = employeeCodePattern.matcher(employeeCode);
		if (!employeeCodeMatcher.matches()) {
			return true;
		}

		// パスワードチェック
		String passRegex = "^[0-9a-zA-Z]{1,16}$";
		Pattern passPattern = Pattern.compile(passRegex);
		Matcher passMatcher = passPattern.matcher(password);
		if (!passMatcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * ログイン可能ユーザー検索・セッション格納
	 * @param employeeCode
	 * @param password
	 * @return true:該当無 false:該当有
	 */
	public boolean notFindLoginUser(String employeeCode, String password) {
		Users loginUser = findByIdAndPassword(employeeCode, password);
		if (loginUser != null) {
			if (!isDate(loginUser.getStartDate())) {
				return true;
			}
			//取得したユーザーをセッションに入れる
			sessionHelper.setUser(loginUser);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 社員番号・パスワードでユーザーデータ検索
	 * @param employeeCode
	 * @param password
	 * @return 該当のユーザーデータ
	 */
	public Users findByIdAndPassword(String employeeCode, String password) {

		Integer employeeCodeInteger = Integer.parseInt(employeeCode);
		String hashPassword = passwordUtil.getSaltedAndStrechedPassword(password, employeeCode);
		Users loginUser = loginMapper.findByCodeAndPassword(employeeCodeInteger, hashPassword);

		return loginUser;

	}

	/**
	 * 利用開始日チェック
	 * @param startDate
	 * @return true:利用可 false:利用不可
	 */
	public boolean isDate(LocalDate startDate) {
		if (startDate == null) {
			return false;
		}
		System.out.println("startDate.isAfter(LocalDate.now()):"+startDate.isAfter(LocalDate.now()));
		return startDate.isBefore(LocalDate.now());
	}

	/**
	 * ログイン試行回数追加+超過チェック
	 * @return true:超過 false:制限内
	 */
	public void hasExceededLoginFailureLimit() {
		LocalDateTime now = LocalDateTime.now();

		Users loginUser = sessionHelper.getUser();
		Integer failCount;
		if (loginUser == null) {
			loginUser = new Users();
			failCount = 1;
		} else {
			if (loginUser.getFailCount() == null) {
				failCount = 1;
			} else {
				failCount = loginUser.getFailCount() + 1;
			}
		}
		//		loginUser.setFailCount(
		//				(loginUser != null && loginUser.getFailCount() != null) ? loginUser.getFailCount() + 1 : 1);
		loginUser.setFailCount(failCount);
		loginUser.setLastFailDate(now);
		if (loginUser.getFailCount() >= Constants.LOGIN_FAILURE_LIMIT) {
			loginUser.setLoginLockDate(now);
		}
		System.out.println("ログイン試行回数追加:"+loginUser);
		sessionHelper.setUser(loginUser);
	}

	/**
	 * ログインロック可否チェック
	 * @return true:ロックする false:ロックしない
	 */
	public boolean lockLogin() {
		LocalDateTime now = LocalDateTime.now();
		Users loginUser = sessionHelper.getUser();
		System.out.println("ログインロック可否:"+loginUser);
		if (loginUser != null && loginUser.getLoginLockDate() != null) {
			//アカウントロックは規定時間後に解除される
			if (loginUser.getLoginLockDate().plusMinutes(Constants.LOGIN_LOCK_TIMEOUT_MINUTES).isAfter(now)) {
				return true;
			}
		}
		return false;
	}

}

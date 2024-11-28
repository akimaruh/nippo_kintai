package com.analix.project.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
	
	/** パスワードポリシー */
	public final static String PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z\\-]{10,16}$";
	
	private static int STRETCH_COUNT = 10;
	
	/**
	 * パスワード自動生成
	 * @return ランダムな8文字の文字列
	 */
	public String getRandomPassword() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		int length = 8;
		Random random = new Random();
		StringBuilder randomString = new StringBuilder(length);
		
		for(int i = 0; i<length; i++) {
			int index = random.nextInt(characters.length());
			randomString.append(characters.charAt(index));
		}
		return randomString.toString();
	}
	
	
	/**
	 * salt +ハッシュ化+ストレッチングしたパスワ-ドを 取得
	 * 
	 * @param password
	 * @param userId
	 * @return getStretchedPassword()
	 */
	public String getSaltedAndStrechedPassword(String password, String employeeCode) {
		return getStretchedPassword(getSaltedPassword(password, employeeCode), employeeCode);
	}

	/**
	 * salt＋ハッシュ化したパスワードを取得
	 * 
	 * @param password
	 * @param userId
	 * @return getSha256()
	 * 
	 */
	private String getSaltedPassword(String password, String employeeCode) {
		String salt = getSha256(employeeCode);
		return getSha256(salt + password);
	}

	/**
	 * salt + ストレッチングしたパスワードを取得(推奨)
	 * 
	 * @param password
	 * @param userId
	 * @return hash
	 */
	private String getStretchedPassword(String password, String employeeCode) {
		String salt = getSha256(employeeCode);
		String hash = "";

		for (int i = 0; i < STRETCH_COUNT; i++) {
			hash = getSha256(hash + salt + password);
		}

		return hash;
	}

	/**
	 * 文字列から SHA256 のハッシュ値を取得
	 * 
	 * @param target
	 * @return SHA256 のハッシュ値
	 */
	private String getSha256(String target) {
		MessageDigest md = null;
		StringBuffer buf = new StringBuffer();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(target.getBytes());
			byte[] digest = md.digest();

			for (int i = 0; i < digest.length; i++) {
				buf.append(String.format("%02x", digest[i]));
			}

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		return buf.toString();
	}
	
	

	/**
	 * パスワードポリシーに沿っているパスワードであるかチェックする。
	 * 
	 * @param password
	 * @return 判定結果：パスワードポリシーに沿っている場合true、パスワードポリシーに沿っていない場合false
	 */
	public boolean isValidPassword(String password) {
		if (password == null || password.equals("")) {
			return false;
		} else if (!password.matches(PASSWORD_POLICY)) {
			return false;
		}
		return true;
	}

}
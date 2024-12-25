package com.analix.project.util;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Component;

@Component
public class CreateUrlUtil {
	/**
	 * 通知リンク押下後遷移先URL生成(日報勤怠忘れ・システム障害・承認申請)
	 * @param type
	 * @return
	 */
	public static final String createUrl(String type) {
		String pass = null;
		LocalDate today = LocalDate.now();
		int thisYear = today.getYear();
		int thisMonth = today.getMonthValue();

		switch (type) {
		case "日報未提出":
			pass = "/dailyReport/change?date=" + today;
			break;
		case "勤怠未提出":
			pass = "/attendance/regist/display?year=" + thisYear + "&month=" + thisMonth;
			break;
		case "日報勤怠未提出":
			pass = "";
			break;
		case "月次申請提出":
			pass = "/attendance/approve";
			break;
		case "訂正申請提出":
			pass = "/attendance/approve";
			break;
		case "システム障害":
			pass = "";
			break;
		default:
			throw new IllegalArgumentException("未知の値です: " + type);

		}

		return pass;
	}

	/**
	 * 通知リンク押下後遷移先URL生成(承認・却下)	
	 * @param type
	 * @param yearMonth
	 * @return
	 */
	public static final String createUrl(String type, YearMonth yearMonth) {
		String pass = null;
		int targetYear = yearMonth.getYear();
		int targetMonth = yearMonth.getMonthValue();

		switch (type) {
		case "承認":
			pass = "/attendance/regist/display?year=" + targetYear + "&month=" + targetMonth;
			break;
		case "却下":
			pass = "/attendance/regist/display?year=" + targetYear + "&month=" + targetMonth;
			break;
		default:
			throw new IllegalArgumentException("未知の値です: " + type);

		}
		return pass;
	}

}

package com.analix.project.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class CustomDateUtil {

	//java.sql.Date→LocalDateへ変換
	public LocalDate convertToLocalDate(java.sql.Date sqlDate) {
		return sqlDate.toLocalDate();
	}

	//LocalDate→java.sql.Dateへ変換
	public java.sql.Date convertToSqlDate(LocalDate localDate) {
		return java.sql.Date.valueOf(localDate);
	}

	//LocalDate→String型のyyyy/MM/ddへ変換
	public String dateHyphenSlashConverter(LocalDate localDate) {
		return localDate.toString().replace("-", "/");
	}

	//YearMonth→String型のyyyy/MMへ変換
	public String yearMonthHyphenSlashConverter(YearMonth yearMonth) {
		return yearMonth.toString().replace("-", "/");
	}
	//String型 yyyy/M/d→LoacalDate型 yyyy-MM-ddへ変換
	public LocalDate formatDate(String inputDate) {
		// 入力形式に対応するフォーマッタ
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
		// 日付を解析してフォーマット
		LocalDate date = LocalDate.parse(inputDate, inputFormatter);
		return date;
	}
	
	/**
	 * String型→LocalTime型(HH:mm:ss)へ変換
	 * @param inputTime
	 * @return
	 */
	public LocalTime formatTime(String inputTime) {
		// 入力形式に対応するフォーマッタ
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H/m/s");
		// 時刻を解析してLocalTime型にフォーマット
		LocalTime time = LocalTime.parse(inputTime, inputFormatter);
		return time;
	}
	
	//LocalTime型→String型(HH:mm)へ変換
	public String convertLocalTimeToString(LocalTime time) {
		if (time == null) {
			return "";
		}
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		String timeStr = time.format(timeFormatter);
		return timeStr;
	}
	
	//日付から1文字の曜日を取得
	public static String getDayOfWeekInJapanese(LocalDate date) {
		return date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPAN);
		
	}

}

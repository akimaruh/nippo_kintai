package com.analix.project.util;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import one.cafebabe.businesscalendar4j.Holiday;
import one.cafebabe.businesscalendar4j.ビジネスカレンダー;

public class JapaneseHoliday {

	/**
	 * 指定された年月の祝日を取得
	 * @param targetYearMonth
	 * @return 祝日の日付（"yyyy/MM/dd"形式）のリスト
	 */
	public static List<String> getHoliday(YearMonth targetYearMonth) {
		ビジネスカレンダー japanCal = ビジネスカレンダー.newBuilder()
				.祝休日(ビジネスカレンダー.日本.PUBLIC_HOLIDAYS)
				.build();

		// 指定年月の月初から月末までの範囲の祝日をyyyy/MM/ddに変換してリストに格納
		List<String> list = new ArrayList<>();
		List<Holiday> holidayList = japanCal.get指定期間内の祝休日(targetYearMonth.atDay(1), targetYearMonth.atEndOfMonth());
		for (Holiday holiday : holidayList) {
			//2025年11月25日が祝日判定されるため手動除外
			if (!holiday.date.toString().equals("2025-11-25")) {
				list.add(holiday.date.toString().replace("-", "/"));
			}
		}
		return list;
	}
}

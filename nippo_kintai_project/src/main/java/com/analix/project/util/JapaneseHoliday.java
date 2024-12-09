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
		System.out.println("1" + targetYearMonth.atDay(1));
		System.out.println(targetYearMonth.atEndOfMonth());
		ビジネスカレンダー japanCal = ビジネスカレンダー.newBuilder()
				.祝休日(ビジネスカレンダー.日本.PUBLIC_HOLIDAYS)
				.build();

//		System.out.println(japanCal.get内閣府公表祝休日初日());
		
		// 指定年月の月初から月末までの範囲の祝日をyyyy/MM/ddに変換してリストに格納
		List<String> list = new ArrayList<>();
		List<Holiday> holidayList = japanCal.get指定期間内の祝休日(targetYearMonth.atDay(1), targetYearMonth.atEndOfMonth());
		for (Holiday holiday : holidayList) {
			list.add(holiday.date.toString().replace("-", "/"));
		}
		System.out.println(list);
		return list;
	}
}

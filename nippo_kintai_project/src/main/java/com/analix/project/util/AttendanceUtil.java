package com.analix.project.util;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AttendanceUtil {
	
		/*
		 * 曜日の日本語変換用util
		 */
	    private static final Map<DayOfWeek, String> japaneseDaysOfWeek = new HashMap<>();

	    static {
	        japaneseDaysOfWeek.put(DayOfWeek.MONDAY, "月");
	        japaneseDaysOfWeek.put(DayOfWeek.TUESDAY, "火");
	        japaneseDaysOfWeek.put(DayOfWeek.WEDNESDAY, "水");
	        japaneseDaysOfWeek.put(DayOfWeek.THURSDAY, "木");
	        japaneseDaysOfWeek.put(DayOfWeek.FRIDAY, "金");
	        japaneseDaysOfWeek.put(DayOfWeek.SATURDAY, "土");
	        japaneseDaysOfWeek.put(DayOfWeek.SUNDAY, "日");
	    }

	    public static String getJapaneseDayOfWeek(DayOfWeek dayOfWeek) {
	        return japaneseDaysOfWeek.get(dayOfWeek);
	    }
	


}

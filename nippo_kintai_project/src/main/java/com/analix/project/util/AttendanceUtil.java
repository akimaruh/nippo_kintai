package com.analix.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AttendanceUtil {
	
		/*
		 * 曜日の日本語変換用util
		 */
//	    private static final Map<DayOfWeek, String> japaneseDaysOfWeek = new HashMap<>();
//
//	    static {
//	        japaneseDaysOfWeek.put(DayOfWeek.MONDAY, "月");
//	        japaneseDaysOfWeek.put(DayOfWeek.TUESDAY, "火");
//	        japaneseDaysOfWeek.put(DayOfWeek.WEDNESDAY, "水");
//	        japaneseDaysOfWeek.put(DayOfWeek.THURSDAY, "木");
//	        japaneseDaysOfWeek.put(DayOfWeek.FRIDAY, "金");
//	        japaneseDaysOfWeek.put(DayOfWeek.SATURDAY, "土");
//	        japaneseDaysOfWeek.put(DayOfWeek.SUNDAY, "日");
//	    }
//
//	    public static String getJapaneseDayOfWeek(DayOfWeek dayOfWeek) {
//	        return japaneseDaysOfWeek.get(dayOfWeek);
//	    }
	
	    /**
	     * 勤怠状況変換用util
	     */
	    private static final Map<Byte,String> attendanceStatus = new HashMap<>();
	    static {
	    	attendanceStatus.put((byte) 0,"通常出勤");
	    	attendanceStatus.put((byte) 1,"休日");
	    	attendanceStatus.put((byte) 2,"祝日");
	    	attendanceStatus.put((byte) 3,"遅刻");
	    	attendanceStatus.put((byte) 4,"有給");
	    	attendanceStatus.put((byte) 5,"欠勤");
	    	attendanceStatus.put((byte) 6,"早退");
	    	attendanceStatus.put((byte) 7,"時間外勤務");
	    	attendanceStatus.put((byte) 8,"振替出勤");
	    	attendanceStatus.put((byte) 9,"振替休日");
	    	attendanceStatus.put((byte) 10,"代替出勤");
	    	attendanceStatus.put((byte) 11,"代替休日");
	    	
	    }
	    
	    public static String getAttendanceStatus(Byte status) {
	    	return attendanceStatus.get(status);
	    }
	    
	    //出勤系統
	    private static List<Byte> attendanceSystem = new ArrayList<Byte>();
	    static {
	    attendanceSystem.add((byte) 0);//通常出勤
	    attendanceSystem.add((byte) 3);//遅刻
	    attendanceSystem.add((byte) 6);//早退
	    attendanceSystem.add((byte) 7);//時間外勤務
	    attendanceSystem.add((byte) 8);//振替出勤
	    attendanceSystem.add((byte) 10);//代替出勤
	    
	    }
	    public static List<Byte> getAttendanceSystem(){
	    	return attendanceSystem;
	    }
	    
	    //休日系統
	    private static List<Byte> holidaySystem = new ArrayList<Byte>();
	    static {
	    	holidaySystem.add((byte) 1);//休日
	    	holidaySystem.add((byte) 2);//祝日
	    	holidaySystem.add((byte) 4);//有給
	    	holidaySystem.add((byte) 5);//欠勤
	    	holidaySystem.add((byte) 9);//振替休日
	    	holidaySystem.add((byte) 11);//代替休日
	    
	    }
	    public static List<Byte> getHolidaySystem(){
	    	return holidaySystem;
	    }
	    

}

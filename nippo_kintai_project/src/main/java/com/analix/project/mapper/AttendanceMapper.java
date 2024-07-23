package com.analix.project.mapper;

import java.time.Month;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Attendance;

@Mapper
public interface AttendanceMapper {
	
	/*
	 * 勤怠一覧取得マッパー
	 */
	public List<Attendance> findAllDailyAttendance(@Param("userId") Integer userId, @Param("year") int year,@Param("month") Month month);
	
	/*
	 * 勤怠登録
	 */
	public Boolean registDailyAttendance(@Param("attendance") Attendance attendance);
	
	/*
	 * 勤怠修正
	 */
	public Boolean updateDailyAttendance(@Param("attendance") Attendance attendance);
	
	
	
	

}

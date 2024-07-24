package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Attendance;

@Mapper
public interface AttendanceMapper {
	
	/*
	 * 勤怠一覧取得マッパー
	 */
	public List<Attendance> findAllDailyAttendance(@Param("userId") Integer userId, @Param("yearMonth") String yearMonth);
	
	/*
	 * 勤怠登録
	 */
	public Boolean registDailyAttendance(@Param("attendance") Attendance attendance);
	
	/*
	 * 勤怠修正
	 */
	public Boolean updateDailyAttendance(@Param("attendance") Attendance attendance);
	
	/*
	 * 承認申請情報取得
	 */

	
	
	

}

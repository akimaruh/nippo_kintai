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
	 * 承認申請者情報取得
	 */
	public List<Attendance> findByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("targetYearMonth") String targetYearMonth);
	
	/*
	 * １か月の登録件数カウント
	 */
	public Integer monthRegistrationCount(@Param("userId") Integer userId, @Param("yearMonth") String yearMonth);
//	/*
//	 * 「承認申請」ボタン押下後
//	 */
//	public Date findDateByUserId
	

}

package com.analix.project.mapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Attendance;
import com.analix.project.entity.Users;

@Mapper
public interface AttendanceMapper {

	/**
	 * 勤怠一覧取得マッパー
	 * @param userId
	 * @param yearMonth
	 * @return 勤怠一覧リスト
	 */
	public List<Attendance> findAllDailyAttendance(@Param("userId") Integer userId,
			@Param("yearMonth")YearMonth targetYearMonth);

	/**
	 * 勤怠登録
	 * @param attendance
	 * @return 反映結果
	 */
	public Boolean registDailyAttendance(@Param("attendance") Attendance attendance);

	/**
	 * 勤怠修正
	 * @param attendance
	 * @return 反映結果
	 */
	public Boolean updateDailyAttendance(@Param("attendance") Attendance attendance);

	/**
	 * 承認申請者情報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return 承認申請者の勤怠リスト
	 */
	public List<Attendance> findByUserIdAndYearMonth(@Param("userId") Integer userId,
			@Param("targetYearMonth") String targetYearMonth);
	
	/**
	 * 勤怠未提出者情報取得
	 * @param today
	 * @return 勤怠未提出者のリスト
	 */
	public List<Users> attendanceUnsubmittedPersonList(@Param("today") LocalDate today);

}

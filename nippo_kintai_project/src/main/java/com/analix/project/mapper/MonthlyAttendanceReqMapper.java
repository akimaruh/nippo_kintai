package com.analix.project.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.MonthlyAttendanceReqDto;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	/*
	 * 全件取得
	 */
	public List<MonthlyAttendanceReqDto> findAllMonthlyAttendanceReq();
	
	/**
	 * 対象者を絞って全件取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public List<MonthlyAttendanceReqDto> findAllMonthlyAttendanceReqByUserId(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonth);
	
	/*
	 * ログインユーザーのstatus取得
	 */
	public Integer findStatusByUserId(@Param("userId") Integer userId);
	
	/*
	 * 「承認申請」ボタン押下後
	 */
	public void insertMonthlyAttendanceReq(@Param("monthlyDto") MonthlyAttendanceReqDto monthlyDto);
	
	/*
	 * 「承認申請」ボタン押下後 - status検索
	 */
	public Integer findStatusByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("attendanceDate") LocalDate attendanceDate);

	/*
	 * status更新 承認申請
	 */
	public void updateStatusWaiting(@Param("userId") Integer userId, @Param("attendanceDate") LocalDate attendanceDate);
	
	/*
	 * status更新 承認
	 */
	public void updateStatusApprove(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay);
	
	/*
	 * status更新 却下
	 */
	public void updateStatusReject(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay);
	
	/*
	 * ユーザー名取得
	 */
	public MonthlyAttendanceReqDto findMonthlyAttendanceReqByUserId(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay);

}

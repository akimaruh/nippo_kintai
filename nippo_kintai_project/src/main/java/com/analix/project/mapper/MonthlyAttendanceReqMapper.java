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
	
	/**
	 * 月次idからデータ（ユーザーId、申請者、対象日付、申請日）取得
	 * @param id 月次id
	 * @return 
	 */
	public MonthlyAttendanceReqDto findMonthlyDataById(@Param("id") Integer id);
	
	/*
	 * 月次申請承認後（status更新）
	 */
	public boolean updateApproveStatus(@Param("userId") Integer userId, @Param("targetYearMonthAtDay") LocalDate targetYearMonthAtDay);
	
	/*
	 * 月次申請却下後（status更新・却下理由追加）
	 */
	public boolean updateRejectStatusAndComment(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay, @Param("comment") String comment);
	

	
	/*
	 * ユーザー名取得
	 */
	public MonthlyAttendanceReqDto findMonthlyAttendanceReqByUserId(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay);

}

package com.analix.project.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.MonthlyAttendanceReqDto;

@Mapper
public interface MonthlyAttendanceReqMapper {

	/**
	 * 月次申請情報をすべて取得
	 * @return 月次申請+ユーザー名リスト
	 */
	public List<MonthlyAttendanceReqDto> findAllMonthlyAttendanceReq();

	/**
	 * 対象の月次申請を取得
	 * @param userId
	 * @param targetYearMonth
	 * @return 月次申請+ユーザー名
	 */
	public MonthlyAttendanceReqDto findMonthlyAttendanceReqByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonth);
	
	/*
	 * ログインユーザーのstatus取得
	 */
//	public Integer findStatusByUserId(@Param("userId") Integer userId);


	/**
	 * 対象年月のstatus取得
	 * @param userId
	 * @param attendanceDate
	 * @return
	 */
	public Integer findStatusByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("attendanceDate") LocalDate attendanceDate);

	/**
	 * データが存在するかどうか確認
	 * @param userId
	 * @param attendanceDate
	 * @return true存在する false存在しない
	 */
	public boolean exsistsMonthlyAttendanceReq(@Param("userId") Integer userId, @Param("attendanceDate") LocalDate attendanceDate);
	
	/**
	 * 月次情報を登録
	 * @param monthlyDto
	 * @return true成功 false失敗
	 */
	public boolean insertMonthlyAttendanceReq(MonthlyAttendanceReqDto monthlyDto);

	/**
	 * statusを1(申請中)に更新
	 * @param userId
	 * @param attendanceDate
	 * @return true成功 false失敗
	 */
	public boolean updateStatusToPendingByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("attendanceDate") LocalDate attendanceDate, @Param("date") LocalDate date);
	
	/**
	 * 月次idからデータ（ユーザーId、申請者、対象日付、申請日）取得
	 * @param id 月次id
	 * @return 
	 */
	public MonthlyAttendanceReqDto findMonthlyDataById(Integer id);
	
	/**
	 * 月次申請承認後（status更新）
	 * @param userId
	 * @param targetYearMonthAtDay
	 * @return true成功 false失敗
	 */
	public boolean updateApproveStatus(@Param("userId") Integer userId, @Param("targetYearMonthAtDay") LocalDate targetYearMonthAtDay);

	/**
	 * 月次申請却下後（status更新・却下理由追加）
	 * @param userId
	 * @param targetYearMonthAtDay
	 * @param comment
	 * @return true成功 false失敗
	 */
	public boolean updateRejectStatusAndComment(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay, @Param("comment") String comment);
	

	
	/*
	 * ユーザー名取得
	 */
//	public MonthlyAttendanceReqDto findMonthlyAttendanceReqByUserId(@Param("userId") Integer userId, @Param("targetYearMonth") LocalDate targetYearMonthAtDay);

}

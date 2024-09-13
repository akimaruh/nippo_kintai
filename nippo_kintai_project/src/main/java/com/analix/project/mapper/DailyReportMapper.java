package com.analix.project.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.DailyReportDto;
import com.analix.project.entity.DailyReport;
import com.analix.project.entity.DailyReportDetail;
import com.analix.project.entity.Users;

@Mapper
public interface DailyReportMapper {
	
	/**
	 * 日報ステータス取得
	 * @param userId
	 * @param targetDate
	 * @return ステータス
	 */
	public Integer findStatusByUserIdAndTargetDate(@Param("userId") Integer userId,
			@Param("targetDate") LocalDate targetDate);
	
	/**
	 * 登録済みの日報データ確認
	 * @param userId
	 * @param targetDate
	 * @return
	 */
	public int countRegistedDailyReportByTargetDate (@Param("userId") Integer userId,@Param("targetDate") LocalDate targetDate);
	/**
	 * 日報テーブル登録
	 * @param dailyReport
	 * @return 反映結果
	 */
	public boolean registDailyReport(@Param("dailyReport") DailyReport dailyReport);
	
	/**
	 * 日報詳細登録
	 * @param dailyReportDetail
	 * @return 反映結果
	 */
	public boolean registDailyReportDetail(@Param("dailyReportDetail")DailyReportDetail dailyReportDetail);
	
	/**
	 * 日報詳細更新
	 * @param dailyReportDetail
	 * @return 反映結果
	 */
	public boolean updateDailyReportDetail(@Param("dailyReportDetail")DailyReportDetail dailyReportDetail);
	
	/**
	 * 日報ステータス更新
	 * @param dailyReport
	 * @return 反映結果
	 */
	public boolean updateDailyReportStatus(@Param("dailyReport")DailyReport dailyReport);
	
	/**
	 * 日報取得
	 * @param userId
	 * @param targetDate
	 * @return 日報+日報詳細
	 */
	public DailyReportDto findAllDailyReportByUserIdAndTargetDate(@Param("userId")Integer userId,@Param("targetDate")LocalDate targetDate);
	
	/**
	 * 日報詳細行毎に削除
	 * @param id
	 * @return 反映結果
	 */
	public boolean deleteDailyReportDetail(@Param("id")Integer id);
	
	/**
	 * 日報未提出者一覧取得
	 * @param today
	 * @return 日報未提出ユーザーIDのリスト
	 */
	public List<Users> dailyReportUnsubmittedPersonList(@Param("today")LocalDate today);
		
	
}

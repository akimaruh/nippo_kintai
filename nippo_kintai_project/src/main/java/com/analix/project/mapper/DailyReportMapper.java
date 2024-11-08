package com.analix.project.mapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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

	//	/**
	//	 * 登録済みの日報データ確認
	//	 * @param userId
	//	 * @param targetDate
	//	 * @return
	//	 */
	//	public int countRegistedDailyReportByTargetDate (@Param("userId") Integer userId,@Param("targetDate") LocalDate targetDate);

	/**
	 * 登録済み日報データ存在チェック
	 * @param userId
	 * @param targetDate
	 * @return 存在有無
	 */
	public boolean registedDailyReportByTargetDateExistCheck(@Param("userId") Integer userId,
			@Param("targetDate") LocalDate targetDate);

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
	public boolean registDailyReportDetail(@Param("dailyReportDetail") DailyReportDetail dailyReportDetail);
	
	/**
	 * 日報詳細一括登録
	 * @param insertList
	 * @return
	 */
	public boolean batchInsertDailyReportDetails(@Param("insertList") List<DailyReportDetail> insertList);

	/**
	 * 日報詳細更新
	 * @param dailyReportDetail
	 * @return 反映結果
	 */
	public boolean updateDailyReportDetail(@Param("dailyReportDetail") DailyReportDetail dailyReportDetail);
	
	/**
	 * 日報詳細一括更新
	 * @param updateList
	 * @return
	 */
	public boolean batchUpdateDailyReportDetails(@Param("updateList") List<DailyReportDetail> updateList);

	/**
	 * 日報ステータス更新
	 * @param dailyReport
	 * @return 反映結果
	 */
	public boolean updateDailyReportStatus(@Param("dailyReport") DailyReport dailyReport);

	/**
	 * 日報取得
	 * @param userId
	 * @param targetDate
	 * @return 日報+日報詳細
	 */
	public DailyReportDto findAllDailyReportByUserIdAndTargetDate(@Param("userId") Integer userId,
			@Param("targetDate") LocalDate targetDate);

	/**
	 * 日報日付で全件検索
	 * @param targetDate
	 * @return
	 */
	public List<DailyReportDto> getAllDatilReportListByTargetDate(@Param("targetDate") LocalDate targetDate);

	/**
	 * 日報詳細行毎に削除
	 * @param id
	 * @return 反映結果
	 */
	public boolean deleteDailyReportDetail(@Param("id") Integer id);
	
	/**
	 * 日報詳細まとめて削除
	 * @param deleteList
	 * @return
	 */
	public boolean deleteDailyReportDetails(@Param("deleteList") List<Integer> deleteList);

	/**
	 * 日報未提出者一覧取得
	 * @param today
	 * @return 日報未提出ユーザーIDのリスト
	 */
	public List<Users> dailyReportUnsubmittedPersonList(@Param("today") LocalDate today);
	
	/**
	 * 帳票出力用 ユーザーの１か月分の日報リスト
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public List<DailyReportDto> dailyReportListForAMonth(@Param("userId")Integer userId,@Param("targetYearMonth") YearMonth targetYearMonth);
	
	/**
	 * 1日の総作業時間を求めたリスト
	 * @param userId
	 * @param targetYearMonth
	 * @return 日ごとの総作業時間
	 */
	public List<Map<String,Object>>getTimePerDate(@Param("userId")Integer userId,@Param("targetYearMonth")YearMonth targetYearMonth);
	
	/**
	 * 1カ月の総作業時間
	 * @param userId
	 * @param targetYearMonth
	 * @return 総作業時間
	 */
	public Integer getTimePerMonth(@Param("userId") Integer userId,@Param("targetYearMonth") YearMonth targetYearMonth);
	
	/**
	 * 作業毎の１か月の作業時間
	 * @param userId
	 * @param targetYearMonth
	 * @return 作業毎の１か月の作業時間
	 */
	public List<Map<String,Object>>getWorkTimeByProcess(@Param("userId")Integer userId,@Param("targetYearMonth")YearMonth targetYearMonth);

}

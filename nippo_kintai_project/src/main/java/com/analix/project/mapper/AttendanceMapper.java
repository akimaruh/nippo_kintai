package com.analix.project.mapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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
			@Param("yearMonth") YearMonth targetYearMonth);

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

	/**
	 * 出勤打刻済レコード数確認
	 * @param userId
	 * @param today
	 * @return 該当レコード数
	 */
	public boolean todaysStartTimeExistCheck(@Param("userId") Integer userId, @Param("today") LocalDate today);

	/**
	 * 勤怠出勤打刻新規登録or更新
	 * @param attendance
	 */
	public boolean upsertStartTime(@Param("attendance") Attendance attendance);

	/**
	 * 勤怠退勤打刻更新
	 * @param attendance
	 * @return 
	 */
	public boolean updateEndTime(@Param("attendance") Attendance attendance);

	/**
	 * 訂正申請承認後(訂正テーブルをもとに勤怠情報を更新)
	 * @param id 訂正ID
	 */
	public int updateAttendanceFromCorrection(@Param("id") Integer id);

	/**
	 * 勤怠状況別日数・時間取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public List<Map<String, Object>> daysTimeRetrieveByStatus(@Param("userId") Integer userId,
			@Param("targetYearMonth") YearMonth targetYearMonth);

	/**
	 * 各週の残業時間取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public List<Map<String, Object>> everyWeekOvertimeHoursRetrieve(@Param("userId") Integer userId,
			@Param("targetYearMonth") YearMonth targetYearMonth);

/**
	 * 勤怠情報と訂正情報を取得（年月or日付）
	 * @param userId
	 * @param targetYearMonth 対象年月
	 * @param targetDate 対象日付
	 * @return AttendanceInfoDtoリスト
	 */
	public List<AttendanceInfoDto> findAttendanceWithCorrectons(@Param("userId") Integer userId, 
		@Param("targetYearMonth") YearMonth targetYearMonth, @Param("targetDate") LocalDate targetDate);
}

package com.analix.project.mapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.AttendanceCorrectionDto;
import com.analix.project.entity.AttendanceCorrection;
import com.analix.project.form.AttendanceCorrectionForm;

@Mapper
public interface AttendanceCorrectionMapper {
	
	/**
	 * 勤怠訂正登録
	 * @param attendanceCorrection
	 */
	public void registAttendanceCorrection(@Param("correction") AttendanceCorrection correction);
	
	/**
	 * 全件取得 correctionテーブル+ usersテーブルのname
	 */
	public List<AttendanceCorrection> findAllAttendanceCorrection();
	
	/**
	 * 訂正idからデータ（ユーザーId、申請者、対象日付、申請日）取得
	 * @param id 訂正id
	 * @return 
	 */
	public AttendanceCorrectionDto findCorrectionDataById(@Param("id") Integer id);
	
	/**
	 * 対象者の訂正申請情報
	 * @param userId
	 * @param date
	 * @return
	 */
	public AttendanceCorrection findAttendanceByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

	/**
	 * 対象者の申請中の訂正申請リスト
	 * @param userId
	 * @param targetYearMonth
	 * @return 申請中リスト
	 */
	public List<AttendanceCorrection> findReqestedCorrectionByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("yearMonth") YearMonth targetYearMonth);

	/**
	 * 対象者の却下された訂正申請リスト
	 * @param userId
	 * @param targetYearMonthAtDay
	 * @return 却下リスト
	 */
	public List<AttendanceCorrection> findRejectedAttendanceByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("yearMonth") YearMonth targetYearMonth);
	
	/**
	 * 訂正申請却下後(却下理由、確認者、却下フラグ1に更新)
	 * @param correctionForm
	 * @return ture:却下成功 false:却下失敗
	 */
	public boolean updateRejectCorrection(@Param("correctionForm") AttendanceCorrectionForm correctionForm);
	
	/**
	 * 訂正申請承認後(確認者更新)
	 * @param confirmer
	 * @param id
	 */
	public Integer updateApproveCorrection(@Param("confirmer") String confirmer, @Param("id") Integer id);
	

	/**
	 * 却下理由【訂正申請】×ボタン押下(却下フラグ0に更新)
	 * @param correctionId
	 */
	public void updateRejectFlg(@Param("correctionId") Integer correctionId);
	

}

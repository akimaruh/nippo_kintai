package com.analix.project.mapper;

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
	 * 勤怠訂正登録(既存データがあれば上書き)
	 * @param attendanceCorrection
	 */
	public void registAttendanceCorrection(@Param("correction") AttendanceCorrection correction);

	/**
	 * 訂正申請情報をすべて取得
	 * @return 訂正申請+ユーザー名リスト
	 */
	public List<AttendanceCorrection> findAllAttendanceCorrection();
	
	/**
	 * 訂正idからデータ取得
	 * @param id 訂正id
	 * @return ユーザーId、申請者、対象日付、申請日
	 */
	public AttendanceCorrectionDto findCorrectionDataById(@Param("id") Integer id);
	
	/**
	 * 対象者の訂正申請情報
	 * @param userId
	 * @param date
	 * @return
	 */
//	public AttendanceCorrection findAttendanceByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

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
//	public List<AttendanceCorrection> findRejectedAttendanceByUserIdAndYearMonth(@Param("userId") Integer userId, @Param("yearMonth") YearMonth targetYearMonth);
	
	/**
	 * 訂正申請却下(却下理由、確認者、却下フラグ1に更新)
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
	 * 却下理由【訂正申請】×ボタン押下(却下理由をnullに更新)
	 * @param correctionId
	 */
	public void updateRejectionReason(@Param("correctionId") Integer correctionId);
	

}
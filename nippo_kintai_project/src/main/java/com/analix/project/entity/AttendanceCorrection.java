package com.analix.project.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class AttendanceCorrection {
	
	/** 訂正ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 勤務状況 */
	private Byte status;
	/** 日付 */
	private LocalDate date;
	/** 勤務開始時刻 */
	private LocalTime startTime;
	/** 勤務終了時刻 */
	private LocalTime endTime;
	/** 備考 */
	private String remarks;
	/** 訂正理由 */
	private String correctionReason;
	/** 却下理由 */
	private String rejectionReason;
	/** 訂正申請日 */
	private LocalDate applicationDate;
	/** 確認者 */
	private String confirmer;
	/** 却下フラグ（0:未却下、1:却下） */
	private Byte rejectFlg;
	
	/** 名前 */
	private String userName;

}

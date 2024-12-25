package com.analix.project.dto;

import java.time.LocalDate;
import java.time.YearMonth;

import lombok.Data;

@Data
public class MonthlyAttendanceReqDto {
	
	/** 申請ID */
	private Integer id;
	/** 申請者のユーザーID */
	private Integer userId;
	/** 申請対象年月 */
	private LocalDate targetYearMonth;
	/** 申請日 */
	private LocalDate date;
	/** ステータス */
	private Integer status;
	/** 氏名 */
	private String name;
	/** 却下理由 */
	private String comment;
	
	/** メール */
//	private String email;
	
//	private String role;

	
	// フォーマット済み日付フィールド
	private YearMonth formattedYearMonth; // yyyy-MM
	private String YearMonthStr; // yyyy/MM 表示用
	
	private String formattedDate; // yyyy/MM/dd 表示用

	
	
}

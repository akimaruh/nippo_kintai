package com.analix.project.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AttendanceCorrectionDto {

	/** ユーザーID */
	private Integer userId;
	/** 名前 */
	private String userName;
	/** 日付 */
	private LocalDate date;
	/** 訂正申請日 */
	private LocalDate applicationDate;
	
	// フォーマット済み日付フィールド
    private String formattedDate;
    private String formattedApplicationDate;
}

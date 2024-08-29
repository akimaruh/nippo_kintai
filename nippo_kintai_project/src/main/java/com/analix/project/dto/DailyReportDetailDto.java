package com.analix.project.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class DailyReportDetailDto {
	/*シーケンス*/
	private Integer id;
	/*ユーザID*/
	private Integer userId;
	/*日付*/
	private LocalDate date;
	/*時間*/
	private Integer time;
	/*内容*/
	private String content;

}

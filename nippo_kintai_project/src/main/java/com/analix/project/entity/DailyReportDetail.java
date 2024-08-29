package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyReportDetail {
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

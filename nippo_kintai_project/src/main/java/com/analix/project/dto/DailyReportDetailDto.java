package com.analix.project.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class DailyReportDetailDto {
	/*シーケンス*/
	private Integer reportDetailId;
	/*ユーザID*/
	private Integer userId;
	/*日付*/
	private LocalDate date;
	/*作業ID*/
	private Integer workId;
	/*作業名*/
	private String workName;
	/*時間*/
	private Integer time;
	/*内容*/
	private String content;

}

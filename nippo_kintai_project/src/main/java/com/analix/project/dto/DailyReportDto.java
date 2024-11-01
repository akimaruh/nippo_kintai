package com.analix.project.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
@Data
public class DailyReportDto {
	/*日報ID*/
	private Integer reportId;
	/*ユーザID*/
	private Integer userId;
	/*日付*/
	private LocalDate date;
	/*ステータス*/
	private Integer status;
	/*日報詳細*/
	private List<DailyReportDetailDto> dailyReportDetailDtoList;
	
	/*ユーザ名*/
	private String userName;
	/*1日の作業時間*/
	private Integer timePerDay;

}

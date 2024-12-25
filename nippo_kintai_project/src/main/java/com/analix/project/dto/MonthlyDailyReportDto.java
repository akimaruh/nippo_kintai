package com.analix.project.dto;

import java.util.List;

import lombok.Data;

@Data
public class MonthlyDailyReportDto {
	/*1カ月の日報リスト*/
	private List<DailyReportDto> dailyReportDtoList;
	/*日報サマリ*/
	private DailyReportSummaryDto dailyReportSummaryDto;

}

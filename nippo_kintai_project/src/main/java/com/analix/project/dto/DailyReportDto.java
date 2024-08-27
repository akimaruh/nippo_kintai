package com.analix.project.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
@Data
public class DailyReportDto {
	/*日報ID*/
	private Integer id;
	/*ユーザID*/
	private Integer userId;
	/*日付*/
	private LocalDate date;
	/*ステータス*/
	private Integer status;
	/*日報詳細*/
	private List<DailyReportDetailDto> dailyReportDetailDtoList;

}

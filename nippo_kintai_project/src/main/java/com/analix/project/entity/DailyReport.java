package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyReport {

	/*日報ID*/
	private Integer id;
	/*ユーザID*/
	private Integer userId;
	/*日付*/
	private LocalDate date;
	/*ステータス*/
	private Integer status;

}

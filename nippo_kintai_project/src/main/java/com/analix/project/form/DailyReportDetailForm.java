package com.analix.project.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DailyReportDetailForm {
	
	private Integer id;
	private Integer userId;
	private LocalDate date;

	@Max(value = 99, message = "2桁で入力して下さい")
	private Float time;

	@Size(max = 50, message = "50文字以内で入力して下さい")
	private String content;

}

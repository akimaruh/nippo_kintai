package com.analix.project.form;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Valid
@Data
public class DailyReportDetailForm {
	
	private Integer id;
	private Integer userId;
	private LocalDate date;
	@Digits(integer = 2,fraction = 0 ,message = "1以上100未満の整数を入力して下さい")
	@Min(value = 1, message="1以上100未満の整数を入力して下さい")
	private Integer time;

	@Size(max = 20,message = "20文字以内で入力して下さい")
	private String content;

}

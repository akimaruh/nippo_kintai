package com.analix.project.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Valid
public class WorkScheduleForm {
	
	private Integer userId;
	@NotNull(message = "出勤時間は必須です。")
	private Integer startTimeHour;
	@NotNull(message = "出勤時間は必須です。")
	private Integer startTimeMinute;
	@NotNull(message = "退勤時間は必須です。")
	private Integer endTimeHour;
	@NotNull(message = "退勤時間は必須です。")
	private Integer endTimeMinute;
	
	@Size(max = 3, message="休憩時間は3桁以内で入力してください。")
	@Pattern(regexp = "^[0-9]*$", message = "休憩時間は数字で入力してください。")
	@NotBlank(message = "休憩時間は必須です。")
	private String breakTime;
	
}

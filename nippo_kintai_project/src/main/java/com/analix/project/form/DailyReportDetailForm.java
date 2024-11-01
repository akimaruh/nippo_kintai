package com.analix.project.form;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Valid
@Data
public class DailyReportDetailForm {

	private Integer id;
	private Integer userId;
	private LocalDate date;
	@NotNull(message = "必須です", groups = { StartMenuDailyReportGroup.class })
	private Integer workId;
	@NotNull(message = "必須です", groups = { StartMenuDailyReportGroup.class })
	//Integerの許容外の数値エラー対策のためtimeはString型で受け取る
	@Max(value =24,message = "1以上24未満の整数を入力して下さい", groups = { DailyReportGroup.class,
			StartMenuDailyReportGroup.class })
	@Min(value = 1, message = "1以上24未満の整数を入力して下さい", groups = { DailyReportGroup.class,
			StartMenuDailyReportGroup.class })
	private String time;
	@NotBlank(message = "必須です", groups = { StartMenuDailyReportGroup.class })
	@Size(max = 20, message = "20文字以内で入力して下さい", groups = { DailyReportGroup.class, StartMenuDailyReportGroup.class })
	private String content;

	/*作業名*/
	private String workName;

}

package com.analix.project.form;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Data

public class DailyReportForm {
	private Integer id;
	private Integer userId;
	private LocalDate date;
	@Valid
	private List<DailyReportDetailForm> dailyReportFormDetailList;
}
